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
package com.addthis.bundle.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import com.google.common.annotations.Beta;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a set and supports "view" semantics typical for maps et al. In particular, it supports removal operations, but
 * not add operations. This class may be later moved to basis.
 *
 * Warning: this class assumes all methods that accept type "Object" are called with only type B objects. This may
 * result in {@link ClassCastException}s being thrown where other implementations would return "false" and the like.
 */
@Beta
public class ConvertingSetView<A, B> extends AbstractSet<B> {
    private static final Logger log = LoggerFactory.getLogger(ConvertingSetView.class);

    private final Set<A> source;
    private final Function<Object, A> inputConverter;
    private final Function<A, B> sourceConverter;

    @SuppressWarnings("unchecked")
    public ConvertingSetView(Set<A> source, Function<A, B> sourceConverter, Function<B, A> inputConverter) {
        this.source = source;
        this.sourceConverter = sourceConverter;
        this.inputConverter = (Function<Object, A>) inputConverter;
    }

    @Override public boolean contains(Object o) {
        return source.contains(inputConverter.apply(o));
    }

    @Override public int size() {
        return source.size();
    }

    @Override public boolean isEmpty() {
        return source.isEmpty();
    }

    @Override public Iterator<B> iterator() {
        return Iterators.transform(source.iterator(), sourceConverter::apply);
    }

    @Override public boolean remove(Object o) {
        return source.remove(inputConverter.apply(o));
    }

    @Override public boolean containsAll(Collection<?> c) {
        return source.containsAll(Collections2.transform(c, inputConverter::apply));
    }

    @Override public boolean retainAll(Collection<?> c) {
        return source.retainAll(Collections2.transform(c, inputConverter::apply));
    }

    @Override public boolean removeAll(Collection<?> c) {
        return source.removeAll(Collections2.transform(c, inputConverter::apply));
    }

    @Override public void clear() {
        source.clear();
    }
}
