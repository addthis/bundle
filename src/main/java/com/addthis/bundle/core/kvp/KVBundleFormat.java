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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;

/** */
public class KVBundleFormat implements BundleFormat {

    private final HashMap<String, KVBundleField> fields = new LinkedHashMap<>();
    private Object version = new Object();

    @Override
    public String toString() {
        return fields.toString();
    }

    @Override
    public Object getVersion() {
        return version;
    }

    @Override
    public boolean hasField(String name) {
        return fields.get(name) != null;
    }

    @Override
    public BundleField getField(String name) {
        KVBundleField field = fields.get(name);
        if (field == null) {
            field = new KVBundleField(name);
            fields.put(name, field);
            version = new Object();
        }
        return field;
    }

    /** */
    private static class KVBundleField implements BundleField {

        private final String name;

        KVBundleField(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Integer getIndex() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            KVBundleField that = (KVBundleField) o;

            if (name != null ? !name.equals(that.name) : that.name != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    @Override
    public Iterator<BundleField> iterator() {
        return new Iterator<BundleField>() {
            final Iterator<KVBundleField> iter = fields.values().iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public BundleField next() {
                return iter.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public BundleField getField(int pos) {
        for (KVBundleField field : fields.values()) {
            if (pos-- == 0) {
                return field;
            }
        }
        return null;
    }

    @Override
    public int getFieldCount() {
        return fields.size();
    }
}
