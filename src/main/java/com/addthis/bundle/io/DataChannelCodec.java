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
package com.addthis.bundle.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;

import com.addthis.basis.util.LessBytes;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.table.DataTable;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueCustom;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueMapEntry;
import com.addthis.bundle.value.ValueObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility for creating/consuming binary encodings of DataTable to/from
 * streams.
 * <p/>
 * TODO need a mid-stream dictionary reset detector/switch
 */
public final class DataChannelCodec {

    private static final Logger log = LoggerFactory.getLogger(DataChannelCodec.class);

    private static final HashMap<Integer, TYPE> typeMap = new HashMap<>();
    private static final TYPE bundleTypeInit = TYPE.BUNDLE_INIT;
    private static final TYPE bundleTypeStart = TYPE.BUNDLE_START;
    private static final FieldIndexMap FIMNull = new FIMNull();
    private static final ClassIndexMap CIMNull = new CIMNull();

    private static enum TYPE {
        NULL(0), STRING(1), BYTES(2), LONG(3), LONG_NEG(4), LONG_BIG(5),
        DOUBLE(6), ARRAY(7), MAP(8), CUSTOM_INDEX(9), CUSTOM_CLASS(10),
        BUNDLE_INIT(11), BUNDLE_START(12), BUNDLE_FIELD_INDEX(13), BUNDLE_FIELD_NAME(14), BUNDLE_END(15);

        private final int val;

        TYPE(final int val) {
            this.val = val;
            typeMap.put(val, this);
        }

        @Override
        public String toString() {
            return val + "=" + name();
        }
    }

    /** */
    public static interface ObjectIndexMap<T> {

        /**
         * @return null if no index exists for this class
         */
        public Integer getObjectIndex(T type);

        /**
         * @return index or null if index already exists
         */
        public Integer createObjectIndex(T type);

        /**
         * @return index or null if index already exists
         */
        public Integer setObjectIndex(int index, T type);

        /**
         * @return class registered to this index or null if none
         */
        public T getObject(Integer index);

        /**
         * @return number of entries in map
         */
        public int size();

        /**
         * reset map state
         */
        public void reset();
    }

    public static interface FieldIndexMap extends ObjectIndexMap<BundleField> {

    }

    public static interface ClassIndexMap extends ObjectIndexMap<Class<? extends ValueObject>> {

    }

    /**
     * for non-persisted, transient sends/receives
     */
    public static ClassIndexMap createClassIndexMap() {
        return new CIM();
    }

    /**
     * for non-persisted, transient sends/receives
     */
    public static FieldIndexMap createFieldIndexMap() {
        return new FIM();
    }

    /**
     * for stateless encoding
     */
    public static byte[] encodeBundle(Bundle row) throws IOException {
        return encodeBundle(row, FIMNull, CIMNull);
    }

