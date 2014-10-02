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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

public class HashValueMap extends HashMap<String, ValueObject> implements ValueMap {

    public HashValueMap() { super(); }
    public HashValueMap(int initialCapacity) { super(initialCapacity); }
    public HashValueMap(Map<String, ? extends ValueObject> m) { super(m); }

    @Override
    public TYPE getObjectType() {
        return TYPE.MAP;
    }

    @Override
    public Map<String, Object> asNative() {
        return Maps.transformValues(this, AsNative.INSTANCE);
    }

    @Override
    public ValueBytes asBytes() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueArray asArray() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueMap asMap() throws ValueTranslationException {
        return this;
    }

    @Override
    public Numeric asNumeric() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueLong asLong() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueDouble asDouble() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueString asString() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public Iterator<ValueMapEntry> iterator() {
        return new Iterator<ValueMapEntry>() {
            private final Iterator<Map.Entry<String, ValueObject>> iter =
                    HashValueMap.super.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public ValueMapEntry next() {
                return new ValueMapEntry() {
                    final Map.Entry<String, ValueObject> next = iter.next();

                    @Override
                    public String getKey() {
                        return next.getKey();
                    }

                    @Override
                    public ValueObject getValue() {
                        return next.getValue();
                    }

                    @Override
                    public ValueObject setValue(ValueObject val) {
                        return next.setValue(val);
                    }
                };
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public ValueCustom<Map<String, Object>> asCustom() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

}
