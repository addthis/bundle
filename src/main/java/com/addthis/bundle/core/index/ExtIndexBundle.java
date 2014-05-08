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
package com.addthis.bundle.core.index;

import com.addthis.bundle.core.IndexBundle;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueCustom;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueObject;
import com.addthis.bundle.value.ValueString;

import io.netty.buffer.ByteBuf;

/**
 * Non-standard (not implemented or needed by 'Bundle') methods
 * specific to a sub-set of IndexBundle implementations that provide
 * convenience or alternate interfacing.
 *
 * These get/ set methods make it possible for implementations to
 * be more flexible with their storage options, potentially avoid
 * some wrapper object overhead, use different object types without
 * interacting with standard bundles, and hopefully allow more concise
 * and explicit downstream code.
 */
public interface ExtIndexBundle extends IndexBundle, Iterable<ValueObject> {

    ValueString string(int index);
    void string(int index, ValueString string);

    ValueArray array(int index);
    void array(int index, ValueArray array);

    ValueMap map(int index);
    void map(int index, ValueMap map);

    ByteBuf buf(int index);
    void buf(int index, ByteBuf buf);

    <T extends ValueCustom> T custom(int index);
    void custom(int index, ValueCustom custom);

    long integer(int index);
    void integer(int index, long integer);

    double floating(int index);
    void floating(int index, double floating);

    @Override
    public ExtIndexFormat format();
}
