/**
 * Copyright 2019 Jordan Zimmerman
 *
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
package io.soabase.maple.slf4j;

import io.soabase.maple.api.MdcCloseable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.impl.StaticLoggerBinder;

import static org.assertj.core.api.Assertions.assertThat;

class TestLogging {
    @AfterEach
    void cleanUp() {
        StaticLoggerBinder.getEventQueue().clear();
        MDC.clear();
    }

    @Test
    void testBasic() {
        MapleLogger<Schema> logger = MapleFactory.getLogger(getClass(), Schema.class);
        logger.info(s -> s.name("me").age(24));

        SubstituteLoggingEvent event = StaticLoggerBinder.getEventQueue().remove();
        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(event.getMessage()).isEqualTo("age=24 name=me");

        logger.logger().error("this is a test");
        event = StaticLoggerBinder.getEventQueue().remove();
        assertThat(event.getLevel()).isEqualTo(Level.ERROR);
        assertThat(event.getMessage()).isEqualTo("this is a test");
    }

    @Test
    void testMdc() {
        MapleLogger<Schema> logger = MapleFactory.getLogger(getClass(), Schema.class);
        MdcCloseable closeable = logger.mdc(s -> s.name("me").age(24));
        assertThat(MDC.get("name")).isEqualTo("me");
        assertThat(MDC.get("age")).isEqualTo("24");
        closeable.close();
        assertThat(MDC.get("name")).isNull();
        assertThat(MDC.get("age")).isNull();
    }

    @Test
    void testDefaultMdcValue() {
        MapleLogger<SchemaWithMdc> logger = MapleFactory.getLogger(getClass(), SchemaWithMdc.class);
        try ( MdcCloseable closeable = logger.mdc(s -> s.transactionId("id")) ) {
            logger.info(s -> s.name("n").age(1));

            SubstituteLoggingEvent event = StaticLoggerBinder.getEventQueue().remove();
            assertThat(event.getLevel()).isEqualTo(Level.INFO);
            assertThat(event.getMessage()).isEqualTo("age=1 name=n transaction_id=id");

            logger.info(s -> s.name("n").age(1).transactionId("other"));
            event = StaticLoggerBinder.getEventQueue().remove();
            assertThat(event.getLevel()).isEqualTo(Level.INFO);
            assertThat(event.getMessage()).isEqualTo("age=1 name=n transaction_id=other");
        }
    }
}
