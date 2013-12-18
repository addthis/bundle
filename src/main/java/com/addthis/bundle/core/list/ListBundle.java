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
import java.util.NoSuchElementException;

import com.addthis.basis.util.Parameter;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleException;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;

public class ListBundle implements Bundle {

    private static final boolean debugToString = Parameter.boolValue("bundle.list.verbose", false);
    private static final ValueObject skip = ValueFactory.create("__skip__");

    private final List<ValueObject> bundle;
    private final ListBundleFormat format;
    private int count;

    public ListBundle() {
        this(new ListBundleFormat(), 1);
    }

    public ListBundle(ListBundleFormat format) {
        this(format, format.getFieldCount());
    }

    public ListBundle(ListBundleFormat format, int size) {
        this.format = format;
        this.bundle = createList(size);
    }

    @Override
    public String toString() {
        if (!debugToString) {
            return bundle.toString();
        }
        StringBuilder sb = new StringBuilder();
        for (BundleField field : format) {
            if (field.getIndex() >= bundle.size()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(field.getName()).append("=").append(bundle.get(field.getIndex()));
        }
        return sb.toString();
    }

    /**
     * override this in subclasses to use another list type
     */
    protected List<ValueObject> createList(int size) {
        return new ArrayList<ValueObject>(size);
    }

    @Override
    public Iterator<BundleField> iterator() {
        return new Iterator<BundleField>() {
            private final Iterator<BundleField> iter = format.iterator();
            private BundleField peek;

            @Override
            public boolean hasNext() {
                while (peek == null && iter.hasNext()) {
                    BundleField next = iter.next();
                    ValueObject value = getRawValue(next);
                    if (value == skip) {
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
                throw new UnsupportedOperationException();
            }

        };
    }

    @Override
    public ValueObject getValue(BundleField field) throws BundleException {
        ValueObject value = getRawValue(field);
        if (value == skip) {
            value = null;
        }
        return value;
    }

    private ValueObject getRawValue(BundleField field) throws BundleException {
        if (field != null) {
            Integer index = field.getIndex();
            if (index != null && index < bundle.size()) {
                return bundle.get(index);
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
                    bundle.add(skip);
                }
                ValueObject prev = bundle.set(index, value);
                if (prev == skip) {
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
        set(field, skip);
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
}
