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
package com.addthis.bundle.table;

import java.util.Iterator;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.util.ValueUtil;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;

import org.junit.Assert;
import org.junit.Test;

public class TestTable {

    public static void testFillTable(DataTable table) {
        testFillTable(table, new String[][]{
                new String[]{"Col-A", "Col-B", "Col-C", "Col-D"},
                new String[]{"mary", "her", "and", "the"},
                new String[]{"had", "fleece", "everywhere", "lamb"},
                new String[]{"a", "was", "that", "was"},
                new String[]{"little", "white", "mary", "sure"},
                new String[]{"lamb", "as", "went", "to"},
                new String[]{null, "snow", null, "follow"},
        });
    }

    private static final int count(Iterable<?> o) {
        int i = 0;
        for (Iterator<?> iter = o.iterator(); iter.hasNext(); iter.next()) {
            i++;
        }
        return i;
    }

    /**
     * @param table empty table to be filled and tested
     * @param data  table as string array of arrays with first array as header
     */
    public static void testFillTable(DataTable table, String[][] data) {
        BundleFormat format = table.getFormat();
        Assert.assertEquals(0, count(format));
        String[] title = data[0];
        BundleField[] cols = new BundleField[title.length];
        for (int i = 0; i < title.length; i++) {
            cols[i] = format.getField(title[i]);
        }
        // insert data
        for (int i = 1; i < data.length; i++) {
            Bundle bundle = table.createBundle();
            String[] row = data[i];
            int nonNull = 0;
            for (int j = 0; j < row.length; j++) {
                bundle.setValue(cols[j], ValueFactory.create(row[j]));
                nonNull += (row[j] != null ? 1 : 0);
            }
            table.append(bundle);
            Assert.assertEquals("row len = " + row.length + " bundle = " + bundle + " nonNull = " + nonNull, row.length, bundle.getCount());
        }
        // check basic stats
        Assert.assertEquals(data.length - 1, table.size());
        Assert.assertEquals(4, count(format));
        // check table data
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            Bundle bundle = table.get(i - 1);
            // check row values knowing column field
            int nonNull = 0;
            for (int j = 0; j < row.length; j++) {
                Assert.assertEquals(row[j], ValueUtil.asNativeString(bundle.getValue(cols[j])));
                nonNull += (row[j] != null ? 1 : 0);
            }
            Assert.assertEquals(row.length, bundle.getCount());
            // check column field ordering matches row's
            int j = 0;
            for (BundleField field : bundle) {
                ValueObject value = bundle.getValue(field);
                Assert.assertEquals(row[j++], ValueUtil.asNativeString(value));
            }
        }
    }

    @Test
    public void testMemArrayTable() {
        testFillTable(new DataTableMemArray(10));
    }

    @Test
    public void testMemLinkedTable() {
        testFillTable(new DataTableMemLinked(10));
    }
}
