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
package com.addthis.bundle.core.index;

import java.util.Arrays;

import com.addthis.bundle.value.ValueType;

import com.google.common.base.Objects;

public class ExtendedArrayFormat implements ExtIndexFormat {

    /**
     * for debugging and hinting purposes _only_. Do _NOT_ use like map-bundle field names.
     */
    private final CharSequence[] labels;

    /**
     * for debugging and hinting about value types. provides more options for implementations
     * by reducing their reliance on reified ValueObjects. has applications in serialization
     * optimizations and processing pipeline planning.
     */
    private final ValueType[] types;

    public ExtendedArrayFormat(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be greater than zero");
        }
        this.types  = new ValueType[length];
        this.labels = new CharSequence[length];
    }

    public ExtendedArrayFormat(ValueType[] types, CharSequence[] labels) {
        if (types.length != labels.length) {
            throw new IllegalArgumentException("types array and labels array must be the same size");
        }
        if (types.length < 1) {
            throw new IllegalArgumentException("length must be greater than zero");
        }
        this.types  = types;
        this.labels = labels;
    }

    public ExtendedArrayFormat(ValueType... types) {
        if (types.length < 1) {
            throw new IllegalArgumentException("length must be greater than zero");
        }
        this.types  = types;
        this.labels = new CharSequence[types.length];
    }

    public ExtendedArrayFormat(CharSequence... labels) {
        if (labels.length < 1) {
            throw new IllegalArgumentException("length must be greater than zero");
        }
        this.labels = labels;
        this.types  = new ValueType[labels.length];
    }

    /**
     * This constructor shares the labels field of the base object, but
     * makes a copy of the type array. It is intended to allow formats
     * of the same length and nature to make relatively efficient copy
     * on write changes to the type hinting. eg. For before and after
     * a transformation on some number of columns.
     */
    public ExtendedArrayFormat(ExtendedArrayFormat base) {
        this.labels = base.labels;
        this.types  = new ValueType[labels.length];
        System.arraycopy(base.types, 0, types, 0, types.length);
    }

    @Override
    public CharSequence label(int index) {
        return labels[index];
    }

    @Override
    public void label(int index, CharSequence label) {
        labels[index] = label;
    }

    @Override
    public ValueType type(int index) {
        return types[index];
    }

    @Override
    public void type(int index, ValueType type) {
        types[index] = type;
    }

    @Override
    public ExtIndexBundle newBundle() {
        return new ExtendedArrayBundle(this);
    }

    @Override
    public int size() {
        return types.length;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("length", types.length)
                .add("labels", Arrays.toString(labels))
                .add("types", Arrays.toString(types))
                .toString();
    }

    @Override
    public int getFieldCount() {
        return types.length;
    }
}
