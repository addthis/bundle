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
package com.addthis.bundle.core.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleException;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.core.Bundles;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ListBundle implements Bundle {

    //marker object for fields in the format but not in this bundle to allow lazy consistency
    private static final ValueObject SKIP = ValueFactory.create("__skip__");

    private final List<ValueObject> bundle;
    private final ListBundleFormat format;
    private int count;

    public ListBundle() {
        this(new ListBundleFormat(), 1);
    }

    @JsonCreator
    public ListBundle(Map<String, ValueObject> valueMap) {
        this(new ListBundleFormat(), valueMap.size());
        for (Map.Entry<String, ValueObject> entry : valueMap.entrySet()) {
            BundleField field = this.format.getField(entry.getKey());
            set(field, entry.getValue());
        }
    }

    public ListBundle(ListBundleFormat format) {
        this(format, format.getFieldCount());
    }

    public ListBundle(ListBundleFormat format, int size) {
        this(format, new ArrayList<ValueObject>(size));
    }

    public ListBundle (ListBundleFormat format, List<ValueObject> list) {
        this.format = format;
        this.bundle = list;
    }

    @Override
    public String toString() {
        return Bundles.toJsonString(this);
    }

    @Override
    public Iterator<BundleField> iterator() {
        return new Iterator<BundleField>() {
            private final Iterator<BundleField> iter = format.iterator();
            private BundleField peek = null;

            @Override
            public boolean hasNext() {
                while (peek == null && iter.hasNext()) {
                    BundleField next = iter.next();
                    ValueObject value = getRawValue(next);
                    if (value == SKIP) {
                        continue;
                    }
                    peek = next;
                    break;
                }
                return peek != null;
            }

            @Override
            public BundleField next() {
                if (hasNext()) {
                    BundleField next = peek;
                    peek = null;
                    return next;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Field iterator is read-only");
            }

        };
    }

    @Override
    public ValueObject getValue(BundleField field) throws BundleException {
        ValueObject value = getRawValue(field);
        if (value == SKIP) {
            return null;
        }
        return value;
    }

    private ValueObject getRawValue(BundleField field) throws BundleException {
        if (field != null) {
            Integer index = field.getIndex();
            if (index < bundle.size()) {
                return bundle.get(index);
            } else {
                return SKIP;
            }
        }
        return null;
    }

    @Override
    public void setValue(BundleField field, ValueObject value) throws BundleException {
        set(field, value);
    }

    private void set(BundleField field, ValueObject value) throws BundleException {
        if (field != null) {
            Integer index = field.getIndex();
            if (index != null) {
                while (bundle.size() <= index) {
                    bundle.add(SKIP);
                }
                ValueObject prev = bundle.set(index, value);
                if (prev == SKIP) {
                    count++;
                }
            }
        }
    }

    public int size() {
        return bundle.size();
    }

    @Override
    public void removeValue(BundleField field) throws BundleException {
        set(field, SKIP);
        count--;
    }

    @Override
    public BundleFormat getFormat() {
        return format;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Bundle createBundle() {
        return new ListBundle(format);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ListBundle)) {
            return false;
        }
        ListBundle other = (ListBundle) obj;
        return this.bundle.equals(other.bundle);
    }
}
