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
import com.addthis.codec.jackson.Jackson;

import com.google.common.base.Objects;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class DerivedTypedField<T> implements TypedField<T> {
    private final AutoField field;
    private final TypeReference<T> type;

    @JsonCreator
    public DerivedTypedField(AutoField field, @JacksonInject TypeReference<T> type) {
        this.field = field;
        this.type = type;
    }

    @Nullable @Override public T getValue(Bundle bundle) {
        ValueObject valueObject = field.getValue(bundle);
        return Jackson.defaultMapper().convertValue(valueObject.asNative(), type);
    }

    @Override public void setValue(Bundle bundle, @Nullable T value) {
        ValueObject convertedValue = Jackson.defaultMapper().convertValue(value, ValueObject.class);
        field.setValue(bundle, convertedValue);
    }

    @Override public void removeValue(Bundle bundle) {
        field.removeValue(bundle);
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
                      .add("field", field)
                      .add("type", type)
                      .toString();
    }

    @Override public boolean isConstant() { return false; }
}
