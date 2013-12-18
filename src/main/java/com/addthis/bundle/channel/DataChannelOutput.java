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
package com.addthis.bundle.channel;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.BundleFactory;
import com.addthis.bundle.core.BundleOutput;

/**
 * the opposite of a source
 */
public interface DataChannelOutput extends BundleFactory, BundleOutput {

    /**
     * a sink SHOULD require sent bundles to be from it's own factory.
     * a sink MAY require sent bundles to use the same BundleFormat as bundles
     * created from it's own factory.
     */
    public void send(Bundle bundle) throws DataChannelError;

    /**
     * indicates a normal end of send. no more data.
     */
    public void sendComplete();

    /**
     * indicates end of send because of a source error
     *
     * @param er error encountered from the source
     */
    public void sourceError(DataChannelError er);
}
