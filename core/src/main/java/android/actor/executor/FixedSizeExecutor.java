/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.actor.executor;

import android.actor.Executor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedSizeExecutor implements Executor {

    private static final String TAG = FixedSizeExecutor.class.getSimpleName();

    @NonNull
    private final List<Manager> mManagers;

    private final Lock mLock = new ReentrantLock();

    private boolean mStopped = false;

    public FixedSizeExecutor(final int size) {
        super();

        if (size < 1) {
            throw new IllegalArgumentException("Size must be grater than zero!");
        }

        mManagers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mManagers.add(new Manager());
        }
    }

    @Nullable
    @Override
    public final Submission submit(@NonNull final Executable executable) {
        Manager best;

        mLock.lock();
        try {
            if (mStopped) {
                throw new UnsupportedOperationException("Executor is stopped!");
            }

            best = mManagers.get(0);
            final int size = mManagers.size();
            for (int i = 1; (i < size) && !best.isEmpty(); i++) {
                final Manager manager = mManagers.get(i);
                if (manager.size() < best.size()) {
                    best = manager;
                }
            }
        } finally {
            mLock.unlock();
        }

        return best.submit(executable);
    }

    @Override
    public final boolean stop() {
        boolean success = true;

        mLock.lock();
        try {
            for (final Manager manager : mManagers) {
                success = manager.stop() && success;
            }
            mManagers.clear();
            mStopped = true;
        } finally {
            mLock.unlock();
        }

        return success;
    }

    private static class Manager extends android.actor.executor.Manager {

        @NonNull
        private final Dispatcher mDispatcher;

        private Manager() {
            super();

            mDispatcher = new Dispatcher(this);
            mDispatcher.start();
        }

        public final boolean stop() {
            final boolean success = onStop();

            if (success) {
                mDispatcher.stop();
            } else {
                Log.w(TAG, "Manager failed to stop"); //NON-NLS
            }

            return success;
        }
    }
}
