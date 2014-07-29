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

@SuppressWarnings("serial")
public class DataChannelError extends RuntimeException {

    /** return DataChannelError or wrap/promote */
    public static DataChannelError promote(Throwable ex) {
        if (ex instanceof DataChannelError) {
            return (DataChannelError) ex;
        }
        return new DataChannelError(ex);
    }

    public DataChannelError() {
    }

    public DataChannelError(Exception ex) {
        super(ex);
    }

    public DataChannelError(String message, Throwable cause) {
        super(message, cause);
    }

    public DataChannelError(String message) {
        super(message);
    }

    public DataChannelError(Throwable cause) {
        super(cause);
    }

}
