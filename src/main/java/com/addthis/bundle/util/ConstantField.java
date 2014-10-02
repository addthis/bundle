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

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.value.ValueObject;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ConstantField implements AutoField {
    private final ValueObject value;

    @JsonCreator
    public ConstantField(ValueObject value) {
        this.value = value;
    }

    @Nullable @Override public ValueObject getValue(Bundle bundle) {
        return value;
    }

    @Override public void setValue(Bundle bundle, @Nullable ValueObject value) {
        throw new UnsupportedOperationException("cannot meaningfully change a static constant");
    }

    @Override public void removeValue(Bundle bundle) {
        throw new UnsupportedOperationException("cannot meaningfully delete a static constant");
    }
}
