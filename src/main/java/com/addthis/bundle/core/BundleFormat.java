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


/**
 * Bundles and Bundle streams MUST preserve BundleField format ordering.
 * Bundles and Bundle streams MUST return the same BundleFormat object
 * unless the underlying format has changed.  A change in format object
 * is used by many stream clients to perform re-binding.
 */
public interface BundleFormat extends Iterable<BundleField>, BundleFactory {

    /**
     * returns a field for the given name.  creates a new field
     * position if that field does not exist.
     *
     * @param name requested field
     * @return a field bound to this name
     */
    public BundleField getField(String name);

    public boolean hasField(String name);

    /**
     * return a field by position if it exists, null otherwise
     *
     * @param pos position of desired field in format
     * @return a bound field
     */
    public BundleField getField(int pos);

    /**
     * @return numnber of bound fields in this format
     */
    public int getFieldCount();

    /**
     * @return an object that changes only when the format changes
     */
    public Object getVersion();
}
