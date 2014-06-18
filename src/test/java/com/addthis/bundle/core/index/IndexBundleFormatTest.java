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
package com.addthis.bundle.core.index;

import com.google.common.collect.Iterators;

import org.junit.Assert;
import org.junit.Test;

public class IndexBundleFormatTest {

    @Test
    public void iterateCorrectly() {
        IndexBundleFormat format = new IndexBundleFormat(5);
        Assert.assertEquals("Index Format should iterate over each column",
                5, Iterators.size(format.iterator()));

        IndexBundleFormat formatStrings = new IndexBundleFormat("one", "two", "merge");
        Assert.assertEquals("Index Format should iterate over each column",
                3, Iterators.size(formatStrings.iterator()));
    }

}
