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
package com.addthis.bundle.channel.kvp;

import java.io.OutputStream;

import java.util.List;

import com.addthis.basis.kv.KVPairs;
import com.addthis.basis.util.Bytes;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.kvp.KVBundle;
import com.addthis.bundle.core.kvp.KVBundleFormat;
import com.addthis.bundle.channel.DataChannelOutput;

import com.google.common.base.Throwables;

public class KVChannelOutput implements DataChannelOutput {

    private final OutputStream out;
    private final boolean binary;
    private final KVBundleFormat format;

    public KVChannelOutput(OutputStream out, boolean binary) {
        this.out = out;
        this.binary = binary;
        this.format = new KVBundleFormat();
    }

    @Override
    public void send(Bundle row) {
        KVPairs kv = new KVPairs();
        for (BundleField field : row.getFormat()) {
            kv.putValue(field.getName(), row.getValue(field).toString());
        }
        try {
            if (binary) {
                byte[] data = kv.toBinArray();
                Bytes.writeInt(data.length, out);
                out.write(data);
            } else {
                out.write(Bytes.toBytes(kv.toString()));
                out.write('\n');
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void send(List<Bundle> bundles) {
        if (bundles != null && !bundles.isEmpty()) {
            for (Bundle bundle : bundles) {
                send(bundle);
            }
        }
    }

    @Override
    public Bundle createBundle() {
        return new KVBundle(format);
    }

    @Override
    public void sourceError(Throwable cause) {
        Throwables.propagate(cause);
    }

    @Override
    public void sendComplete() {
    }

}
