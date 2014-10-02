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

import javax.annotation.Syntax;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.addthis.bundle.util.ValueUtil;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueObject;
import com.addthis.codec.config.Configs;
import com.addthis.maljson.JSONArray;
import com.addthis.maljson.JSONException;
import com.addthis.maljson.JSONObject;

public final class Bundles {
    private Bundles() {}

    public static Bundle decode(@Syntax("HOCON") String config) throws IOException {
        return Configs.decodeObject(Bundle.class, config);
    }

    /**
     * Create a new {@link JSONObject} as if calling the Bundle were a map passed to
     * {@link JSONObject#JSONObject(Map)}. Fields containing {@link ValueArray} and
     * {@link ValueMap} are recursively turned into {@link JSONArray}s and {@link JSONObject}s.
     *
     * The "JSONObject" capitalization scheme is kept to match the class name of the returned
     * object.
     */
    public static JSONObject toJSONObject(Bundle row) {
        JSONObject jsonRow = new JSONObject();
        for (BundleField field : row) {
            ValueObject valueObject = row.getValue(field);
            try {
                jsonRow.put(field.getName(), jsonWrapValue(valueObject));
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
        return jsonRow;
    }

    private static Object jsonWrapValue(ValueObject valueObject) {
        if (valueObject == null) {
            return null;
        } else {
            return JSONObject.wrap(valueObject.asNative());
        }
    }

    /**
     * Return a {@link String} that is a valid JSON representation of the Bundle. Exactly
     * the same as calling {@link #toJSONObject(Bundle)} and then {@link JSONObject#toString()}.
     */
    public static String toJsonString(Bundle row) {
        return toJSONObject(row).toString();
    }

    public static Map<String, String> getAsStringMapSlowly(Bundle b) {
        Map<String, String> map = new HashMap<String, String>();
        for (BundleField bf : b) {
            map.put(bf.getName(), ValueUtil.asNativeString(b.getValue(bf)));
        }
        return map;
    }

    public static Bundle deepCopyBundle(Bundle bundle) {
        return deepCopyBundle(bundle, bundle.createBundle());
    }

    public static Bundle deepCopyBundle(Bundle bundle, Bundle shell) {
        for (BundleField bundleField : bundle.getFormat()) {
            shell.setValue(shell.getFormat().getField(bundleField.getName()), ValueFactory.copyValue(
                    bundle.getValue(bundleField)));
        }
        return shell;
    }

    public static Bundle shallowCopyBundle(Bundle bundle) {
        return shallowCopyBundle(bundle, bundle.createBundle());
    }

    public static Bundle shallowCopyBundle(Bundle bundle, Bundle shell) {
        for (BundleField bundleField : bundle.getFormat()) {
            shell.setValue(shell.getFormat().getField(bundleField.getName()), bundle.getValue(bundleField));
        }
        return shell;
    }

    /**
     * Copies all fields from <code>fromBundle</code> to <code>toBundle</code>,
     * replacing fields with the same name if <code>replace</code> is true. If
     * <code>replace</code> is false, the original value in <code>toBundle</code>
     * will be retained in case of name conflicts.
     * <b>Note: The toBundle field WILL BE MODIFIED by this method!</b> 
     * @param fromBundle source of the fields to copy
     * @param toBundle destination for the copied fields, modified in-place
     * @param replace true to keep toBundle fields in case of conflict, false for fromBundle fields
     * @return the modified <code>toBundle</code>
     */
    public static Bundle addAll(Bundle fromBundle, Bundle toBundle, boolean replace) {
        for (BundleField fromField : fromBundle) {
            // set field if replace is true or if field is not set.
            BundleField toField = toBundle.getFormat().getField(fromField.getName());
            if (replace || toBundle.getValue(toField) == null) {
                toBundle.setValue(toField, fromBundle.getValue(fromField));
            }
        }
        return toBundle;
    }

    /**
     * Copies all fields from <code>fromBundle</code> to <code>toBundle</code>,
     * renaming copied fields using the <code>replaceSuffix</code> if there are conflicts.
     * <b>Note: The toBundle field WILL BE MODIFIED by this method!</b>
     * <b>Note: If used more than once, this method could overwrite previously suffixed conflicting fields.</b>
     * @param fromBundle source of the fields to copy
     * @param toBundle destination for the copied fields, modified in-place
     * @param replaceSuffix suffix to append to name of conflicting copied fields.
     * @return the modified <code>toBundle</code>
     */
    public static Bundle addAll(Bundle fromBundle, Bundle toBundle, String replaceSuffix) {
        for (BundleField fromField : fromBundle) {
            // set field if replace is true or if field is not set.
            String actualSuffix = "";
            BundleField toField = toBundle.getFormat().getField(fromField.getName());
            if (toBundle.getValue(toField) != null) {
                actualSuffix = replaceSuffix;
            }
            toBundle.setValue(toBundle.getFormat().getField(fromField.getName() + actualSuffix), fromBundle.getValue(fromField));
        }
        return toBundle;
    }

    public static boolean equals(Bundle a, Bundle b) {
        if (a.getCount() != b.getCount()) {
            return false;
        }
        for (BundleField aField : a) {
            BundleField bField = b.getFormat().getField(aField.getName());
            if (!Objects.equals(a.getValue(aField), b.getValue(bField))) {
                return false;
            }
        }
        return true;
    }
}
