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
package com.addthis.bundle.core;

import com.addthis.bundle.core.list.ListBundle;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBundle {

    public static Bundle fillBundle(Bundle bundle) {
        return testBundle(bundle, new String[][]{
                new String[]{"abc", "def"},
                new String[]{"ghi", "jkl"},
                new String[]{"mno", "pqr"},
        });
    }

    public static Bundle testBundle(Bundle bundle, String[][] kv) {
        return testBundle(bundle, kv, true);
    }

    public static Bundle testBundle(Bundle bundle, String[][] kv, boolean clear) {
        assertEquals(0, bundle.getCount());
        BundleFormat format = bundle.getFormat();
        BundleField[] field = new BundleField[kv.length];
        ValueObject[] value = new ValueObject[kv.length];
        for (int i = 0; i < kv.length; i++) {
            field[i] = format.getField(kv[i][0]);
            value[i] = ValueFactory.create(kv[i][1]);
            bundle.setValue(field[i], value[i]);
        }
        assertEquals(format, bundle.getFormat());
        for (int i = 0; i < kv.length; i++) {
            assertEquals(field[i], format.getField(kv[i][0]));
            assertEquals(value[i], ValueFactory.create(kv[i][1]));
            assertEquals(value[i], bundle.getValue(field[i]));
        }
        assertEquals(kv.length, bundle.getCount());
        if (!clear) {
            return bundle;
        }
        for (int i = 0; i < kv.length; i++) {
            bundle.removeValue(field[i]);
        }
        assertEquals(0, bundle.getCount());
        for (int i = 0; i < kv.length; i++) {
            assertEquals(null, bundle.getValue(field[i]));
        }
        return bundle;
    }

    @Test
    public void testListBundle() {
        fillBundle(new ListBundle());
    }
}
