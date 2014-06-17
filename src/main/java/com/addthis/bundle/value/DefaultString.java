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

import java.util.Objects;

import com.addthis.basis.util.Bytes;

import com.addthis.bundle.util.ValueUtil;

public class DefaultString implements ValueString {

    private final String value;

    public DefaultString(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        String other = (obj != null) ? obj.toString() : null;
        String self  = this.toString();
        return Objects.equals(self, other);
    }

    @Override
    public TYPE getObjectType() {
        return TYPE.STRING;
    }

    @Override
    public ValueArray asArray() {
        return ValueUtil.asArray(this);
    }

    @Override
    public ValueMap asMap() {
        throw new ValueTranslationException();
    }

    @Override
    public ValueNumber asNumber() {
        return value == null ? new DefaultLong(0) : value.indexOf(".") >= 0 ? asDouble() : asLong();
    }

    @Override
    public ValueLong asLong() {
        try {
            return ValueFactory.create(Long.parseLong(value));
        } catch (Exception ex) {
            throw new ValueTranslationException(ex);
        }
    }

    @Override
    public ValueDouble asDouble() {
        try {
            return ValueFactory.create(Double.parseDouble(value));
        } catch (Exception ex) {
            throw new ValueTranslationException(ex);
        }
    }

    @Override
    public ValueString asString() {
        return this;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public ValueBytes asBytes() throws ValueTranslationException {
        try {
            return ValueFactory.create(Bytes.toBytes(value));
        } catch (Exception ex) {
            throw new ValueTranslationException(ex);
        }
    }

    @Override
    public ValueCustom asCustom() throws ValueTranslationException {
        throw new ValueTranslationException();
    }
}
