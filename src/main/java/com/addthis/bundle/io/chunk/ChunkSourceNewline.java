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
package com.addthis.bundle.io.chunk;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ChunkSourceNewline implements ChunkSource {

    private final BufferedReader in;
    private final ByteArrayOutputStream buf = new ByteArrayOutputStream();

    public ChunkSourceNewline(InputStream in) {
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public byte[] next() {
        buf.reset();
        try {
            String line;
            if ((line = in.readLine()) != null) {
                buf.write(line.getBytes());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return buf.size() > 0 ? buf.toByteArray() : null;
    }

    @Override
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
