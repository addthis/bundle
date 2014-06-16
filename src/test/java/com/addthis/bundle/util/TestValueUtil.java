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
package com.addthis.bundle.util;

import com.addthis.bundle.value.DefaultString;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueMap;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestValueUtil {

    @Test
    public void testIsDeeplyEqual() {
        assertTrue(ValueUtil.isDeeplyEqual(ValueFactory.create("foo"), ValueFactory.create("foo")));
        assertTrue(ValueUtil.isDeeplyEqual(new DefaultString(null), new DefaultString(null)));
        assertFalse(ValueUtil.isDeeplyEqual(ValueFactory.create("foo"), new DefaultString(null)));
        assertFalse(ValueUtil.isDeeplyEqual(ValueFactory.create("foo"), ValueFactory.create("bar")));
        assertTrue(ValueUtil.isDeeplyEqual(ValueFactory.create(1), ValueFactory.create(1)));
        assertFalse(ValueUtil.isDeeplyEqual(ValueFactory.create(1), ValueFactory.create(0)));
        assertTrue(ValueUtil.isDeeplyEqual(ValueFactory.create(2.0), ValueFactory.create(2.0)));
        assertFalse(ValueUtil.isDeeplyEqual(ValueFactory.create(1.0), ValueFactory.create(2.0)));
        ValueArray a1 = ValueFactory.createArray(3);
        ValueArray a2 = ValueFactory.createArray(3);
        assertTrue(ValueUtil.isDeeplyEqual(a1, a2));
        a1.add(ValueFactory.create("foo"));
        a1.add(ValueFactory.create("bar"));
        a1.add(ValueFactory.create("baz"));
        a2.add(ValueFactory.create("foo"));
        a2.add(ValueFactory.create("bar"));
        assertFalse(ValueUtil.isDeeplyEqual(a1, a2));
        a2.add(ValueFactory.create("baz"));
        assertTrue(ValueUtil.isDeeplyEqual(a1, a2));
        ValueMap m1 = ValueFactory.createMap();
        ValueMap m2 = ValueFactory.createMap();
        assertTrue(ValueUtil.isDeeplyEqual(m1, m2));
        m1.put("foo", ValueFactory.create(1));
        m2.put("foo", ValueFactory.create(1));
        assertTrue(ValueUtil.isDeeplyEqual(m1, m2));
        m2.remove("foo");
        m2.put("bar", ValueFactory.create(1));
        assertFalse(ValueUtil.isDeeplyEqual(m1, m2));
    }

}
