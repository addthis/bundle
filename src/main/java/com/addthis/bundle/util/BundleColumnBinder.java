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

import java.util.ArrayList;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.core.BundleFormatted;
import com.addthis.bundle.value.ValueObject;

/**
 * bundle to column mapper that re-maps fields to columns if the backing format changes
 */
public class BundleColumnBinder {

    private final String[] fieldNames;
    private BundleField[] fields;
    private Object lastVersion;

    public BundleColumnBinder(BundleFormatted bundle) {
        this(bundle, null);
    }

    public BundleColumnBinder(BundleFormatted bundle, String[] fieldNames) {
        this.fieldNames = fieldNames;
        checkBinding(bundle);
    }

    private Integer getPos(String pos) {
        try {
            return Integer.parseInt(pos);
        } catch (Exception ex) {
            return null;
        }
    }

    private final void checkBinding(BundleFormatted bundle) {
        BundleFormat format = bundle.getFormat();
        Object version = format.getVersion();
        if (version != lastVersion) {
            ArrayList<BundleField> allFields = new ArrayList<BundleField>(format.getFieldCount());
            for (BundleField field : format) {
                allFields.add(field);
            }
            ArrayList<BundleField> newFields = allFields;
            if (fieldNames != null) {
                newFields = new ArrayList<BundleField>(fieldNames.length);
                for (String fieldName : fieldNames) {
                    Integer pos = getPos(fieldName);
                    if (pos != null && pos < allFields.size()) {
                        newFields.add(allFields.get(pos.intValue()));
                    } else {
                        newFields.add(format.getField(fieldName));
                    }
                }
            }
            fields = newFields.toArray(new BundleField[newFields.size()]);
            lastVersion = version;
        }
    }

    public BundleField[] getFields() {
        return fields;
    }

    /** */
    public ValueObject getColumn(Bundle bundle, int column) {
        checkBinding(bundle);
        return column < fields.length ? bundle.getValue(fields[column]) : null;
    }

    /** */
    public void setColumn(Bundle bundle, int column, ValueObject value) {
        checkBinding(bundle);
        if (column >= fields.length) {
            throw new RuntimeException("invalid column");
        }
        bundle.setValue(fields[column], value);
    }

    public void appendColumn(Bundle bundle, ValueObject value) {
        if (value == null) {
            // bundles measure non-null values in getCount()
            // so appending a null cannot append a column
            throw new IllegalArgumentException("cannot append a null column value");
        }
        checkBinding(bundle);
        BundleField field = null;
        if (bundle.getCount() < fields.length) {
            field = fields[bundle.getCount()];
        } else {
            BundleField newFields[] = new BundleField[fields.length + 1];
            System.arraycopy(fields, 0, newFields, 0, fields.length);
            newFields[newFields.length - 1] = bundle.getFormat().getField(Integer.toString(fields.length));
            fields = newFields;
            field = fields[fields.length - 1];
        }
        bundle.setValue(field, value);
    }
}
