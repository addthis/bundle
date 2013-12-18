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

import com.addthis.basis.kv.KVPair;
import com.addthis.basis.util.Bytes;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.kvp.KVBundle;
import com.addthis.bundle.core.kvp.KVBundleFormat;
import com.addthis.bundle.channel.DataChannelSource;
import com.addthis.bundle.io.chunk.ChunkSource;
import com.addthis.bundle.value.ValueFactory;

public class KVChannelSource implements DataChannelSource {

    private final KVBundleFormat format;
    private final boolean binary;
    private final ChunkSource in;
    private Bundle peek;

    /**
     * takes a kv input stream in binary or url encoded newline separated format
     *
     * @param in
     * @param binary
     */
    public KVChannelSource(ChunkSource in, boolean binary) {
        this.in = in;
        this.binary = binary;
        this.format = new KVBundleFormat();
    }

    public KVBundleFormat getFormat() {
        return format;
    }

    @Override
    public void close() {
        in.close();
    }

    @Override
    public Bundle next() {
        Bundle ret;
        if (peek != null) {
            ret = peek;
            peek = null;
            return ret;
        }
        try {
            byte next[] = in.next();
            if (next == null) {
                return null;
            }
            if (binary) {
                return fromBin(next, format);
            } else {
                return fromText(next, format);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Bundle peek() {
        if (peek == null) {
            peek = next();
        }
        return peek;
    }

    /**
     * ported from code in KVPairs.class
     */
    public static KVBundle fromText(byte text[], KVBundleFormat format) {
        if (text.length == 0) {
            return null;
        }
        KVBundle bundle = new KVBundle(format);
        String s = Bytes.toString(text);
        int i = 0;
        int j = s.indexOf('&');
        while (j >= 0) {
            if (j > 0) {
                KVPair kv = KVPair.parsePair(s.substring(i, j));
                if (kv != null) {
                    bundle.setValue(format.getField(kv.getKey()), ValueFactory.create(kv.getValue()));
                }
            }
            i = j + 1;
            j = s.indexOf('&', i);
        }
        KVPair kv = KVPair.parsePair(s.substring(i));
        if (kv != null) {
            bundle.setValue(format.getField(kv.getKey()), ValueFactory.create(kv.getValue()));
        }
        return bundle;
    }

    /**
     * copied from KVPairs.class and modified
     */
    public static KVBundle fromBin(byte bin[], KVBundleFormat format) {
        KVBundle bundle = new KVBundle(format);
        int pos = 0;
        while (pos < bin.length) {
            String key = null;
            String val = null;
            for (int i = pos; i < bin.length; i++) {
                if (bin[i] == 0) {
                    key = new String(bin, pos, i - pos);
                    pos = i + 1;
                    break;
                }
                if (i == bin.length - 1) {
                    key = new String(bin, pos, i - pos + 1);
                    pos = i + 1;
                    break;
                }
            }
            for (int i = pos; i < bin.length; i++) {
                if (bin[i] == 0) {
                    val = new String(bin, pos, i - pos);
                    pos = i + 1;
                    break;
                }
                if (i == bin.length - 1) {
                    val = new String(bin, pos, i - pos + 1);
                    pos = i + 1;
                    break;
                }
            }
            bundle.setValue(format.getField(key), ValueFactory.create(val));
        }
        return bundle;
    }
}
