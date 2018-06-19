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
package com.addthis.bundle.util;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.value.ValueObject;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;

@ThreadSafe
public class IndexField implements AutoField {

    public final int index;

    // we don't use IndexBundleField here because these utility classes favor convenience over correctness and so
    // we would rather not throw an exception if getName is called (eg. from a KVBundle)
    public final BundleField field = new BundleField() {
        @Override public String getName() {
            return String.valueOf(index);
        }

        @Override public Integer getIndex() {
            return index;
        }
    };

    public IndexField(int index) {
        checkArgument(index >= 0);
        this.index = index;
    }

    public IndexField(String indexAsString) {
        this(Integer.parseInt(indexAsString));
    }

    @Override @Nullable public ValueObject getValue(Bundle bundle) {
        return bundle.getValue(field);
    }

    @Override public void setValue(Bundle bundle, @Nullable ValueObject value) {
        bundle.setValue(field, value);
    }

    @Override public void removeValue(Bundle bundle) {
        bundle.removeValue(field);
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                      .add("index", index)
                      .toString();
    }
}
