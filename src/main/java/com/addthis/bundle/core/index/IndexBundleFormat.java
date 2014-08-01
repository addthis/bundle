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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;

import com.google.common.base.Objects;

/**
 * This class could do some String to Integer and vice versa auto conversion for some of its methods.
 * It does not do so intentionally. Its purpose is to root out such tricks that may cause subtle bugs
 * in downstream code.
 */
public class IndexBundleFormat implements BundleFormat {

    final int length;

    /**
     * for debugging and hinting purposes _only_. Do _NOT_ use
     * like normal field names.
     */
    private final CopyOnWriteArrayList<String> labels;

    public IndexBundleFormat(int length) {
        this.length = length;
        this.labels = null;
    }

    public IndexBundleFormat(String... labels) {
        this.length = labels.length;
        this.labels = new CopyOnWriteArrayList<>(labels);
    }

    public String getLabel(int index) {
        return labels.get(index);
    }

    public String setLabel(int index, String label) {
        return labels.set(index, label);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("length", length)
                .add("labels", labels)
                .toString();
    }

    @Override
    public Object getVersion() {
        return this;
    }

    @Override
    public Iterator<BundleField> iterator() {
        return new Iterator<BundleField>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < length;
            }

            @Override
            public BundleField next() {
                if (hasNext()) {
                    BundleField next = new IndexBundleField(index);
                    index += 1;
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
    public boolean hasField(String name) {
        throw new UnsupportedOperationException("Index Bundle Format does not index fields by Strings");
    }

    @Override
    public BundleField getField(String name) {
        throw new UnsupportedOperationException("Index Bundle Format does not index fields by Strings");
    }

    @Override
    public BundleField getField(int pos) {
        if (pos < length) {
            return new IndexBundleField(pos);
        } else {
            throw new IndexOutOfBoundsException("Index formats use an immutable size to encourage caution");
        }
    }

    @Override
    public int getFieldCount() {
        return length;
    }

    @Override public Bundle createBundle() {
        return new IndexBundle(this);
    }
}
