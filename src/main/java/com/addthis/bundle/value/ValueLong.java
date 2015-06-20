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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = DefaultLong.class)
public interface ValueLong extends ValueNumber {

    @Override public Long asNative();

    @Override ValueLong sum(Numeric val);

    @Override ValueLong diff(Numeric val);

    @Override ValueLong prod(Numeric val);

    @Override ValueLong divide(Numeric val);

    @Override ValueLong avg(int count);

    @Override ValueLong min(Numeric val);

    @Override ValueLong max(Numeric val);

    public long getLong();
}
