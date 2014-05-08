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

import com.addthis.bundle.value.ValueObject;


/**
 * Represents one "line" or "packet" of data having multiple fields.
 */
public interface IndexBundle {

    /**
     * gets the field at the given index as a ValueObject
     */
    public ValueObject value(int index);

    /**
     * sets the field as the given index to a ValueObject.
     */
    public void value(int index, ValueObject value);

    /**
     * returns the Format for the bundle. It has metadata
     * about the columns in this bundle which varies by implementation.
     *
     * returns the format for this bundle.  maps to known fields.  the
     * object returned by this call MUST NOT change unless the underlying
     * stream or object has changed in an incompatible way.  the order
     * of fields presented by this object MUST remain consistent.
     */
    public IndexFormat format();
}
