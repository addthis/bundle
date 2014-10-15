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

import java.util.Objects;

import com.addthis.bundle.value.Numeric;
import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueObject;
import com.addthis.bundle.value.ValueString;

public final class ValueUtil {

    /** @deprecated Use {@link Objects#equals(Object, Object)} */
    @Deprecated
    public static boolean isEqual(ValueObject v1, ValueObject v2) {
        if (v1 == v2) {
            return true;
        }
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.equals(v2);
    }

    /**
     * return true if null value or value is equivalent to an empty string
     */
    public static boolean isEmpty(ValueObject v) {
        return v == null || (v.getObjectType() == ValueObject.TYPE.STRING && v.toString().length() == 0);
    }

    /**
     * @return String or null if it is null or not convertible
     */
    public static String asNativeString(ValueObject v) {
        return v != null ? v.getObjectType() == ValueObject.TYPE.STRING ? v.asString().asNative() : v.toString() : null;
    }

    /**
     * @return String or null if it is null or not convertible
     */
    public static ValueString asString(ValueObject v) {
        return v != null ? v.getObjectType() == ValueObject.TYPE.STRING ? v.asString() : ValueFactory.create(v.toString()) : null;
    }

    /**
     * @return null if null or an array otherwise.
     */
    public static ValueArray asArray(ValueObject v) {
        if (v == null) {
            return null;
        }
        if (v.getObjectType() == ValueObject.TYPE.ARRAY) {
            return v.asArray();
        }
        ValueArray arr = ValueFactory.createArray(1);
        arr.add(v);
        return arr;
    }

    /**
     * @return null if null or a number otherwise.
     */
    public static Numeric asNumber(ValueObject v) {
        if (v != null) {
            if (v instanceof Numeric) {
                return (Numeric) v;
            }
            ValueObject.TYPE t = v.getObjectType();
            if (t == ValueObject.TYPE.INT) {
                return v.asLong();
            }
            if (t == ValueObject.TYPE.FLOAT) {
                return v.asDouble();
            }
        }
        return null;
    }

    /**
     * return number if it's already one.  if not guess double or
     * long based on the presence of "." in string version and parse.
     */
    public static Numeric asNumberOrParse(ValueObject v) {
        Numeric n = asNumber(v);
        if (n == null && v != null) {
            String sv = v.toString();
            if (sv.indexOf(".") > 0) {
                return ValueFactory.create(Double.parseDouble(v.toString()));
            } else {
                return ValueFactory.create(Long.parseLong(v.toString(), 10));
            }
        }
        return n;
    }

    /**
     * return number if it's already one.  if not parse as long from
     * the string value using provided radix.
     */
    public static Numeric asNumberOrParseLong(ValueObject v, int radix) {
        Numeric n = asNumber(v);
        if (n == null && v != null) {
            return ValueFactory.create(Long.parseLong(v.toString(), radix));
        }
        return n;
    }

    /**
     * return number if it's already one.  if not parse as double from
     * the string value.
     */
    public static Numeric asNumberOrParseDouble(ValueObject v) {
        Numeric n = asNumber(v);
        if (n == null && v != null) {
            return ValueFactory.create(Double.parseDouble(v.toString()));
        }
        return n;
    }

    /**
     * @return a string concatenation of all array elements
     */
    public static String template(ValueArray array) {
        StringBuilder sb = new StringBuilder(array.size());
        for (ValueObject o : array) {
            if (o != null) {
                sb.append(o.toString());
            }
        }
        return sb.toString();
    }
}
