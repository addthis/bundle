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
package com.addthis.bundle.core.list;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleField;
import com.addthis.bundle.core.BundleFormat;

import com.google.common.collect.Iterators;

public class ListBundleFormat implements BundleFormat {

    @Nonnull
    private final AtomicReference<State> state = new AtomicReference<>(new State());

    private static class State {

        private static final BundleField[] EMPTY_LIST_FIELDS = new ListBundleField[0];

        @Nonnull
        final Map<String, BundleField> fieldMap;

        @Nonnull
        final BundleField[] fieldArray;

        @Nonnull
        final Object version;

        State() {
            this(Collections.<String, BundleField>emptyMap(), EMPTY_LIST_FIELDS);
        }

        State(Map<String, BundleField> fieldMap, BundleField[] fieldArray) {
            this.fieldMap = fieldMap;
            this.fieldArray = fieldArray;
            version = new Object();
        }
    }

    @Override
    public String toString() {
        State currentState = state.get();
        return currentState.fieldArray.toString();
    }

    @Override
    public Object getVersion() {
        State currentState = state.get();
        return currentState.version;
    }

    @Override
    public Iterator<BundleField> iterator() {
        State currentState = state.get();
        return Iterators.forArray(currentState.fieldArray);
    }

    /**
     * Atomically creates a bundle field that did not previously exist.
     *
     * @param prefix
     * @return
     */
    public BundleField createNewField(String prefix) {
        while (true) {
            State currentState = state.get();
            int size = currentState.fieldArray.length;
            int accumulate = 0;
            String name = prefix + (size + accumulate);
            while (currentState.fieldMap.containsKey(name)) {
                accumulate++;
                name = prefix + (size + accumulate);
            }
            Map<String, BundleField> newMap = new HashMap<>(currentState.fieldMap);
            BundleField[] newArray = Arrays.copyOf(currentState.fieldArray, size + 1);
            ListBundleField newField = new ListBundleField(name, size);
            newMap.put(name, newField);
            newArray[size] = newField;
            State newState = new State(newMap, newArray);
            if (state.compareAndSet(currentState, newState)) {
                return newField;
            }
        }
    }

    @Override
    public boolean hasField(String name) {
        State currentState = state.get();
        BundleField field = currentState.fieldMap.get(name);
        return field != null;
    }

    /**
     * Either retrieves an existing bundle field with the given name,
     * or creates a bundle field with the given name if it does not
     * already exist.
     *
     * @param name requested field
     * @return
     */
    @Override
    public BundleField getField(String name) {
        while (true) {
            State currentState = state.get();
            BundleField field = currentState.fieldMap.get(name);
            if (field != null) {
                return field;
            }
            int size = currentState.fieldArray.length;
            Map<String, BundleField> newMap = new HashMap<>(currentState.fieldMap);
            BundleField[] newArray = Arrays.copyOf(currentState.fieldArray, size + 1);
            ListBundleField newField = new ListBundleField(name, size);
            newMap.put(name, newField);
            newArray[size] = newField;
            State newState = new State(newMap, newArray);
            if (state.compareAndSet(currentState, newState)) {
                return newField;
            }
        }
    }

    @Override
    public BundleField getField(int pos) {
        State currentState = state.get();
        int size = currentState.fieldArray.length;
        if (pos < 0 || pos >= size) {
            return null;
        } else {
            return currentState.fieldArray[pos];
        }
    }

    @Override
    public int getFieldCount() {
        State currentState = state.get();
        return currentState.fieldArray.length;
    }

    @Override public Bundle createBundle() {
        return new ListBundle(this);
    }
}
