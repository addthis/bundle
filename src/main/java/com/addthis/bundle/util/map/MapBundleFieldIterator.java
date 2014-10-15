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
package com.addthis.bundle.util.map;

import java.util.Iterator;

import com.addthis.bundle.core.BundleField;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
class MapBundleFieldIterator implements Iterator<BundleField> {

    private final Iterator<String> fields;

    MapBundleFieldIterator(Iterator<String> fields) {
        this.fields = fields;
    }

    @Override public boolean hasNext() {
        return fields.hasNext();
    }

    @Override public BundleField next() {
        if (hasNext()) {
            return new MapBundleField(fields.next());
        } else {
            return null;
        }
    }

    @Override public void remove() {
        fields.remove();
    }
}
