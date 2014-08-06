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
package com.addthis.bundle.table;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleFactory;
import com.addthis.bundle.core.BundleFormat;
import com.addthis.bundle.core.BundleFormatted;

public interface DataTable extends List<Bundle>, BundleFactory, BundleFormatted {

    @Override public BundleFormat getFormat();

    /** @deprecated Use {@link List#add(int, Object)} */
    @Deprecated public void insert(int index, Bundle row);

    /** @deprecated Use {@link List#add(Object)} */
    @Deprecated public void append(Bundle row);

    /** @deprecated Use {@link List#addAll(Collection)} */
    @Deprecated public void append(DataTable table);

    /** Sort list with given comparator. */
    public void sort(Comparator<Bundle> comp);
}
