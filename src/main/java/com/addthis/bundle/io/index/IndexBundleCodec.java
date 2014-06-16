/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addthis.bundle.io.index;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.addthis.bundle.core.index.ExtIndexBundle;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueCustom;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueMapEntry;
import com.addthis.bundle.value.ValueObject;
import com.addthis.bundle.value.ValueType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;

/**
 * clean version of DataChannelCodec that uses ByteBufs and nameless fields (index bundles)
 *
 * bundles must be encoded and decoded with the same number of columns
 */
public final class IndexBundleCodec {

    private static final Logger log = LoggerFactory.getLogger(IndexBundleCodec.class);

    private static enum TYPE {
        NULL, STRING, BYTES, LONG, LONG_NEG, LONG_BIG,
        DOUBLE, ARRAY, MAP, CUSTOM_INDEX, CUSTOM_CLASS
    }

    private static final TYPE[] TYPES = TYPE.values();

    private final List<Class<? extends ValueObject>> classList = new ArrayList<>(0);

    public void encodeBundle(ExtIndexBundle row, ByteBuf out) throws IOException {
        for (ValueObject value : row) {
            encodeValue(value, out);
        }
    }

    public void encodeValue(ValueObject val, ByteBuf out) throws IOException {
        if (val == null) {
            out.writeByte(TYPE.NULL.ordinal());
            return;
        }
        ValueType objectType = val.getObjectType();
        switch (objectType) {
            case CUSTOM:
                ValueCustom custom = val.asCustom();
                Class<? extends ValueObject> type = custom.getContainerClass();
                int classIndex = classList.indexOf(type);
                if (classIndex == -1) {
                    classIndex = classList.size();
                    classList.add(type);
                    out.writeByte(TYPE.CUSTOM_CLASS.ordinal());
                    ByteBufs.writeLength(classIndex, out);
                    ByteBufs.writeString(type.getName(), out);
                } else {
                    out.writeByte(TYPE.CUSTOM_INDEX.ordinal());
                    ByteBufs.writeLength(classIndex, out);
                }
                encodeValue(custom.asMap(), out);
                break;
            case MAP:
                ValueMap map = val.asMap();
                out.writeByte(TYPE.MAP.ordinal());
                ByteBufs.writeLength(map.size(), out);
                for (ValueMapEntry e : map) {
                    encodeValue(ValueFactory.create(e.getKey()), out);
                    encodeValue(e.getValue(), out);
                }
                break;
            case ARRAY:
                out.writeByte(TYPE.ARRAY.ordinal());
                ValueArray arr = val.asArray();
                ByteBufs.writeLength(arr.size(), out);
                for (ValueObject vo : arr) {
                    encodeValue(vo, out);
                }
                break;
            case STRING:
                out.writeByte(TYPE.STRING.ordinal());
                ByteBufs.writeString(val.asString().getString(), out);
                break;
            case BYTES:
                out.writeByte(TYPE.BYTES.ordinal());
                ByteBufs.writeBytes(val.asBytes().getBytes(), out);
                break;
            case INT:
                long lv = val.asLong().getLong();
                // over 2^48, direct bytes are more efficient
                if (lv > 281474976710656L) {
                    out.writeByte(TYPE.LONG_BIG.ordinal());
                    out.writeLong(lv);
                    break;
                }
                if (lv >= 0) {
                    out.writeByte(TYPE.LONG.ordinal());
                } else {
                    out.writeByte(TYPE.LONG_NEG.ordinal());
                    lv = -lv;
                }
                ByteBufs.writeLength(lv, out);
                break;
            case FLOAT:
                long dv = Double.doubleToLongBits(val.asDouble().getDouble());
                out.writeByte(TYPE.DOUBLE.ordinal());
                out.writeLong(dv);
                break;
            default:
                log.error("Unknown object type {}", objectType);
                throw new IOException("Unknown object type " + objectType);
        }
    }

    public ExtIndexBundle decodeBundle(ExtIndexBundle bundle, ByteBuf in) throws IOException {
        for (int i = 0; i < bundle.format().size(); i++) {
            ValueObject nextValue = decodeValue(in);
            bundle.value(i, nextValue);
        }
        return bundle;
    }

    public ValueObject decodeValue(ByteBuf in) throws IOException {
        byte typeInteger = in.readByte();
        TYPE type = TYPES[(int) typeInteger];
        switch (type) {
            case NULL:
                return null;
            case BYTES:
                return ValueFactory.create(ByteBufs.readBytes(in));
            case LONG:
                return ValueFactory.create(ByteBufs.readLength(in));
            case LONG_NEG:
                return ValueFactory.create(-ByteBufs.readLength(in));
            case LONG_BIG:
                return ValueFactory.create(in.readLong());
            case DOUBLE:
                return ValueFactory.create(Double.longBitsToDouble(in.readLong()));
            case STRING:
                return ValueFactory.create(ByteBufs.readString(in));
            case ARRAY:
                int len = (int) ByteBufs.readLength(in);
                ValueArray arr = ValueFactory.createArray(len);
                for (int i = 0; i < len; i++) {
                    arr.add(decodeValue(in));
                }
                return arr;
            case MAP:
                ValueMap map = ValueFactory.createMap();
                long count = ByteBufs.readLength(in);
                while (count-- > 0) {
                    ValueObject key = decodeValue(in);
                    ValueObject val = decodeValue(in);
                    map.put(key.toString(), val);
                }
                return map;
            case CUSTOM_INDEX:
                Class<? extends ValueObject> ci = classList.get((int) ByteBufs.readLength(in));
                return rehydrate(ci, in);
            case CUSTOM_CLASS:
                int index = (int) ByteBufs.readLength(in);
                Class<? extends ValueObject> cc;
                try {
                    cc = (Class<? extends ValueObject>) Class.forName(ByteBufs.readString(in));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (classList.size() != index) {
                    throw new RuntimeException("index conflict for " + cc + " @ " + index);
                }
                classList.add(cc);
                return rehydrate(cc, in);
            default:
                throw new RuntimeException("invalid decode type " + type);
        }
    }

    private ValueCustom rehydrate(Class<? extends ValueObject> cc, ByteBuf in) {
        try {
            ValueCustom vc = cc.newInstance().asCustom();
            ValueMap map = decodeValue(in).asMap();
            vc.setValues(map);
            return vc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
