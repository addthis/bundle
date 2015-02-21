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

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.value.ValueObject;
import com.addthis.codec.annotations.Pluggable;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Pluggable("value-field")
public interface AutoField {

    @Nullable public ValueObject getValue(Bundle bundle);

    public void setValue(Bundle bundle, @Nullable ValueObject value);

    public void removeValue(Bundle bundle);

    default Optional<String> getString(Bundle bundle) {
        ValueObject valueObject = getValue(bundle);
        if (valueObject == null) {
            return Optional.empty();
        }
        return Optional.of(valueObject.asString().asNative());
    }

    default OptionalDouble getDouble(Bundle bundle) {
        ValueObject valueObject = getValue(bundle);
        if (valueObject == null) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(ValueUtil.asNumberOrParse(valueObject).asDouble().asNative());
    }

    default OptionalLong getLong(Bundle bundle) {
        ValueObject valueObject = getValue(bundle);
        if (valueObject == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(ValueUtil.asNumberOrParse(valueObject).asLong().asNative());
    }

    default OptionalInt getInt(Bundle bundle) {
        ValueObject valueObject = getValue(bundle);
        if (valueObject == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(ValueUtil.asNumberOrParse(valueObject).asLong().asNative().intValue());
    }


    // static construction and deserialization utilities

    static final CharMatcher FIELD_NAME_DELIMITER = CharMatcher.anyOf("./");
    static final Splitter FIELD_NAME_SPLITTER = Splitter.on(FIELD_NAME_DELIMITER)
                                                        .trimResults()
                                                        .omitEmptyStrings();

    @JsonCreator
    public static AutoField newAutoField(@JsonProperty("path") String path,
                                         @JsonProperty("parseIndex") boolean parseIndex,
                                         @JsonProperty("pathAsList") List<String> pathAsList) {
        if (path == null) {
            return newAutoField(pathAsList, parseIndex);
        }
        if (pathAsList == null) {
            return newAutoField(path, parseIndex);
        }
        throw new IllegalArgumentException("path and pathAsList are mutually exclusive -- one must be null");
    }

    @JsonCreator
    public static AutoField newAutoField(String nameOrJoinedArray) {
        return newAutoField(nameOrJoinedArray, false);
    }

    public static AutoField newAutoField(String nameOrJoinedArray, boolean parseIndex) {
        checkNotNull(nameOrJoinedArray);
        if (FIELD_NAME_DELIMITER.matchesAnyOf(nameOrJoinedArray)) {
            return newAutoField(FIELD_NAME_SPLITTER.splitToList(nameOrJoinedArray), parseIndex);
        } else if (parseIndex) {
            return new IndexField(nameOrJoinedArray);
        } else {
            return new CachingField(nameOrJoinedArray);
        }
    }

    public static AutoField newAutoField(List<String> names, boolean parseIndex) {
        checkNotNull(names);
        checkArgument(!names.isEmpty(), "list of field names must not be empty (usually >=2)");
        String name = names.get(0);
        AutoField baseAutoField;
        if (parseIndex) {
            baseAutoField = new IndexField(name);
        } else {
            baseAutoField = new CachingField(name);
        }
        if (names.size() == 1) {
            return baseAutoField;
        } else {
            String[] subNames = names.subList(1, names.size()).toArray(new String[names.size() - 1]);
            return new FullAutoField(baseAutoField, subNames);
        }
    }
}
