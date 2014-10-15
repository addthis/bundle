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

    /** Returns a field for the given name. Creates a new field position if that field does not exist. */
    public BundleField getField(String name);

    public boolean hasField(String name);

    /** Return a field by position if it exists, null otherwise */
    public BundleField getField(int pos);

    /** Numnber of bound fields in this format. */
    public int getFieldCount();

    /** Object that changes only when the format changes. */
    public Object getVersion();
}
