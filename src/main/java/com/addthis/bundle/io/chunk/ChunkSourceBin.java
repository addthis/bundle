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

import java.io.IOException;
import java.io.InputStream;

import com.addthis.basis.util.Bytes;

public class ChunkSourceBin implements ChunkSource {

    private final InputStream in;
    private final boolean varLength;

    public ChunkSourceBin(InputStream in, boolean varLength) {
        this.in = in;
        this.varLength = varLength;
    }

    @Override
    public byte[] next() {
        try {
            if (varLength) {
                return Bytes.readBytes(in);
            } else {
                return Bytes.readBytes(in, Bytes.readInt(in));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
