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
import java.io.InputStream;

import com.addthis.basis.util.LessBytes;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleFactory;
import com.addthis.bundle.io.DataChannelCodec.ClassIndexMap;
import com.addthis.bundle.io.DataChannelCodec.FieldIndexMap;

public class DataChannelReader implements BundleReader {

    private final BundleFactory factory;
    private final InputStream in;
    private final ClassIndexMap classMap = DataChannelCodec.createClassIndexMap();
    private final FieldIndexMap fieldMap = DataChannelCodec.createFieldIndexMap();

    public DataChannelReader(BundleFactory factory, InputStream in) {
        this.factory = factory;
        this.in = in;
    }

    @Override public Bundle read() throws IOException {
        return DataChannelCodec.decodeBundle(factory.createBundle(), LessBytes.readBytes(in), fieldMap, classMap);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override public BundleFactory getFactory() {
        return factory;
    }
}
