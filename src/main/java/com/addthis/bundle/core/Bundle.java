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
package com.addthis.bundle.core;

import java.util.Iterator;
import java.util.Map;

import com.addthis.bundle.value.BundleValueArray;
import com.addthis.bundle.value.BundleValueMap;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueObject;
import com.addthis.codec.annotations.Pluggable;

import com.google.common.annotations.Beta;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents one "line" or "packet" of data having multiple fields.
 */
@Pluggable("bundle")
public interface Bundle extends Iterable<BundleField>, BundleFormatted, BundleFactory {

    /** Gets the specified field. */
    public ValueObject getValue(BundleField field);

    /** Looks up a field by name, then gets the specified field.  Prefer the BundleField overload when possible */
    default ValueObject getValue(String fieldName) {
        return this.getValue(this.getFormat().getField(fieldName));
    }

    /** Sets the specified field. */
    public void setValue(BundleField field, ValueObject value);

    /** Looks up a field by name, then sets the specified field.  Prefer the BundleField overload when possible */
    default void setValue(String fieldName, ValueObject value) {
        this.setValue(this.getFormat().getField(fieldName), value);
    }

    /** Removes a field from a bundle. */
    public void removeValue(BundleField field);

    default void removeValue(String fieldName) {
        this.removeValue(this.getFormat().getField(fieldName));
    }

    /** Returns the number of values in this bundle. */
    @JsonIgnore
    public int getCount();

    /** Returns the format for this bundle. This value should never change. */
    @JsonIgnore
    @Override public BundleFormat getFormat();

    /** Returns a new bundle from the same factory. Usually the same as getFormat().createBundle(). */
    @Override public Bundle createBundle();

    /** Iterates over the list of fields set in this bundle. May differ from getFormat().iterator(). */
    @Override public Iterator<BundleField> iterator();

    /** Returns a view of this bundle as a {@link Map}. Changes to one are reflected in the other. */
    @Beta default Map<BundleField, ValueObject> asMap() {
        return new BundleMapView<>(this);
    }

    /** Returns a view of this bundle as a {@link ValueMap}. Changes to one are reflected in the other. */
    @Beta default ValueMap asValueMap() {
        return new BundleValueMap(this);
    }

    /** Returns a view of this bundle as a {@link ValueArray}. Changes to one are reflected in the other. */
    @Beta default ValueArray asValueArray() {
        return new BundleValueArray(this);
    }
}
