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
package com.addthis.bundle.core.list;

import java.util.Iterator;

import com.addthis.bundle.core.BundleField;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestListBundleFormat {

    @Test
    public void testEmptyFormat() {
        ListBundleFormat format = new ListBundleFormat();
        assertEquals(0, format.getFieldCount());
        assertNull(format.getField(-1));
        assertNull(format.getField(0));
        assertNull(format.getField(1));
    }

    @Test
    public void testCreateNewField() {
        ListBundleFormat format = new ListBundleFormat();
        BundleField field0 = format.createNewField("foo_");
        assertEquals("foo_0", field0.getName());
        assertEquals(0, (int) field0.getIndex());
        BundleField field1 = format.createNewField("foo_");
        assertEquals("foo_1", field1.getName());
        assertEquals(1, (int) field1.getIndex());
    }

    @Test
    public void testCreateNewFieldCollision() {
        ListBundleFormat format = new ListBundleFormat();
        BundleField field0 = format.getField("1");
        assertEquals("1", field0.getName());
        assertEquals(0, (int) field0.getIndex());
        BundleField field1 = format.createNewField("");
        assertEquals("2", field1.getName());
    }

    @Test
    public void testGetField() {
        ListBundleFormat format = new ListBundleFormat();
        BundleField field = format.getField("foo");
        assertEquals("foo", field.getName());
        assertEquals(0, (int) field.getIndex());
        assertEquals(field, format.getField(0));
        assertEquals(1, format.getFieldCount());
        BundleField field2 = format.getField("foo");
        assertEquals(field, field2);
    }

    @Test
    public void testIterator() {
        ListBundleFormat format = new ListBundleFormat();
        format.getField("foo");
        format.getField("bar");
        format.getField("baz");
        format.getField("baz");
        Iterator<BundleField> iterator = format.iterator();
        assertTrue(iterator.hasNext());
        BundleField field = iterator.next();
        assertEquals("foo", field.getName());
        assertEquals(0, (int) field.getIndex());
        assertTrue(iterator.hasNext());
        field = iterator.next();
        assertEquals("bar", field.getName());
        assertEquals(1, (int) field.getIndex());
        assertTrue(iterator.hasNext());
        field = iterator.next();
        assertEquals("baz", field.getName());
        assertEquals(2, (int) field.getIndex());
        assertFalse(iterator.hasNext());
    }

}
