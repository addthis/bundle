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

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.core.Bundles;
import com.addthis.bundle.core.list.ListBundleFormat;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.codec.config.Configs;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class IndexFieldTest {
    private static final Logger log = LoggerFactory.getLogger(IndexFieldTest.class);

    private static Bundle makeListBundleWithOrder() throws Exception {
        BundleFormat bundleFormat = new ListBundleFormat();
        bundleFormat.getField("a");
        bundleFormat.getField("b");
        bundleFormat.getField("c");
        bundleFormat.getField("0");
        Bundle bundle = bundleFormat.createBundle();
        Bundles.shallowCopyBundle(Bundles.decode("a = hi, b = there, c = {}, 0 = 44"), bundle);
        return bundle;
    }

    @Test
    public void accessByIndex() throws Exception {
        Bundle bundle = makeListBundleWithOrder();
        AutoField field = Configs.decodeObject(AutoField.class, "index = 1");
        assertEquals("there", field.getValue(bundle).asNative());
    }

    @Test
    public void indexThenNested() throws Exception {
        Bundle bundle = makeListBundleWithOrder();
        AutoField writeField = Configs.decodeObject(AutoField.class, "auto = c.sub-field");
        writeField.setValue(bundle, ValueFactory.create("sub-value"));
        AutoField field = Configs.decodeObject(AutoField.class, "index = 2/sub-field");
        assertEquals("sub-value", field.getValue(bundle).asNative());
    }
}