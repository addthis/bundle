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
package com.addthis.bundle.core.index;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleException;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;

public class IndexBundle implements Bundle {

    //marker object for fields explicitly set to null to allow lazy consistency with the format
    private static final ValueObject NULL = ValueFactory.create("__user_null__");

    private final ValueObject[] bundle;
    private final IndexBundleFormat format;

    private int count;

    public IndexBundle(IndexBundleFormat format) {
        this.format = format;
        this.bundle = new ValueObject[format.length];
    }

    @Override
    public String toString() {
        return Arrays.toString(bundle);
    }

    @Override
    public Iterator<BundleField> iterator() {
        return new Iterator<BundleField>() {
            int index = 0;
            private BundleField peek = null;

            @Override
            public boolean hasNext() {
                while ((peek == null) && (index < bundle.length)) {
                    ValueObject value = bundle[index];
                    index += 1;
                    if (value == null) { // unset field, user nulls are NULL and those are returned
                        continue;
                    }
                    peek = new IndexBundleField(index);
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
        ValueObject value = bundle[field.getIndex()];
        if (value == NULL) {
            return null;
        }
        return value;
    }

    @Override
    public void setValue(BundleField field, ValueObject value) throws BundleException {
        if (value == null) {
            value = NULL;
        }
        rawSet(field.getIndex(), value);
    }

    // convenience methods for Index Bundles
    public void setValue(int index, ValueObject value) throws BundleException {
        if (value == null) {
            rawSet(index, NULL);
        } else {
            rawSet(index, value);
        }
    }

    public void setValue(int index, String value) throws BundleException {
        if (value == null) {
            rawSet(index, NULL);
        } else {
            rawSet(index, ValueFactory.create(value));
        }
    }

    private void rawSet(int index, ValueObject value) throws BundleException {
        ValueObject prev = bundle[index];
        bundle[index] = value;
        if (prev == null) {
            count++;
        }
    }

    @Override
    public void removeValue(BundleField field) throws BundleException {
        rawSet(field.getIndex(), null);
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
        return new IndexBundle(format);
    }
}
