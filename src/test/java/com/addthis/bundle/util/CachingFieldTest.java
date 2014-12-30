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
package com.addthis.bundle.util;

import org.junit.Test;

import static com.addthis.codec.config.Configs.decodeObject;

public class CachingFieldTest {

    @Test
    public void deser() throws Exception {
        decodeObject("value-field", "cached: some-field");
        decodeObject(SimpleCopyFilter.class, "from.cached: some-field-a, to: some-field-b");
    }
}