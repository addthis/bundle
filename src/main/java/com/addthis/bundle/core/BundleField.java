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
 * Used for efficient access to a field of data stored in a Bundle. Fields
 * reference data using a specific key name. Fields are managed by Bundle Streams,
 * they are bound to specific columns in the underlying stream and are rebound
 * when the stream format changes.
 */
public interface BundleField {

    /**
     * @return bound field name
     */
    public String getName();

    /**
     * @return field index or null if this field type does not support indexing
     */
    public Integer getIndex();
}
