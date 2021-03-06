/*
 * Copyright 2017 Sanjin Sehic
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

package simple.actor.testing;

import org.junit.runner.RunWith;

import alioli.Scenario;

import static alioli.Asserts.assertThrows;
import static com.google.common.truth.Truth.assertThat;

/** Tests for {@link SpyActor}. */
@RunWith(Scenario.Runner.class)
public class SpyActorTest extends Scenario {
    {
        subject("spy actor", () -> {
            final SpyActor<Message> actor = new SpyActor<>();

            should("not be started", () -> {
                assertThat(actor.isStarted()).isFalse();
            });

            should("not be stopped", () -> {
                assertThat(actor.isStopped()).isFalse();
            });

            should("have no received messages", () -> {
                assertThat(actor.getReceivedMessages()).isEmpty();
            });

            should("complain if it receives a message because it is not started", () -> {
                assertThrows(() -> actor.onMessage(new Message()));
            });

            should("complain if stopped because it is not started", () -> {
                assertThrows(actor::onStop);
            });

            when("started", () -> {
                actor.onStart(new SpyChannel<>(), new SpyContext());

                should("remember it", () -> {
                    assertThat(actor.isStarted()).isTrue();
                });

                should("complain if started again", () -> {
                    assertThrows(() -> actor.onStart(new SpyChannel<>(), new SpyContext()));
                });

                should("not be stopped", () -> {
                    assertThat(actor.isStopped()).isFalse();
                });

                and("it receives a message", () -> {
                    final Message message = new Message();
                    actor.onMessage(message);

                    should("remember it", () -> {
                        assertThat(actor.getReceivedMessages()).containsExactly(message);
                    });
                });

                and("stopped", () -> {
                    actor.onStop();

                    should("remember it", () -> {
                        assertThat(actor.isStopped()).isTrue();
                    });

                    should("complain if it receives a message because it is stopped", () -> {
                        assertThrows(() -> actor.onMessage(new Message()));
                    });

                    should("complain if stopped again", () -> {
                        assertThrows(actor::onStop);
                    });
                });
            });
        });
    }

    private static final class Message {}
}
