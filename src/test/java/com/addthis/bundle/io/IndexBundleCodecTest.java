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

import com.addthis.bundle.core.index.ExtIndexBundle;
import com.addthis.bundle.core.index.ExtIndexFormat;
import com.addthis.bundle.core.index.ExtendedArrayFormat;
import com.addthis.bundle.io.index.ByteBufs;
import com.addthis.bundle.io.index.IndexBundleCodec;
import com.addthis.bundle.value.ValueFactory;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import static org.junit.Assert.assertEquals;

public class IndexBundleCodecTest {

    public static void paranoidAssertBundlesEqual(ExtIndexBundle b1, ExtIndexBundle b2) throws Exception {
        assertEquals(b1.toString(), b2.toString());
        ByteBuf buf1 = ByteBufs.quickAlloc();
        ByteBuf buf2 = ByteBufs.quickAlloc();
        new IndexBundleCodec().encodeBundle(b1, buf1);
        new IndexBundleCodec().encodeBundle(b2, buf2);
        assertEquals(buf1, buf2);
    }

    @Test
    public void roundTrip() throws Exception {
        ExtIndexFormat format = new ExtendedArrayFormat(3);
        ExtIndexBundle bundle1 = format.newBundle();
        bundle1.string(0, ValueFactory.create("heya"));
        bundle1.string(1, ValueFactory.create("there"));
        bundle1.integer(2, 42);
        ExtIndexBundle bundle2 = format.newBundle();
        ByteBuf buf1 = ByteBufs.quickAlloc();
        new IndexBundleCodec().encodeBundle(bundle1, buf1);
        new IndexBundleCodec().decodeBundle(bundle2, buf1);
        paranoidAssertBundlesEqual(bundle1, bundle2);
    }
}
