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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormatted;
import com.addthis.bundle.value.ValueObject;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A convenience class for codable objects that might otherwise have to implement
 * their own bundle field binding and caching. It might be nice to expand to options
 * for different native type conversions and inter-value object type conversions.
 *
 * When using CodecConfig, you would simply replace the String fields with AutoField
 * fields of the same name, and then use the AutoField object to get, set, remove values
 * and so on.
 */
@ThreadSafe
public class CachingField implements AutoField {

    @Nonnull public final String name;

    public CachingField(@Nonnull String name) {
        this.name = checkNotNull(name);
    }

    @Override @Nullable public ValueObject getValue(Bundle bundle) {
        BundleField field = checkAndGet(bundle);
        return bundle.getValue(field);
    }

    @Override public void setValue(Bundle bundle, @Nullable ValueObject value) {
        BundleField field = checkAndGet(bundle);
        bundle.setValue(field, value);
    }

    @Override public void removeValue(Bundle bundle) {
        BundleField field = checkAndGet(bundle);
        bundle.removeValue(field);
    }

    /* always check before using and always copy to a local variable */
    private transient BundleField cachedField;

    private BundleField checkAndGet(BundleFormatted bundle) {
        BundleField currentField = cachedField;
        if ((currentField != null)
            && (currentField.getIndex() != null) /* kv bundles ruin everything */
            && (bundle.getFormat().getField(currentField.getIndex()) == currentField)) {
            return currentField;
        } else {
            BundleField newField = bundle.getFormat().getField(name);
            cachedField = newField;
            return newField;
        }
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
                      .add("name", name)
                      .add("cachedField", cachedField)
                      .toString();
    }

    @Override public boolean isConstant() { return false; }
}
