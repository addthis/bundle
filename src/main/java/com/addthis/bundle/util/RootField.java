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

import javax.annotation.Nullable;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueObject;

import com.google.common.collect.Sets;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Reads and writes to the entire bundle as if it were a ValueMap or ValueArray. */
public class RootField implements AutoField {
    private static final Logger log = LoggerFactory.getLogger(RootField.class);

    private final boolean force;
    private final boolean array;

    RootField(@JsonProperty("force") boolean force,
              @JsonProperty("array") boolean array) {
        this.force = force;
        this.array = array;
    }

    @Nullable @Override public ValueObject getValue(Bundle bundle) {
        if (array) {
            return bundle.asValueArray();
        } else {
            return bundle.asValueMap();
        }
    }

    @Override public void setValue(Bundle bundle, @Nullable ValueObject value) {
        if (value == null) {
            bundle.asMap().replaceAll((a, b) -> null);
        } else if (array || (value.getObjectType() == ValueObject.TYPE.ARRAY)) {
            ValueArray bundleList = bundle.asValueArray();
            ValueArray valueList  = value.asArray();
            int bundleSize = bundleList.size();
            int valueSize  = valueList.size();
            if (valueSize > bundleSize) {
                bundleList.addAll(bundleSize, valueList.subList(bundleSize, valueSize));
            }
            for (int i = 0; i < Math.min(bundleSize, valueSize); i++) {
                bundleList.set(i, valueList.get(i));
            }
        } else {
            ValueMap bundleMap = bundle.asValueMap();
            ValueMap valueMap  = value.asMap();
            if (!force && !Sets.intersection(bundleMap.keySet(), valueMap.keySet()).isEmpty()) {
                String message = String.format(
                        "Trying to write value map (%s) to bundle root (%s), at least one field already exists and " +
                        "force=false", valueMap, bundle);
                throw new IllegalArgumentException(message);
            }
            bundleMap.putAll(valueMap);
        }
    }

    @Override public void removeValue(Bundle bundle) {
        bundle.asMap().clear();
    }
}
