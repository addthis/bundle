/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.addthis.bundle.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.addthis.bundle.util.ValueUtil;
import com.addthis.bundle.value.ValueFactory;

import com.google.common.collect.Sets;

public class Bundles {

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
            shell.setValue(shell.getFormat().getField(bundleField.getName()), ValueFactory.copyValue(bundle.getValue(bundleField)));
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

    public static Bundle shallowCopyBundle(Bundle fromBundle, Bundle toBundle, boolean replace) {
        for (BundleField fromField : fromBundle) {
            // set field if replace is true or if field is not set.
            BundleField toField = toBundle.getFormat().getField(fromField.getName());
            if (replace || toBundle.getValue(toField) == null) {
                toBundle.setValue(toField, fromBundle.getValue(fromField));
            }
        }
        return toBundle;
    }

    public static Bundle shallowCopyBundle(Bundle fromBundle, Bundle toBundle, String replaceSuffix) {
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
        Set<String> fields = new HashSet<>();
        for (BundleField field : a) {
            fields.add(field.getName());
        }
        for (BundleField field : b) {
            fields.add(field.getName());
        }        
        for (String field : fields) {
            if (!ValueUtil.isDeeplyEqual(a.getValue(a.getFormat().getField(field)),
                    b.getValue(b.getFormat().getField(field)))) {
                return false;
            }
        }
        return true;
    }
}
