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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleFactory;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.core.list.ListBundle;
import com.addthis.bundle.core.list.ListBundleFormat;

/**
 * List<Bundle> wrapper that presents a table interface
 */
public class DataTableListWrapper implements DataTable {

    private final BundleFormat format;
    private final List<Bundle> list;
    private final BundleFactory factory;

    public DataTableListWrapper(List<Bundle> list) {
        final ListBundleFormat listFormat = new ListBundleFormat();
        this.format = listFormat;
        this.list = list;
        this.factory = new BundleFactory() {
            @Override
            public Bundle createBundle() {
                return new ListBundle(listFormat);
            }
        };
    }

    public DataTableListWrapper(List<Bundle> list, BundleFactory factory, BundleFormat format) {
        this.factory = factory;
        this.format = format;
        this.list = list;
    }

    /**
     * return list that backs this table
     */
    protected List<Bundle> getBackingList() {
        return list;
    }

    private Bundle ensureCompatibility(Bundle row) {
        if (row.getFormat() != format) {
            Bundle replace = createBundle();
            for (BundleField field : row) {
                replace.setValue(format.getField(field.getName()), row.getValue(field));
            }
            return replace;
        }
        return row;
    }

    @Override
    public void append(Bundle row) {
        list.add(ensureCompatibility(row));
    }

    @Override
    public void append(DataTable result) {
        for (Bundle row : result) {
            append(row);
        }
    }

    @Override
    public void insert(int index, Bundle row) {
        if (index >= list.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        list.add(index, ensureCompatibility(row));
    }

    @Override
    public void sort(Comparator<Bundle> comp) {
        Collections.sort(list, comp);
    }

    @Override
    public Iterator<Bundle> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public Bundle get(int rownum) {
        return rownum < list.size() ? list.get(rownum) : null;
    }

    @Override
    public Bundle set(int rownum, Bundle row) {
        if (rownum >= list.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return rownum < list.size() ? list.set(rownum, ensureCompatibility(row)) : null;
    }

    @Override
    public Bundle remove(int rownum) {
        if (rownum >= list.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return list.remove(rownum);
    }

    @Override
    public Bundle createBundle() {
        return factory.createBundle();
    }

    @Override
    public BundleFormat getFormat() {
        return format;
    }
}
