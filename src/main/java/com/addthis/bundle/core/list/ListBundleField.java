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

import com.addthis.bundle.core.BundleField;

class ListBundleField implements BundleField {

    private final String name;
    private final int pos;

    ListBundleField(String name, int pos) {
        this.name = name;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return pos + "-" + name;
    }

    @Override
    public Integer getIndex() {
        return pos;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ListBundleField that = (ListBundleField) o;

        if (pos != that.pos) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + pos;
        return result;
    }
}
