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

public class HashValueMap<V> extends HashMap<String, ValueObject<V>> implements ValueMap<V> {

    public HashValueMap() { super(); }
    public HashValueMap(int initialCapacity) { super(initialCapacity); }
    public HashValueMap(Map<String, ? extends ValueObject<V>> m) { super(m); }

    @Override
    public TYPE getObjectType() {
        return TYPE.MAP;
    }

    @Override
    public Map<String, V> asNative() {
        return Maps.transformValues(this, AsNative.<V>getInstance());
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
    public Numeric<Map<String, ValueObject<V>>> asNumeric() throws ValueTranslationException {
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
    public Iterator<ValueMapEntry<V>> iterator() {
        return new Iterator<ValueMapEntry<V>>() {
            private final Iterator<Map.Entry<String, ValueObject<V>>> iter =
                    HashValueMap.super.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public ValueMapEntry<V> next() {
                return new ValueMapEntry<V>() {
                    final Map.Entry<String, ValueObject<V>> next = iter.next();

                    @Override
                    public String getKey() {
                        return next.getKey();
                    }

                    @Override
                    public ValueObject<V> getValue() {
                        return next.getValue();
                    }

                    @Override
                    public ValueObject<V> setValue(ValueObject<V> val) {
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
    public ValueCustom<Map<String, V>> asCustom() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

}