    /**
     * used in QueryChannelServer
     */
    public static byte[] encodeBundle(Bundle row, FieldIndexMap fieldMap, ClassIndexMap classIndex) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encodeBundle(row, out, fieldMap, classIndex);
        return out.toByteArray();
    }

    /**
     * used in ResultDiskBacked
     */
    public static void encodeBundle(Bundle row, OutputStream out, FieldIndexMap fieldMap, ClassIndexMap classMap) throws IOException {
        if (fieldMap.size() == 0 && classMap.size() == 0) {
            out.write(TYPE.BUNDLE_INIT.val);
        } else {
            out.write(TYPE.BUNDLE_START.val);
        }
        for (BundleField field : row) {
            Integer fieldIndex = fieldMap.getObjectIndex(field);
            if (fieldIndex == null) {
                fieldIndex = fieldMap.createObjectIndex(field);
                out.write(TYPE.BUNDLE_FIELD_NAME.val);
                LessBytes.writeLength(fieldIndex, out);
                LessBytes.writeString(field.getName(), out);
            } else {
                out.write(TYPE.BUNDLE_FIELD_INDEX.val);
                LessBytes.writeLength(fieldIndex, out);
            }
            ValueObject val = row.getValue(field);
            encodeValue(val, out, classMap);
        }
        out.write(TYPE.BUNDLE_END.val);
    }

    /**
     * encode a single value to a stream
     */
    public static void encodeValue(ValueObject val, OutputStream out, ClassIndexMap classIndex) throws IOException {
        if (val == null) {
            out.write(TYPE.NULL.val);
            return;
        }
        ValueObject.TYPE objectType = val.getObjectType();
        switch (objectType) {
            case CUSTOM:
                ValueCustom custom = val.asCustom();
                Class<? extends ValueObject> type = custom.getClass();
                Integer classID = classIndex.getObjectIndex(type);
                if (classID == null) {
                    classID = classIndex.createObjectIndex(type);
                    out.write(TYPE.CUSTOM_CLASS.val);
                    LessBytes.writeLength(classID, out);
                    LessBytes.writeString(type.getName(), out);
                } else {
                    out.write(TYPE.CUSTOM_INDEX.val);
                    LessBytes.writeLength(classID, out);
                }
                encodeValue(custom.asMap(), out, classIndex);
                break;
            case MAP:
                ValueMap map = val.asMap();
                out.write(TYPE.MAP.val);
                LessBytes.writeLength(map.size(), out);
                for (ValueMapEntry e : map) {
                    encodeValue(ValueFactory.create(e.getKey()), out, classIndex);
                    encodeValue(e.getValue(), out, classIndex);
                }
                break;
            case ARRAY:
                out.write(TYPE.ARRAY.val);
                ValueArray arr = val.asArray();
                LessBytes.writeLength(arr.size(), out);
                for (ValueObject vo : arr) {
                    encodeValue(vo, out, classIndex);
                }
                break;
            case STRING:
                out.write(TYPE.STRING.val);
                LessBytes.writeString(val.asString().asNative(), out);
                break;
            case BYTES:
                out.write(TYPE.BYTES.val);
                LessBytes.writeBytes(val.asBytes().asNative(), out);
                break;
            case INT:
                long lv = val.asLong().getLong();
                // over 2^48, direct bytes are more efficient
                if (lv > 281474976710656L) {
                    out.write(TYPE.LONG_BIG.val);
                    LessBytes.writeLong(lv, out);
                    break;
                }
                if (lv >= 0) {
                    out.write(TYPE.LONG.val);
                } else {
                    out.write(TYPE.LONG_NEG.val);
                    lv = -lv;
                }
                LessBytes.writeLength(lv, out);
                break;
            case FLOAT:
                long dv = Double.doubleToLongBits(val.asDouble().getDouble());
                out.write(TYPE.DOUBLE.val);
                LessBytes.writeLong(dv, out);
                break;
            default:
                log.error("Unknown object type " + objectType);
                throw new IOException("Unknown object type " + objectType);
        }
    }

    /**
     * for stateless decoding.  bytes MUST come from a stateless encode.
     */
    public static Bundle decodeBundle(Bundle bundle, byte[] row) throws IOException {
        return decodeBundle(bundle, new ByteArrayInputStream(row), FIMNull, CIMNull);
    }

    /**
     * used in QueryChannelServer
     */
    public static Bundle decodeBundle(Bundle bundle, byte[] row, FieldIndexMap fieldMap, ClassIndexMap classMap) throws IOException {
        return decodeBundle(bundle, new ByteArrayInputStream(row), fieldMap, classMap);
    }

    /**
     * used in ResultDiskBacked
     */
    public static Bundle decodeBundle(Bundle bundle, InputStream in, FieldIndexMap fieldMap, ClassIndexMap classMap) throws IOException {
        int t = in.read();
        if (t < 0) {
            return null;
        }
        TYPE type = typeMap.get(t);
        if (type == bundleTypeInit) {
            // first bundle in a stream or mid-stream for stream appends
            fieldMap.reset();
            classMap.reset();
        } else if (type == bundleTypeStart) {
            // normal for streams after first bundle
        } else {
            throw new IOException("type mismatch (" + t + " = " + type + ") != valid Bundle type");
        }
        int typever = 0;
        try {
            loop:
            while (true) {
                typever = in.read();
                switch (type = typeMap.get(typever)) {
                    case BUNDLE_FIELD_INDEX:
                        int fi = 0;
                        ValueObject vo = null;
                        BundleField field;
                        try {
                            fi = in.read();
                            vo = decodeValue(in, classMap);
                            field = fieldMap.getObject(fi);
                            bundle.setValue(field, vo);
                        } catch (RuntimeException ex) {
                            log.error("field index failure @ " + fi + " val=" + vo);
                            throw ex;
                        }
                        break;
                    case BUNDLE_FIELD_NAME:
                        int index = (int) LessBytes.readLength(in);
                        field = bundle.getFormat().getField(LessBytes.readString(in));
                        fieldMap.setObjectIndex(index, field);
                        bundle.setValue(field, decodeValue(in, classMap));
                        break;
                    case BUNDLE_END:
                        break loop;
                    default:
                        throw new IOException("type mismatch " + type + " not valid bundle field type");
                }
            }
        } catch (RuntimeException ex) {
            String msg = "decode error from " + in + "\n";
            msg += "  typever = " + typever + "\n";
            msg += "  type = " + type + "\n";
            msg += "  bundle = " + bundle + "\n";
            msg += "  fields = " + fieldMap + "\n";
            msg += "  classz = " + classMap + "\n";
            log.error(msg);
            throw ex;
        }
        return bundle;
    }

    public static ValueObject decodeValue(InputStream in, ClassIndexMap classMap) throws IOException {
        int typeInteger = in.read();
        TYPE type = typeMap.get(typeInteger);
        if (type == null) {
            throw new IOException("Null object type, integer code is " + typeInteger);
        }
        switch (type) {
            case NULL:
                return null;
            case BYTES:
                return ValueFactory.create(LessBytes.readBytes(in));
            case LONG:
                return ValueFactory.create(LessBytes.readLength(in));
            case LONG_NEG:
                return ValueFactory.create(-LessBytes.readLength(in));
            case LONG_BIG:
                return ValueFactory.create(LessBytes.readLong(in));
            case DOUBLE:
                return ValueFactory.create(Double.longBitsToDouble(LessBytes.readLong(in)));
            case STRING:
                return ValueFactory.create(LessBytes.readString(in));
            case ARRAY:
                int len = (int) LessBytes.readLength(in);
                ValueArray arr = ValueFactory.createArray(len);
                for (int i = 0; i < len; i++) {
                    arr.add(decodeValue(in, classMap));
                }
                return arr;
            case MAP:
                ValueMap map = ValueFactory.createMap();
                long count = LessBytes.readLength(in);
                while (count-- > 0) {
                    ValueObject key = decodeValue(in, classMap);
                    ValueObject val = decodeValue(in, classMap);
                    map.put(key.toString(), val);
                }
                return map;
            case CUSTOM_INDEX:
                Class<? extends ValueObject> ci = classMap.getObject((int) LessBytes.readLength(in));
                return rehydrate(ci, in, classMap);
            case CUSTOM_CLASS:
                int index = (int) LessBytes.readLength(in);
                Class<? extends ValueObject> cc;
                try {
                    cc = (Class<? extends ValueObject>) Class.forName(LessBytes.readString(in));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (classMap.setObjectIndex(index, cc) == null) {
                    throw new RuntimeException("index conflict for " + cc + " @ " + index);
                }
                return rehydrate(cc, in, classMap);
            default:
                throw new RuntimeException("invalid decode type " + type);
        }
    }

    /** */
    private static ValueCustom rehydrate(Class<? extends ValueObject> cc, InputStream in, ClassIndexMap classMap) {
        try {
            ValueCustom vc = cc.newInstance().asCustom();
            ValueMap map = decodeValue(in, classMap).asMap();
            vc.setValues(map);
            return vc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * encode result to a stream
     */
    public static void toOutputStream(DataTable result, OutputStream out) {
        ClassIndexMap classMap = createClassIndexMap();
        FieldIndexMap fieldMap = createFieldIndexMap();

        try {
            LessBytes.writeLength(result.size(), out);
            for (Bundle row : result) {
                encodeBundle(row, out, fieldMap, classMap);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * encode result to bytes -- helper
     */
    public static byte[] toBytes(DataTable result) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toOutputStream(result, out);
        return out.toByteArray();
    }

    /**
     * decode results from bytes -- helper
     */
    public static void fromBytes(DataTable result, byte[] raw) {
        ByteArrayInputStream in = new ByteArrayInputStream(raw);
        fromInputStream(result, in);
    }

    /**
     * decode result from stream
     */
    public static void fromInputStream(DataTable result, InputStream in) {
        ClassIndexMap classMap = createClassIndexMap();
        FieldIndexMap fieldMap = createFieldIndexMap();

        try {
            long rows = LessBytes.readLength(in);
            while (rows-- > 0) {
                Bundle row = result.createBundle();
                decodeBundle(row, in, fieldMap, classMap);
                result.append(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class OIM<T> implements ObjectIndexMap<T> {

        private final HashMap<Integer, T> indexMap = new HashMap<>();
        private final HashMap<T, Integer> mapIndex = new HashMap<>();

        @Override
        public String toString() {
            return "OIM[" + indexMap.size() + "," + mapIndex.size() + "](im=" + indexMap + ",mi=" + mapIndex + ")";
        }

        @Override
        public void reset() {
            indexMap.clear();
            mapIndex.clear();
        }

        @Override
        public int size() {
            return indexMap.size();
        }

        @Override
        public Integer getObjectIndex(T obj) {
            return mapIndex.get(obj);
        }

        @Override
        public Integer createObjectIndex(T obj) {
            Integer index = mapIndex.get(obj);
            if (index != null) {
                return null;
            }
            index = indexMap.size() + 1;
            indexMap.put(index, obj);
            mapIndex.put(obj, index);
            return index;
        }

        @Override
        public Integer setObjectIndex(int index, T obj) {
            if (indexMap.get(index) != null) {
                return null;
            }
            indexMap.put(index, obj);
            mapIndex.put(obj, index);
            return index;
        }

        @Override
        public T getObject(Integer index) {
            if (index > indexMap.size()) {
                throw new IllegalArgumentException("map index out of bounds : " + index + " > " + indexMap.size());
            }
            return indexMap.get(index);
        }
    }

    /**
     * for stateless mapping
     */
    private static class OIMNull<T> implements ObjectIndexMap<T> {

        @Override
        public void reset() {
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Integer getObjectIndex(T type) {
            return null;
        }

        @Override
        public Integer createObjectIndex(T type) {
            return 0;
        }

        @Override
        public Integer setObjectIndex(int index, T type) {
            return null;
        }

        @Override
        public T getObject(Integer index) {
            return null;
        }
    }

    /** */
    private static final class CIM extends OIM<Class<? extends ValueObject>> implements ClassIndexMap {

    }

    /** */
    private static final class FIM extends OIM<BundleField> implements FieldIndexMap {

    }

    /** */
    private static final class CIMNull extends OIMNull<Class<? extends ValueObject>> implements ClassIndexMap {

    }

    /** */
    private static final class FIMNull extends OIMNull<BundleField> implements FieldIndexMap {

    }
}
