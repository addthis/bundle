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
package com.addthis.bundle.value;

import java.io.IOException;

import java.util.Collections;

import com.addthis.codec.jackson.Jackson;

import com.google.common.collect.ImmutableList;

import com.typesafe.config.ConfigValueFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueDeserializerTest {

    @Test
    public void mapCreation() throws IOException {
        ValueMap<?> map = Jackson.defaultCodec().decodeObject(
                ValueMap.class, ConfigValueFactory.fromMap(Collections.singletonMap("hello", 12)));
        ValueLong valueLong = (ValueLong) map.get("hello");
        assertEquals(12, valueLong.getLong());
    }

    @Test
    public void arrayCreation() throws IOException {
        ValueArray array = Jackson.defaultCodec().decodeObject(
                ValueArray.class, ConfigValueFactory.fromAnyRef(ImmutableList.of("hey", "friend")));
        ValueString string = (ValueString) array.get(0);
        assertEquals("hey", string.asNative());
    }

    @Test
    public void stringCreation() throws IOException {
        ValueString string = Jackson.defaultCodec().decodeObject(
                ValueString.class, ConfigValueFactory.fromAnyRef("heyo"));
        assertEquals("heyo", string.asNative());
    }
}