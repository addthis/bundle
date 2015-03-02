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

public abstract class AbstractCustom<T> implements ValueCustom<T> {

    protected T heldObject;

    public AbstractCustom(T objectToHold) {
        this.heldObject = objectToHold;
    }

    @Override
    public TYPE getObjectType() {
        return TYPE.CUSTOM;
    }

    @Override
    public T asNative() {
        return heldObject;
    }

    @Override
    public ValueSimple asSimple() {
        try {
            return asLong();
        } catch (ValueTranslationException ignored) {
        }
        try {
            return asDouble();
        } catch (ValueTranslationException ignored) {
        }
        return asString();
    }

    @Override
    public ValueCustom<T> asCustom() throws ValueTranslationException {
        return this;
    }
}
