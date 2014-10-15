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
package com.addthis.bundle.util.map;

import javax.annotation.Nonnull;
import javax.annotation.Syntax;

import java.io.IOException;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleException;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.util.ValueObjectToString;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueDouble;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;
import com.addthis.codec.config.Configs;

import com.google.common.annotations.VisibleForTesting;

import com.fasterxml.jackson.annotation.JsonCreator;

@VisibleForTesting
public class MapBundle extends TreeMap<String, String> implements Bundle, BundleFormat {

    public MapBundle() { super(); }
    public MapBundle(Map<String, String> map) { super(map); }

    @Override
    public BundleFormat getFormat() {
        return this;
    }

    @Override
    public ValueObject getValue(BundleField field) throws BundleException {
        return ValueFactory.create(get(field.getName()));
    }

    @Override
    public void setValue(BundleField field, ValueObject value) {
        if (value != null) {
            put(field.getName(), valueToString(value));
        }
    }

    private static String valueToString(@Nonnull ValueObject value) {
        if (value instanceof ValueArray) {
            return ValueObjectToString.valueArrayToString((ValueArray) value);
        } else if ((value instanceof ValueDouble)
                   && (value.asDouble().getDouble() == (double) value.asLong().getLong())) {
            return value.asLong().asString().toString();
        } else {
            return value.asString().toString();
        }
    }

    @Override
    public Iterator<BundleField> iterator() {
        return new MapBundleFieldIterator(this.keySet().iterator());
    }

    @Override
    public void removeValue(BundleField field) throws BundleException {
        this.remove(field.getName());
    }

    @Override
    public int getCount() {
        return this.size();
    }

    @Override public BundleField getField(String name) {
        return new MapBundleField(name);
    }

    @Override public boolean hasField(String name) {
        return this.containsKey(name);
    }

    @Override public Object getVersion() {
        return this;
    }

    @Override public BundleField getField(int pos) {
        return null;
    }

    @Override public int getFieldCount() {
        return getCount();
    }

    @Override public Bundle createBundle() {
        return new MapBundle();
    }

    /* Creates a map from an even number of String tokens in the pattern key, value, key, value. */
    public static MapBundle createBundle(String... pairs) {
        MapBundle bundle = new MapBundle();
        for (int i = 0; i < (pairs.length - 1); i += 2) {
            bundle.put(pairs[i], pairs[i + 1]);
        }
        return bundle;
    }

    /* Creates a Map that implements Bundle for easy testing. */
    @JsonCreator public static MapBundle fromMap(Map<String, String> map) {
        return new MapBundle(map);
    }

    public static MapBundle decode(@Syntax("HOCON") String config) throws IOException {
        return Configs.decodeObject(MapBundle.class, config);
    }
}
