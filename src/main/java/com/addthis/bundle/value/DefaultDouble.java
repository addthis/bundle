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

import com.fasterxml.jackson.annotation.JsonCreator;

public final class DefaultDouble implements ValueDouble {

    @JsonCreator DefaultDouble(double value) {
        this.value = value;
    }

    private final double value;

    @Override
    public int hashCode() {
        return Doubles.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ValueObject)) {
            return false;
        }
        ValueObject asValueObject = (ValueObject) obj;
        return asValueObject.asDouble().getDouble() == value;
    }

    @Override
    public ValueDouble avg(int count) {
        return new DefaultDouble(value / Math.max(count, 1));
    }

    @Override
    public ValueDouble diff(Numeric val) {
        return new DefaultDouble(value - val.asDouble().getDouble());
    }

    @Override
    public ValueDouble max(Numeric val) {
        return new DefaultDouble(Math.max(value, val.asDouble().getDouble()));
    }

    @Override
    public ValueDouble min(Numeric val) {
        if (value > 0) {
            return new DefaultDouble(Math.min(value, val.asDouble().getDouble()));
        } else {
            return val.asDouble();
        }
    }

    @Override
    public ValueDouble sum(Numeric val) {
        if (val != null) {
            return new DefaultDouble(value + val.asDouble().getDouble());
        } else {
            return new DefaultDouble(value + 0);
        }
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
    public Double asNative() {
        return value;
    }

    @Override
    public ValueBytes asBytes() throws ValueTranslationException {
        return ValueFactory.create(Bytes.toBytes(Double.doubleToLongBits(value)));
    }

    @Override
    public ValueArray asArray() throws ValueTranslationException {
        ValueArray arr = ValueFactory.createArray(1);
        arr.add(this);
        return arr;
    }

    @Override
    public ValueMap asMap() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueDouble asNumeric() throws ValueTranslationException {
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
    public double getDouble() {
        return value;
    }

    @Override
    public ValueCustom<Double> asCustom() throws ValueTranslationException {
        throw new ValueTranslationException();
    }
}
