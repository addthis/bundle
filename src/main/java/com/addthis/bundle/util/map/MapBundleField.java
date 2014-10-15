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

import com.addthis.bundle.core.BundleField;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
class MapBundleField implements BundleField {

    private final String name;

    MapBundleField(String name) {
        this.name = name;
    }

    @Override public String getName() {
        return name;
    }

    @Override public Integer getIndex() {
        return null;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MapBundleField)) {
            return false;
        }
        MapBundleField that = (MapBundleField) obj;
        return name.equals(that.name);
    }

    @Override public int hashCode() {
        return name.hashCode();
    }
}
