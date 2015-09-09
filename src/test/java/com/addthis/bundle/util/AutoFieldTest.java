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

import java.io.IOException;

import java.util.List;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.Bundles;
import com.addthis.bundle.core.list.ListBundle;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.codec.config.Configs;
import com.addthis.codec.jackson.Jackson;

import com.google.common.collect.Lists;

import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class AutoFieldTest {
    private static final Logger log = LoggerFactory.getLogger(AutoFieldTest.class);

    @Test
    public void creation() throws IOException {
        CachingField autoField = Jackson.defaultMapper().readValue("\"fieldName\"", CachingField.class);
        assertEquals("fieldName", autoField.name);
    }

    @Test
    public void createConstantTyped() throws IOException {
        ConstantTypedField<List<String>> autoField =
                Jackson.defaultMapper().readValue("\"someConst\"", new TypeReference<ConstantTypedField<List<String>>>() {});
        assertEquals(Lists.newArrayList("someConst"), autoField.getValue(null));
    }

    @Test
    public void createDerivedTyped() throws IOException {
        DerivedTypedField<List<String>> autoField =
                Jackson.defaultMapper().readValue("\"someField\"", new TypeReference<DerivedTypedField<List<String>>>() {});
        Bundle bundle = Bundles.decode("someField: [a, b, c]");
        List<String> strings = autoField.getValue(bundle);
        assertEquals(Lists.newArrayList("a", "b", "c"), strings);
    }

    @Test
    public void createAndAccess() throws IOException {
        SimpleCopyFilter filter = createSampleFilter();
        Bundle bundle = new ListBundle();
        setAndFilterBundle(bundle, filter);
    }

    @Test
    public void changingFormats() throws IOException {
        SimpleCopyFilter filter = createSampleFilter();
        Bundle bundle = new ListBundle();
        bundle.getFormat().getField("c");
        setAndFilterBundle(bundle, filter);
        bundle = new ListBundle();
        setAndFilterBundle(bundle, filter);
    }

    protected SimpleCopyFilter createSampleFilter() throws IOException {
        return Configs.decodeObject(SimpleCopyFilter.class, "from = a, to = b");
    }

    protected void setAndFilterBundle(Bundle bundle, SimpleCopyFilter filter) {
        BundleField a = bundle.getFormat().getField("a");
        BundleField b = bundle.getFormat().getField("b");
        bundle.setValue(a, ValueFactory.create("SANDWICH"));
        filter.filter(bundle);
        filter.filter(bundle);
        filter.filter(bundle);
        assertEquals("SANDWICH", bundle.getValue(b).toString());
    }
}