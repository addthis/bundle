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
package com.addthis.bundle.io;

import java.util.List;

import com.addthis.basis.util.LessBytes;
import com.addthis.basis.util.LessStrings;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.Bundles;
import com.addthis.bundle.core.TestBundle;
import com.addthis.bundle.core.list.ListBundle;
import com.addthis.bundle.util.AutoField;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestDataChannelCodec {

    public void singleRoundTripTest(Bundle inB, Bundle outB) throws Exception {
        Bundle b = TestBundle.testBundle(inB, new String[][]{
                new String[]{"abc", "def"},
                new String[]{"ghi", "jkl"},
                new String[]{"mno", "pqr"},
        }, false);
        Bundle b2 = DataChannelCodec.decodeBundle(outB, DataChannelCodec.encodeBundle(b));
        paranoidAssertBundlesEqual(b, b2);
    }


    public void paranoidAssertBundlesEqual(Bundle b1, Bundle b2) throws Exception {
        assertEquals(Bundles.getAsStringMapSlowly(b1), Bundles.getAsStringMapSlowly(b2));
        assertEquals(LessStrings.printable(DataChannelCodec.encodeBundle(b1)),
                LessStrings.printable(DataChannelCodec.encodeBundle(b2)));
        assertTrue(LessBytes.equals(DataChannelCodec.encodeBundle(b1),
                                    DataChannelCodec.encodeBundle(b2)));
    }

    @Test
    public void roundTripList() throws Exception {
        singleRoundTripTest(new ListBundle(), new ListBundle());
    }

    @Test
    public void unicode() throws Exception {
        Bundle input = Bundles.decode("abc : Ах чудна българска земьо полюшвай цъфтящи жита, " +
                       "ghi : Ταχίστη αλώπηξ βαφής ψημένη γη δρασκελίζει υπέρ, " +
                       "mno : νωθρού κυνός");
        Bundle output = DataChannelCodec.decodeBundle(new ListBundle(), DataChannelCodec.encodeBundle(input));
        List<AutoField> fields = ImmutableList.of(AutoField.newAutoField("abc"),
                                                  AutoField.newAutoField("ghi"),
                                                  AutoField.newAutoField("mno"));
        for (AutoField field : fields) {
            assertNotNull(field.getValue(input));
            assertEquals(field.getString(input), field.getString(output));
        }
    }
}
