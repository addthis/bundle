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

public interface ValueNumber extends ValueSimple, Numeric {

    @Override public Number asNative();

    @Override ValueNumber sum(Numeric val);

    @Override ValueNumber diff(Numeric val);

    @Override ValueNumber prod(Numeric val);

    @Override ValueNumber divide(Numeric val);

    @Override ValueNumber avg(int count);

    @Override ValueNumber min(Numeric val);

    @Override ValueNumber max(Numeric val);
}
