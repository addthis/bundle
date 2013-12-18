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
package com.addthis.bundle.core.kvp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.addthis.basis.kv.KVPairs;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleException;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.value.ValueObject;

/**
 * Bundle wrapper around KVPairs
 */
public final class KVBundle implements Bundle {

    private final LinkedHashMap<String, ValueObject> values = new LinkedHashMap<String, ValueObject>();
    private final KVBundleFormat format;

    public KVBundle() {
        this(new KVBundleFormat());
    }

    public KVBundle(KVBundleFormat format) {
        this.format = format;
    }

    @Override
    public String toString() {
        KVPairs kv = new KVPairs();
        for (Entry<String, ValueObject> e : values.entrySet()) {
            kv.addValue(e.getKey(), e.getValue().toString());
        }
        return kv.toString();
    }

    @Override
    public BundleFormat getFormat() {
        return format;
    }

    @Override
    public ValueObject getValue(BundleField field) throws BundleException {
        return values.get(field.getName());
    }

    @Override
    public void setValue(BundleField field, ValueObject value) throws BundleException {
        if (value != null) {
            values.put(field.getName(), value);
        } else {
            removeValue(field);
        }
    }

    @Override
    public Iterator<BundleField> iterator() {
        return format.iterator();
    }

    @Override
    public void removeValue(BundleField field) throws BundleException {
        values.remove(field.getName());
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Bundle createBundle() {
        return new KVBundle(format);
    }
}
