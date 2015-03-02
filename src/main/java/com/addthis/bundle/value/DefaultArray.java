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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.annotation.JsonCreator;

public class DefaultArray extends ArrayList<ValueObject> implements ValueArray {

    @JsonCreator protected DefaultArray() { super(); }
    @JsonCreator protected DefaultArray(int size) { super(size); }
    @JsonCreator protected DefaultArray(Collection<ValueObject> c) { super(c); }
}
