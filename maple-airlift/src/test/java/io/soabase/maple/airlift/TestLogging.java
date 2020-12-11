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
package io.soabase.maple.airlift;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogManager;

import static org.assertj.core.api.Assertions.assertThat;

class TestLogging {
    @Test
    void testBasic() {
        TestLogger testLogger = new TestLogger("test-basic");
        LogManager.getLogManager().addLogger(testLogger);
        MapleLogger<Schema> logger = MapleFactory.getLogger("test-basic", Schema.class);
        logger.info(s -> s.name("me").age(24));
        assertThat(testLogger.getAndClearEvents()).containsExactly(new TestLogger.Event(Level.INFO, "age=24 name=me"));

        logger.logger().error("this is a test");
        assertThat(testLogger.getAndClearEvents()).containsExactly(new TestLogger.Event(Level.SEVERE, "this is a test"));
    }
}
