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

import com.addthis.basis.util.LessBytes;
import com.addthis.basis.util.LessStrings;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.Bundles;
import com.addthis.bundle.core.TestBundle;
import com.addthis.bundle.core.kvp.KVBundle;
import com.addthis.bundle.core.list.ListBundle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
    public void roundTripKV() throws Exception {
        singleRoundTripTest(new ListBundle(), new KVBundle());
    }

    @Test
    public void roundTripList() throws Exception {
        singleRoundTripTest(new ListBundle(), new ListBundle());
    }


    @Test
    public void roundTripKV2List() throws Exception {
        singleRoundTripTest(new KVBundle(), new ListBundle());
    }

    @Test
    public void roundTripList2KV() throws Exception {
        singleRoundTripTest(new ListBundle(), new KVBundle());
    }

    @Test
    public void multiTest() throws Exception {
        Bundle b = TestBundle.testBundle(new KVBundle(), new String[][]{
                new String[]{"abc", "def"},
                new String[]{"ghi", "jkl"},
                new String[]{"mno", "pqr"},
        }, false);
        Bundle b2 = DataChannelCodec.decodeBundle(new KVBundle(), DataChannelCodec.encodeBundle(b));
        paranoidAssertBundlesEqual(b, b2);

        b = TestBundle.testBundle(new KVBundle(), new String[][]{
                new String[]{"123", "def"},
                new String[]{"456", "jkl"},
                new String[]{"789", "pqr"},
        }, false);
        b2 = DataChannelCodec.decodeBundle(new KVBundle(), DataChannelCodec.encodeBundle(b));
        //System.out.println(Strings.printable(DataChannelCodec.encodeBundle(b)));
        paranoidAssertBundlesEqual(b, b2);
    }


    @Test
    public void multiTest2() throws Exception {
        Bundle b = TestBundle.testBundle(new KVBundle(), new String[][]{
                new String[]{"abc", "def"},
                new String[]{"ghi", "jkl"},
                new String[]{"mno", "pqr"},
        }, false);
        Bundle b2 = DataChannelCodec.decodeBundle(new ListBundle(), DataChannelCodec.encodeBundle(b));
        paranoidAssertBundlesEqual(b, b2);

        b = TestBundle.testBundle(new ListBundle(), new String[][]{
                new String[]{"123", "def"},
                new String[]{"456", "jkl"},
                new String[]{"789", "pqr"},
        }, false);
        b2 = DataChannelCodec.decodeBundle(new KVBundle(), DataChannelCodec.encodeBundle(b));
        paranoidAssertBundlesEqual(b, b2);
    }

}
