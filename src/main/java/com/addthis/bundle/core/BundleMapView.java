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
package com.addthis.bundle.core;

import javax.annotation.Nullable;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.addthis.bundle.value.ValueObject;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Predicates.equalTo;

public class BundleMapView<E extends Bundle> implements Map<BundleField, ValueObject> {
    private static final Logger log = LoggerFactory.getLogger(BundleMapView.class);

    protected final E bundle;

    public BundleMapView(E bundle) {
        this.bundle = bundle;
    }

    @Override public int size() {
        return bundle.getCount();
    }

    @Override public boolean isEmpty() {
        return size() == 0;
    }

    @Override public boolean containsKey(Object key) {
        return Iterators.contains(bundle.iterator(), key);
    }

    @Override public boolean containsValue(Object value) {
        return Iterators.contains(Iterators.transform(bundle.iterator(), bundle::getValue), value);
    }

    @Nullable @Override public ValueObject get(Object key) {
        if (key instanceof BundleField) {
            return bundle.getValue((BundleField) key);
        } else {
            return Iterators.tryFind(bundle.iterator(), equalTo(key)).transform(this::get).orNull();
        }
    }

    @Nullable @Override public ValueObject put(BundleField key, ValueObject value) {
        ValueObject previousValue = get(key);
        bundle.setValue(key, value);
        return previousValue;
    }

    @Nullable @Override public ValueObject remove(Object key) {
        ValueObject previousValue = get(key);
        if (key instanceof BundleField) {
            bundle.removeValue((BundleField) key);
        } else {
            BundleField equivalentField = Iterators.find(bundle.iterator(), equalTo(key), null);
            if (equivalentField != null) {
                bundle.removeValue(equivalentField);
            }
        }
        return previousValue;
    }

    @Override public void putAll(Map<? extends BundleField, ? extends ValueObject> m) {
        m.forEach(this::put);
    }

    @Override public void clear() {
        bundle.iterator().forEachRemaining(bundle::removeValue);
    }

    @Override public Set<BundleField> keySet() {
        return new AbstractSet<BundleField>() {
            @Override public Iterator<BundleField> iterator() {
                return bundle.iterator();
            }

            @Override public int size() {
                return BundleMapView.this.size();
            }
        };
    }

    @Override public Collection<ValueObject> values() {
        return Collections2.transform(keySet(), bundle::getValue);
    }

    @Override public Set<Map.Entry<BundleField, ValueObject>> entrySet() {
        return Maps.asMap(keySet(), bundle::getValue).entrySet();
    }
}
