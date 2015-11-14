/*
 * Copyright 2015 the original author or authors
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

package android.actor.messenger;

import android.support.annotation.NonNull;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class BufferedMessengerMatchers {

    @NonNull
    public static Matcher<BufferedMessenger<?>> attached() {
        return new Attached();
    }

    private static final class Attached extends TypeSafeMatcher<BufferedMessenger<?>> {

        @Override
        protected boolean matchesSafely(final BufferedMessenger<?> messenger) {
            return messenger.isAttached();
        }

        @Override
        protected void describeMismatchSafely(final BufferedMessenger<?> messenger,
                                              final Description mismatchDescription) {
            mismatchDescription.appendText("was not attached"); //NON-NLS
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("attached"); //NON-NLS
        }
    }

    private BufferedMessengerMatchers() {
        super();
    }
}
