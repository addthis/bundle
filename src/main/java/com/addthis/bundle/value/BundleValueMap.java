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

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.util.ConvertingSetView;

import com.google.common.collect.Iterators;

public class BundleValueMap extends AbstractMap<String, ValueObject> implements ValueMap {

    private final Bundle bundle;

    public BundleValueMap(Bundle bundle) {
        this.bundle = bundle;
    }

    @Nullable @Override public ValueObject put(String key, ValueObject value) {
        return bundle.asMap().put(bundle.getFormat().getField(key), value);
    }

    @Override public int size() {
        return bundle.asMap().size();
    }

    @Override public boolean isEmpty() {
        return bundle.asMap().isEmpty();
    }

    @Override public boolean containsKey(Object key) {
        if (key instanceof String) {
            return bundle.asMap().containsKey(bundle.getFormat().getField((String) key));
        } else {
            return bundle.asMap().containsKey(key);
        }
    }

    @Override public boolean containsValue(Object value) {
        return bundle.asMap().containsValue(value);
    }

    @Nullable @Override public ValueObject get(Object key) {
        if (key instanceof String) {
            return bundle.asMap().get(bundle.getFormat().getField((String) key));
        } else {
            return bundle.asMap().get(key);
        }
    }

    @Override public Iterator<ValueMapEntry> iterator() {
        return Iterators.transform(bundle.iterator(), field -> new ValueMapEntry() {
            @Override public String getKey() {
                return field.getName();
            }

            @Override public ValueObject getValue() {
                return bundle.getValue(field);
            }

            @Override public ValueObject setValue(ValueObject value) {
                ValueObject previousValue = bundle.getValue(field);
                bundle.setValue(field, value);
                return previousValue;
            }
        });
    }

    @Override public Set<String> keySet() {
        return new ConvertingSetView<>(bundle.asMap().keySet(), BundleField::getName, bundle.getFormat()::getField);
    }

    @Override public Set<Map.Entry<String, ValueObject>> entrySet() {
        return new ConvertingSetView<>(bundle.asMap().entrySet(), entry -> new Map.Entry<String, ValueObject>() {
            @Override public String getKey() {
                return entry.getKey().getName();
            }

            @Override public ValueObject getValue() {
                return entry.getValue();
            }

            @Override public ValueObject setValue(ValueObject value) {
                return entry.setValue(value);
            }
        }, entry -> new Map.Entry<BundleField, ValueObject>() {
            @Override public BundleField getKey() {
                return bundle.getFormat().getField(entry.getKey());
            }

            @Override public ValueObject getValue() {
                return entry.getValue();
            }

            @Override public ValueObject setValue(ValueObject value) {
                return entry.setValue(value);
            }
        });
    }
}
