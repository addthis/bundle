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

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.Bundles;
import com.addthis.bundle.util.AutoField;
import com.addthis.bundle.util.CachingField;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueDeserializerTest {

    @Test
    public void bundleCreation() throws IOException {
        Bundle bundle = Bundles.decode("a = 5, b = hello");
        AutoField a = new CachingField("a");
        AutoField b = new CachingField("b");
        assertEquals(5, a.getValue(bundle).asLong().getLong());
        assertEquals("hello", b.getValue(bundle).asString().asNative());
    }

    @Test
    public void mapCreation() throws IOException {
        ValueMap map = ValueFactory.decodeMap("hello = 12");
        ValueLong valueLong = (ValueLong) map.get("hello");
        assertEquals(12, valueLong.getLong());
    }

    @Test
    public void arrayCreation() throws IOException {
        ValueArray array = ValueFactory.decodeValue("[hey, friend]").asArray();
        ValueObject string = array.get(0);
        assertEquals("hey", string.asNative());
    }

    @Test
    public void stringCreation() throws IOException {
        ValueString string = ValueFactory.decodeValue("heyo").asString();
        assertEquals("heyo", string.asNative());
    }

    @Test
    public void booleanCreation() throws IOException {
        ValueBoolean b = ValueFactory.decodeValue("true").asBoolean();
        assertEquals(true, b.asNative());
    }

}