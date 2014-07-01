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

import java.util.ArrayList;
import java.util.List;

public class DefaultArray extends ArrayList<ValueObject<?>> implements ValueArray {

    protected DefaultArray(int size) {
        super(size);
    }

    @Override
    public TYPE getObjectType() {
        return TYPE.ARRAY;
    }

    @Override
    public List<ValueObject<?>> asNative() {
        return this;
    }

    @Override
    public ValueBytes asBytes() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueArray asArray() throws ValueTranslationException {
        return this;
    }

    @Override
    public ValueMap<?> asMap() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public Numeric<?> asNumeric() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueLong asLong() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueDouble asDouble() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueString asString() throws ValueTranslationException {
        throw new ValueTranslationException();
    }

    @Override
    public ValueCustom<List<ValueObject<?>>> asCustom() throws ValueTranslationException {
        throw new ValueTranslationException();
    }
}
