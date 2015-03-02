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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * translation exceptions are runtime exceptions since you should
 * protect calls to them with a getObjectType call first.
 */
@JsonDeserialize(using = ValueDeserializer.class)
public interface ValueObject {

    public static enum TYPE {
        STRING, INT, FLOAT, BYTES, ARRAY, MAP, CUSTOM
    }

    public TYPE getObjectType();

    public Object asNative();

    public default ValueBytes asBytes() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    public default ValueArray asArray() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    public default ValueMap asMap() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    default public Numeric asNumeric() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    default public ValueLong asLong() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    default public ValueDouble asDouble() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    default public ValueString asString() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    default public ValueCustom<?> asCustom() throws ValueTranslationException {
        throw new ValueTranslationException();
    }
}
