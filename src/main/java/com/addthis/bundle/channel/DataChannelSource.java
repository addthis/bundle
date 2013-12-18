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

/**
 * Interface defining a source of data.
 */
public interface DataChannelSource {

    /**
     * Retrieve the next {@link com.addthis.bundle.core.Bundle} from the data source.
     * <p/>
     * Once a Bundle is consumed the source will
     * not provide the consumed Bundle again.  It is up to the consumer
     * to maintain transactions if required.
     *
     * @return the next {@link com.addthis.bundle.core.Bundle} from the data source
     */
    public Bundle next() throws DataChannelError;

    /**
     * Peek at the next {@link Bundle} from the data source.
     * <p/>
     * Peek differs from next in that the source will not consider a {@link Bundle}
     * returned by the peek method as 'consumed'.  It will still be available to the next method.
     *
     * @return the next {@link Bundle} from the data source
     * @throws java.io.IOException - if there is a problem retrieving data from the source
     */
    public Bundle peek() throws DataChannelError;

    /**
     * Close the data source
     */
    public void close();
}

