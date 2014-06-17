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

import java.util.Map;

import com.addthis.bundle.core.kvp.KVBundle;
import com.addthis.bundle.core.list.ListBundle;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestBundles {
    Bundle a;
    Bundle b;
    @Before
    public void setup() {
        a = TestBundle.testBundle(new ListBundle(), new String[][]{
            new String[]{"abc", "def"},
            new String[]{"ghi", "jkl"},
            new String[]{"mno", "pqr"},
        }, false);
        b = TestBundle.testBundle(new ListBundle(), new String[][]{
            new String[]{"zzz", "aaa"},
            new String[]{"yyy", "jkl"},
            new String[]{"abc", "pqr"},
        }, false);
    }
    
    @Test
    public void testGettingStringsSlowly() throws Exception {
        Bundle b = TestBundle.testBundle(new KVBundle(), new String[][]{
                new String[]{"abc", "def"},
                new String[]{"ghi", "jkl"},
                new String[]{"mno", "pqr"},
        }, false);
        Map<String, String> im = ImmutableMap.of("abc", "def", "ghi", "jkl", "mno", "pqr");
        assertEquals(im, Bundles.getAsStringMapSlowly(b));
    }
    
    @Test
    public void testaddAllReplace() {
        Bundle c = Bundles.addAll(a, b, true);
        assertEquals(b, c);
        assertEquals(b.getValue(b.getFormat().getField("abc")), "def");
        assertEquals(b.getValue(b.getFormat().getField("ghi")), "jkl");
        assertEquals(b.getValue(b.getFormat().getField("mno")), "pqr");
        assertEquals(b.getValue(b.getFormat().getField("zzz")), "aaa");
        assertEquals(b.getValue(b.getFormat().getField("yyy")), "jkl");
    }
    
    @Test
    public void testaddAllNoReplace() {
        Bundle c = Bundles.addAll(a, b, false);
        assertEquals(b, c);
        assertEquals(b.getValue(b.getFormat().getField("abc")), "pqr");
        assertEquals(b.getValue(b.getFormat().getField("ghi")), "jkl");
        assertEquals(b.getValue(b.getFormat().getField("mno")), "pqr");
        assertEquals(b.getValue(b.getFormat().getField("zzz")), "aaa");
        assertEquals(b.getValue(b.getFormat().getField("yyy")), "jkl");
    }
    
    @Test
    public void testaddAllReplaceString() {
        Bundle c = Bundles.addAll(a, b, "_new");
        assertEquals(b.getValue(b.getFormat().getField("abc")), "pqr");
        assertEquals(b.getValue(b.getFormat().getField("abc_new")), "def");
        assertEquals(b.getValue(b.getFormat().getField("ghi")), "jkl");
        assertEquals(b.getValue(b.getFormat().getField("mno")), "pqr");
        assertEquals(b.getValue(b.getFormat().getField("zzz")), "aaa");
        assertEquals(b.getValue(b.getFormat().getField("yyy")), "jkl");
    }
    
    @Test
    public void testEquals() {
        Bundle c = TestBundle.testBundle(new ListBundle(), new String[][]{
            new String[]{"zzz", "aaa"},
            new String[]{"yyy", "jkl"},
            new String[]{"abc", "pqr"},
        }, false);
        assertTrue(Bundles.equals(b,c));
        Bundle d = TestBundle.testBundle(new ListBundle(), new String[][]{
            new String[]{"zzz", "aab"},
            new String[]{"yyy", "jkl"},
            new String[]{"abc", "pqr"},
        }, false);
        assertFalse(Bundles.equals(b,d));
        Bundle e = TestBundle.testBundle(new ListBundle(), new String[][]{
            new String[]{"zza", "aaa"},
            new String[]{"yyy", "jkl"},
            new String[]{"abc", "pqr"},
        }, false);
        assertFalse(Bundles.equals(b,e));
    }
}
