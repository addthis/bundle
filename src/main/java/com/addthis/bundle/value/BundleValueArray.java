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

import javax.annotation.Nullable;

import java.util.AbstractList;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleValueArray extends AbstractList<ValueObject> implements ValueArray {
    private static final Logger log = LoggerFactory.getLogger(BundleValueArray.class);

    private final Bundle bundle;

    public BundleValueArray(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override public ValueObject get(int index) {
        return bundle.getValue(getField(index));
    }

    @Nullable @Override public ValueObject set(int index, ValueObject value) {
        return bundle.asMap().put(getField(index), value);
    }

    @Nullable @Override public ValueObject remove(int index) {
        return bundle.asMap().remove(getField(index));
    }

    private BundleField getField(int index) {
        BundleField field = bundle.getFormat().getField(index);
        if (field == null) {
            throw new IndexOutOfBoundsException("index: " + index + " size: " + size());
        }
        return field;
    }

    @Override public int size() {
        return bundle.getFormat().getFieldCount();
    }

    @Override public void add(int index, ValueObject valueObject) {
        // ensure format's field count has increased by at least 1 (user of this bundle instance's pov)
        int fieldSuffix = 0;
        int previousSize = size();
        while (size() <= previousSize) {
            bundle.getFormat().getField("_auto_gen_add_" + fieldSuffix);
            fieldSuffix += 1;
        }
        // slide elements to the right one column
        for (int i = previousSize - 1; i > index; i--) {
            set(i + 1, get(i));
        }
        set(index, valueObject);
    }
}
