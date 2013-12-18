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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.addthis.basis.util.JitterClock;

import com.addthis.bundle.core.Bundle;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract class that extends {@link DataChannelOutput} and provides
 * batching functionality for sending bundles to a data channel. When a
 * caller invokes the {@code send(Bundle bundle)} method this class will
 * enqueue the message and then call {@code sendBatch(List<Bundle> bundleList)}
 * when either the messageList has batchSize messages in it or the messages
 * on the list have been there for more than maxTimeInMillis time.
 * <p/>
 * Subclasses will implement the {@code sendBatch} method to send the full
 * list of messages to the data channel in a single batch
 * <p/>
 * Note:  If the application crashes while messages are in the queue and before they have been sent to the
 * data channel data loss is possible.
 */
public abstract class BatchingDataChannelOutput implements DataChannelOutput {

    private static final Logger log = LoggerFactory.getLogger(BatchingDataChannelOutput.class);

    private final int batchSize;
    private final long maxTimeInMillis;
    protected final List<Bundle> messageList;
    protected final Lock sendLock = new ReentrantLock();
    private long lastSendTime = 0;
    private ScheduledExecutorService bufferPurgeExecutor;
    private boolean sinkUp = true;

    /**
     * Constructor initializes a empty messsage list and starts a purge thread which runs
     * every {@code maxTimeInMillis} and checks to see if the messages need to be purged
     * from the queue.
     *
     * @param batchSize       - the number of messages that the channel should queue before invoking sendBatch
     * @param maxTimeInMillis - the maximum time in milliseconds a message can sit on the queue before
     *                        being sent to the channel.
     */
    public BatchingDataChannelOutput(int batchSize, long maxTimeInMillis) {
        messageList = new ArrayList<Bundle>();
        this.maxTimeInMillis = maxTimeInMillis;
        this.batchSize = batchSize;
        bufferPurgeExecutor = MoreExecutors.getExitingScheduledExecutorService(
                new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().setNameFormat("BatchingDataChannelOutputThread-%d").build()),
                5000, TimeUnit.MILLISECONDS);
        bufferPurgeExecutor.scheduleWithFixedDelay(new BatchSendRunner(), maxTimeInMillis, maxTimeInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void send(Bundle bundle) throws DataChannelError {
        sendLock.lock();
        try {
            if (!sinkUp) {
                throw new RuntimeException("BatchingDataChannelOutput thinks the batch sink is down");
            }
            messageList.add(bundle);
            if (messageList.size() % batchSize == 0) {
                sendBatchAndClear(messageList);
            }
        } finally {
            sendLock.unlock();
        }
    }

    @Override
    public void sendComplete() {
    }

    public void shutdown() {
        bufferPurgeExecutor.shutdown();
        try {
            bufferPurgeExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            bufferPurgeExecutor.shutdownNow();
        }
    }

    private void sendBatchAndClear(List<Bundle> messageList) {
        sendLock.lock();
        try {
            send(messageList);
            messageList.clear();
        } catch (Exception e) {
            sinkUp = false;
            log.error("Sending to kafka failed, will not send until BatchSendRunner succeeds");
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lastSendTime = JitterClock.globalTime();
            sendLock.unlock();
        }
    }

    private class BatchSendRunner implements Runnable {

        @Override
        public void run() {
            if ((JitterClock.globalTime() - lastSendTime) > maxTimeInMillis) {
                sendLock.lock();
                try {
                    sendBatchAndClear(messageList);
                    sinkUp = true;
                } catch (Exception ex) {
                    log.error("error sending message batch from BatchSendRunner");
                } finally {
                    sendLock.unlock();
                }
            }
        }
    }
}
