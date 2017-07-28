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
package com.addthis.bundle.value;

import javax.annotation.Nullable;
import javax.annotation.Syntax;

import java.io.IOException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.addthis.codec.config.Configs;
import com.addthis.codec.jackson.Jackson;

import com.typesafe.config.ConfigFactory;

public final class ValueFactory {
    private ValueFactory() {}

    private static final boolean DEFAULT_TO_SORTED_MAPS =
            ConfigFactory.load().getBoolean("com.addthis.bundle.value.ValueFactory.sorted-maps");

    public static ValueMap decodeMap(@Syntax("HOCON") String config) throws IOException {
        return Configs.decodeObject(ValueMap.class, config);
    }

    public static ValueObject decodeValue(String config) throws IOException {
        return Jackson.defaultCodec().decodeObject(
                ValueObject.class, ConfigFactory.parseString("wrapperValue = " + config).getValue("wrapperValue"));
    }

    public static ValueString create(String val) {
        return val != null ? new DefaultString(val) : null;
    }

    public static ValueBytes create(byte[] val) {
        return val != null ? new DefaultBytes(val) : null;
    }

    public static ValueLong create(long val) {
        return new DefaultLong(val);
    }

    public static ValueDouble create(double val) {
        return new DefaultDouble(val);
    }

    public static ValueArray createArray(int size) {
        return new DefaultArray(size);
    }

    public static ValueMap createMap(Map<String, List<String>> map) {
        ValueMap defaultMap = createMap();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            defaultMap.put(entry.getKey(), createValueArray(entry.getValue()));
        }

        return defaultMap;
    }

    public static ValueMap createMap() {
        if (DEFAULT_TO_SORTED_MAPS) {
            return new TreeValueMap();
        } else {
            return new HashValueMap();
        }
    }

    /** Returns a Value Array from String Array */
    public static ValueArray createValueArray(Collection<String> array) {
        ValueArray out = ValueFactory.createArray(array.size());
        for (String val : array) {
            out.add(ValueFactory.create(val));
        }
        return out;
    }

    public static <C extends ValueCustom<T>, T> C createCustom(Class<C> clazz, ValueMap map)
            throws InstantiationException, IllegalAccessException {
        C c = clazz.newInstance();
        c.setValues(map);
        return c;
    }

    public static ValueObject copyValue(@Nullable ValueObject valueObject) {
        if (valueObject == null) {
            return null;
        }
        ValueObject.TYPE type = valueObject.getObjectType();
        switch (type) {
            // immutable types can simply return themselves
            case STRING: return valueObject;
            case INT:    return valueObject;
            case FLOAT:  return valueObject;
            case BYTES:  return ValueFactory.create(valueObject.asBytes().asNative().clone());
            case ARRAY:
                ValueArray valueArray = ValueFactory.createArray(valueObject.asArray().size());
                for (ValueObject vo : valueObject.asArray()) {
                    valueArray.add(ValueFactory.copyValue(vo));
                }
                return valueArray;
            case MAP:
                ValueMap valueMap = ValueFactory.createMap();
                for (ValueMapEntry vo : valueObject.asMap()) {
                    valueMap.put(vo.getKey(), vo.getValue());
                }
                return valueMap;
            case CUSTOM:
                ValueCustom<?> custom = (ValueCustom<?>) valueObject;
                try {
                    return ValueFactory.createCustom(custom.getClass(), valueObject.asMap());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            default: throw new AssertionError("no other known value object types");
        }
    }
}
