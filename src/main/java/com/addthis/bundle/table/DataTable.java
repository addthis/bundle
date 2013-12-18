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

import java.util.Comparator;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleFactory;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.core.BundleFormatted;

public interface DataTable extends Iterable<Bundle>, BundleFactory, BundleFormatted {

    /** */
    public BundleFormat getFormat();

    /**
     * @return number of rows
     */
    public int size();

    /**
     * @param rownum row index in table
     * @return row value from table
     */
    public Bundle get(int rownum);

    /**
     * @param rownum row index in table
     * @param row    new row value
     * @return old row value
     */
    public Bundle set(int rownum, Bundle row);

    /**
     * @param index to remove
     * @return old row value from table
     */
    public Bundle remove(int index);

    /**
     * @param index to remove
     * @param row   new row to add
     */
    public void insert(int index, Bundle row);

    /**
     * @param row append row as last in table
     */
    public void append(Bundle row);

    /**
     * @param table append another table. more efficient in DBL.
     */
    public void append(DataTable table);

    /**
     * sort list with given comparator
     */
    public void sort(Comparator<Bundle> comp);
}
