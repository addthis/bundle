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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * very simple source selector that just selects the
 * next source in the list with a non-null bundle
 * <p/>
 * TODO:  implement backoff algorithm for soures that do not currently have data
 * TODO:  implement a new sourceselector that splices based on time fields
 */
public class SimpleSourceSelector implements SourceSelector {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceSelector.class);

    @Override
    public DataChannelSource selectSource(Collection<DataChannelSource> sourceList, DataChannelSource last) {
        if (sourceList == null || sourceList.isEmpty()) {
            return null;
        }
        for (DataChannelSource dataChannelSource : sourceList) {
            try {
                if (dataChannelSource.peek() != null) {
                    return dataChannelSource;
                }
            } catch (Exception e) {
                logger.error("Error peeking at dataChannelSource", e);
            }
        }
        return null;
    }
}
