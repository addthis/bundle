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

import java.io.IOException;
import java.io.OutputStream;

import com.addthis.basis.util.LessBytes;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.io.DataChannelCodec.ClassIndexMap;
import com.addthis.bundle.io.DataChannelCodec.FieldIndexMap;

public class DataChannelWriter implements BundleWriter {

    private final OutputStream out;
    private final ClassIndexMap classMap = DataChannelCodec.createClassIndexMap();
    private final FieldIndexMap fieldMap = DataChannelCodec.createFieldIndexMap();
    private boolean closed = false;

    public DataChannelWriter(OutputStream out) {
        this.out = out;
    }

    @Override public void write(Bundle row) throws IOException {
        LessBytes.writeBytes(DataChannelCodec.encodeBundle(row, fieldMap, classMap), out);
    }

    @Override
    public void close() throws IOException {
        closed = true;
        out.close();
    }

    @Override public boolean isClosed() {
        return closed;
    }
}
