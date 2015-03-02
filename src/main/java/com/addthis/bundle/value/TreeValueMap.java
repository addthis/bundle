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

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TreeValueMap extends TreeMap<String, ValueObject> implements ValueMap {

    public TreeValueMap() { super(); }
    public TreeValueMap(Comparator<? super String> comparator) { super(comparator); }
    public TreeValueMap(Map<String, ? extends ValueObject> m) { super(m); }
    public TreeValueMap(SortedMap<String, ? extends ValueObject> m) { super(m); }
}
