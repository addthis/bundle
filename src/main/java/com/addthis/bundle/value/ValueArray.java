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

public interface ValueArray extends ValueObject, Iterable<ValueObject> {

    public int size();

    public ValueObject get(int pos);

    public ValueObject set(int pos, ValueObject val);

    public void insert(int before, ValueObject val);

    public void append(ValueObject val);

    public ValueObject remove(int pos);

    public void clear();
}
