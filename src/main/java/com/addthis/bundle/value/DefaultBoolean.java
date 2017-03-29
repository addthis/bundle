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
package com.addthis.bundle.value;

import com.addthis.bundle.util.ValueUtil;

import com.fasterxml.jackson.annotation.JsonCreator;

public class DefaultBoolean implements ValueBoolean {

    private final boolean value;

    @JsonCreator
    public DefaultBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultBoolean)) {
            return false;
        }
        DefaultBoolean other = (DefaultBoolean)obj;
        return this.value == other.value;
    }

    @Override
    public TYPE getObjectType() {
        return TYPE.BOOLEAN;
    }

    @Override
    public Boolean asNative() {
        return value;
    }

    @Override
    public ValueBoolean asBoolean() {
        return this;
    }

    @Override
    public ValueArray asArray() {
        return ValueUtil.asArray(this);
    }

    @Override
    public ValueString asString() {
        return ValueFactory.create(String.valueOf(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    @Override
    public boolean getBoolean() {
        return value;
    }
}
