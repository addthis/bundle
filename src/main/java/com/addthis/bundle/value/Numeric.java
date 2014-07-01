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

public interface Numeric<T> extends ValueObject<T> {
    /**
     * calculate the sum of the objects and return
     * a new ValueNumber of the same type as this object.
     *
     * @return the sum of this and the param val
     */
    <P extends Numeric<?>> Numeric<?> sum(P val);

    /**
     * calculate the difference of the two objects
     * by subtracting val from this object's value
     * and returning a new ValueNumber of the same
     * type as this object.
     *
     * @return this value less the param val
     */
    <P extends Numeric<?>> Numeric<?> diff(P val);

    /**
     * calculate the average this numnber's value
     * over a denominator returning a new ValueNumber
     * of the same type as this object.
     *
     * @return the average of this over count
     */
    Numeric<?> avg(int count);

    /**
     * calculate the minimum of two numbers returning
     * a new ValueNumber of the same type as this object.
     *
     * @return the lesser of this and the param val
     */
    <P extends Numeric<?>> Numeric<?> min(P val);

    /**
     * calculate the maximum of two numbers returning
     * a new ValueNumber of the same type as this object.
     *
     * @return the greater of this and the param val
     */
    <P extends Numeric<?>> Numeric<?> max(P val);
}
