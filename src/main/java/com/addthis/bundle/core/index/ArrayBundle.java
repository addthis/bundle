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

import com.addthis.bundle.core.IndexBundle;
import com.addthis.bundle.core.IndexFormat;
import com.addthis.bundle.value.ValueObject;

public class ArrayBundle implements IndexBundle {

    final ValueObject[] valueArray;
    final IndexFormat format;

    public ArrayBundle(IndexFormat format) {
        this.format = format;
        this.valueArray = new ValueObject[format.getFieldCount()];
    }

    @Override
    public String toString() {
        return Arrays.toString(valueArray);
    }

    @Override
    public ValueObject value(int index) {
        return valueArray[index];
    }

    @Override
    public void value(int index, ValueObject value) {
        valueArray[index] = value;
    }

    @Override
    public IndexFormat format() {
        return format;
    }
}
