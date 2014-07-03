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

import java.util.List;
import java.util.Map;

public class ValueFactory {

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
        DefaultMap defaultMap = new DefaultMap();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            defaultMap.put(entry.getKey(), createValueArray(entry.getValue()));
        }

        return defaultMap;
    }

    public static <T> ValueMap<T> createMap() {
        return new DefaultMap<>();
    }

    /**
     * Returns a Value Array from String Array
     *
     * @param array
     * @return
     */
    public static ValueArray createValueArray(List<String> array) {
        ValueArray out = ValueFactory.createArray(array.size());
        for (String val : array) {
            out.add(ValueFactory.create(val));
        }
        return out;
    }

    public static <C extends ValueCustom<T>, T> C createCustom(
            Class<C> clazz, ValueMap map) throws InstantiationException, IllegalAccessException {
        C c = clazz.newInstance();
        c.setValues(map);
        return c;
    }

    public static <T> ValueObject<T> copyValue(ValueObject<T> valueObject) {
        ValueObject newValueObject = null;
        if (valueObject != null) {
            ValueObject.TYPE type = valueObject.getObjectType();
            switch (type) {
                case STRING:
                    newValueObject = ValueFactory.create(valueObject.asString().asNative());
                    break;
                case INT:
                    newValueObject = ValueFactory.create(valueObject.asLong().getLong());
                    break;
                case FLOAT:
                    newValueObject = ValueFactory.create(valueObject.asDouble().getDouble());
                    break;
                case BYTES:
                    newValueObject = ValueFactory.create(valueObject.asBytes().asNative());
                    break;
                case ARRAY:
                    ValueArray valueArray = ValueFactory.createArray(valueObject.asArray().size());
                    for (ValueObject vo : valueObject.asArray()) {
                        valueArray.add(ValueFactory.copyValue(vo));
                    }
                    newValueObject = valueArray;
                    break;
                case MAP:
                    ValueMap valueMap = ValueFactory.createMap();
                    for (ValueMapEntry<?> vo : valueObject.asMap()) {
                        valueMap.put(vo.getKey(), vo.getValue());
                    }
                    newValueObject = valueMap;
                    break;
                case CUSTOM:
                    ValueCustom<T> custom = (ValueCustom<T>) valueObject;
                    try {
                        newValueObject = ValueFactory.createCustom(custom.getClass(), valueObject.asMap());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return newValueObject;
    }
}
