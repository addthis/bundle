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

import java.util.Iterator;

import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueMapEntry;
import com.addthis.bundle.value.ValueObject;
import com.addthis.bundle.value.ValueString;

@SuppressWarnings("unused")
public class BundlePrinter {

    private static String printValueObject(ValueObject valueObject) {
        if (valueObject == null) {
            return "null";
        } else if (valueObject instanceof ValueString) {
            return '"' + valueObject.toString() + '"';
        } else if (valueObject instanceof ValueArray) {
            ValueArray asArray = (ValueArray) valueObject;
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            for (int i = 0; i < asArray.size(); i++) {
                builder.append(printValueObject(asArray.get(i)));
                if (i < asArray.size() - 1) {
                    builder.append(" , ");
                }
            }
            builder.append(']');
            return builder.toString();
        } else if (valueObject instanceof ValueMap) {
            ValueMap asMap = (ValueMap) valueObject;
            StringBuilder builder = new StringBuilder();
            builder.append('{');
            Iterator<ValueMapEntry> iterator = asMap.iterator();
            while (iterator.hasNext()) {
                ValueMapEntry entry = iterator.next();
                builder.append('"');
                builder.append(entry.getKey());
                builder.append("\" : ");
                builder.append(printValueObject(entry.getValue()));
                if (iterator.hasNext()) {
                    builder.append(" , ");
                }
            }
            builder.append('}');
            return builder.toString();
        } else {
            return valueObject.toString();
        }
    }

    public static String printBundle(Bundle bundle) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        Iterator<BundleField> iterator = bundle.iterator();
        while (iterator.hasNext()) {
            BundleField field = iterator.next();
            String fieldName = field.getName();
            ValueObject value = bundle.getValue(field);
            String formatValue = printValueObject(value);
            builder.append("\"");
            builder.append(fieldName);
            builder.append("\" = ");
            builder.append(formatValue);
            if (iterator.hasNext()) {
                builder.append(" , ");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    // inefficient but simple method for truncating bundle string (primarily to avoid log-spamming in debug output)
    public static String printBundle(Bundle bundle, int limit) {
        return printBundle(bundle).substring(0, limit);
    }
}
