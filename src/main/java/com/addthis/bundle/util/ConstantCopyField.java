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
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantCopyField extends ConstantField {

    @JsonCreator
    public ConstantCopyField(ValueObject value) {
        super(value);
    }

    @Nullable @Override public ValueObject getValue(Bundle bundle) {
        return ValueFactory.copyValue(super.getValue(bundle));
    }
}
