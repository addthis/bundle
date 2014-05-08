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
 * Represents one "line" or "packet" of data having
 * multiple fields.  Iterator returns a list of fields with bound values.
 * Field iterators MUST obey the same constraints as the getFormat() call.
 */
public interface Bundle extends Iterable<BundleField>, BundleFormatted, BundleFactory, IndexBundle {

    /**
     * gets the specified field as a String value.
     *
     * @param field
     * @return String value of field
     * @throws BundleBindingException
     */
    public ValueObject getValue(BundleField field) throws BundleException;

    /**
     * sets the specified field to a String value.
     *
     * @param field
     * @throws BundleException
     */
    public void setValue(BundleField field, ValueObject value) throws BundleException;

    /**
     * removes a field from a bundle
     *
     * @param field
     * @throws BundleException
     */
    public void removeValue(BundleField field) throws BundleException;

    /**
     * returns the format for this bundle.  maps to known fields.  the
     * object returned by this call MUST NOT change unless the underlying
     * stream or object has changed in an incompatible way.  the order
     * of fields presented by this object MUST remain consistent.
     *
     * @return format object
     */
    public BundleFormat getFormat();

    /**
     * @return number of values in this bundle
     */
    public int getCount();

    /**
     * @return a new bundle from the same factory
     */
    public Bundle createBundle();
}
