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


import com.addthis.bundle.core.IndexFormat;
import com.addthis.bundle.value.ValueType;

/**
 * Non-standard (not implemented or needed by 'Bundle') methods
 * specific to a sub-set of IndexBundle implementations that provide
 * convenience or alternate interfacing.
 */
public interface ExtIndexFormat extends IndexFormat {

    /** returns label at column index. Labels are for human use only */
    CharSequence label(int index);
    /** set label at column index. Labels are for human use only */
    void label(int index, CharSequence label);

    /** returns value type expected for a given column index. For human and computer use */
    ValueType type(int index);
    /** sets the type expected for a given column index. For human and computer use */
    void type(int index, ValueType type);

    /** create a new bundle based on this format */
    ExtIndexBundle newBundle();

    /** alias for getFieldCount */
    int size();
}
