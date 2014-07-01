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

public interface ValueCustom<T> extends ValueObject<T> {

    /**
     * custom objects MUST be able to represent themselves as a ValueMap object
     * and have a class that can be re-hydrated by supplying a map.
     */
    @Override
    public ValueMap<?> asMap();

    /**
     * re-hydration call
     */
    public void setValues(ValueMap<?> map);

    /**
     * for translation to an environment where native encoding cannot be
     * preserved, return a simplified but non-reversible representation. this
     * occurs, for example, when a complex object like a counter/estimator is
     * turned into a long value at egress from the query system to a browser or
     * other non-value-native environment
     */
    public ValueSimple<?> asSimple();
}
