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

public interface ValueNumber extends ValueSimple {

    /**
     * calculate the sum of the objects and return
     * a new ValueNumber of the same type as this object.
     *
     * @param val
     * @return the sum of this and the param val
     */
    public ValueNumber sum(ValueNumber val);

    /**
     * calculate the difference of the two objects
     * by subtracting val from this object's value
     * and returning a new ValueNumber of the same
     * type as this object.
     *
     * @param val
     * @return this value less the param val
     */
    public ValueNumber diff(ValueNumber val);

    /**
     * calculate the average this numnber's value
     * over a denominator returning a new ValueNumber
     * of the same type as this object.
     *
     * @param count
     * @return the average of this over count
     */
    public ValueNumber avg(int count);

    /**
     * calculate the minimum of two numbers returning
     * a new ValueNumber of the same type as this object.
     *
     * @param val
     * @return the lesser of this and the param val
     */
    public ValueNumber min(ValueNumber val);

    /**
     * calculate the maximum of two numbers returning
     * a new ValueNumber of the same type as this object.
     *
     * @param val
     * @return the greater of this and the param val
     */
    public ValueNumber max(ValueNumber val);

    /**
     * @return long representation of this object
     */
    public ValueLong asLong();

    /**
     * @return double representation of this object
     */
    public ValueDouble asDouble();

}
