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

import com.addthis.basis.util.Bytes;

import com.google.common.primitives.Doubles;

public final class DefaultDouble implements ValueDouble {

    protected DefaultDouble(double value) {
        this.value = value;
    }

    private double value;

    @Override
    public int hashCode() {
        return Doubles.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultDouble)) {
            return false;
        }
        return ((DefaultDouble) o).value == value;
    }

    @Override
    public ValueNumber avg(int count) {
        return new DefaultDouble(value / Math.max(count, 1));
    }

    @Override
    public ValueNumber diff(ValueNumber val) {
        return new DefaultDouble(value - val.asDouble().getDouble());
    }

    @Override
    public ValueNumber max(ValueNumber val) {
        return new DefaultDouble(Math.max(value, val.asDouble().getDouble()));
    }

    @Override
    public ValueNumber min(ValueNumber val) {
        return value > 0 ? new DefaultDouble(Math.min(value, val.asDouble().getDouble())) : val.asDouble();
    }

    @Override
    public ValueNumber sum(ValueNumber val) {
        return new DefaultDouble(value + (val != null ? val.asDouble().getDouble() : 0));
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public TYPE getObjectType() {
        return TYPE.FLOAT;
    }

    @Override
    public ValueBytes asBytes() throws ValueTranslationException {
        return ValueFactory.create(Bytes.toBytes(Double.doubleToLongBits(value)));
    }

    @Override
    public ValueArray asArray() throws ValueTranslationException {
        ValueArray arr = ValueFactory.createArray(1);
        arr.append(this);
        return arr;
    }

    @Override
    public ValueMap asMap() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueNumber asNumber() throws ValueTranslationException {
        return this;
    }

    @Override
    public ValueLong asLong() throws ValueTranslationException {
        return ValueFactory.create((long) value);
    }

    @Override
    public ValueDouble asDouble() throws ValueTranslationException {
        return this;
    }

    @Override
    public ValueString asString() throws ValueTranslationException {
        return ValueFactory.create(Double.toString(value));
    }

    @Override
    public Double getDouble() {
        return value;
    }

    @Override
    public void setDouble(Double val) {
        this.value = val;
    }

    @Override
    public ValueCustom asCustom() throws ValueTranslationException {
        throw new ValueTranslationException();
    }
}
