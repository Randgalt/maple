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
package io.soabase.maple;

import io.soabase.maple.api.LoggingLevel;
import io.soabase.maple.api.Statement;
import io.soabase.maple.api.exceptions.InvalidSchemaException;
import io.soabase.maple.schema.BasicSchema;
import io.soabase.maple.schema.invalid.*;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestLogging {
    @Test
    void testBasicSchema() {
        MockMapleLogger<BasicSchema> logger = MockMapleLogger.get(BasicSchema.class);

        logger.trace(s -> s.qty(1).name("1"));
        logger.debug(s -> s.qty(2).name("2"));
        logger.warn(s -> s.qty(3).name("3"));
        logger.error(s -> s.qty(4).name("4"));
        logger.info(s -> s.qty(5).name("5"));

        logger.trace("is trace", s -> s.qty(1).name("1"));
        logger.debug("is debug", s -> s.qty(2).name("2"));
        logger.warn("is warn", s -> s.qty(3).name("3"));
        logger.error("is error", s -> s.qty(4).name("4"));
        logger.info("is info", s -> s.qty(5).name("5"));

        Error trace = new Error("trace");
        Error debug = new Error("debug");
        Error warn = new Error("warn");
        Error error = new Error("error");
        Error info = new Error("info");

        logger.trace(trace, s -> s.qty(1).name("1"));
        logger.debug(debug, s -> s.qty(2).name("2"));
        logger.warn(warn, s -> s.qty(3).name("3"));
        logger.error(error, s -> s.qty(4).name("4"));
        logger.info(info, s -> s.qty(5).name("5"));

        logger.trace("is trace", trace, s -> s.qty(1).name("1"));
        logger.debug("is debug", debug, s -> s.qty(2).name("2"));
        logger.warn("is warn", warn, s -> s.qty(3).name("3"));
        logger.error("is error", error, s -> s.qty(4).name("4"));
        logger.info("is info", info, s -> s.qty(5).name("5"));

        assertThat(logger.logging()).containsSequence(
                new LogEvent(LoggingLevel.TRACE, "name=1 qty=1", null),
                new LogEvent(LoggingLevel.DEBUG, "name=2 qty=2", null),
                new LogEvent(LoggingLevel.WARN, "name=3 qty=3", null),
                new LogEvent(LoggingLevel.ERROR, "name=4 qty=4", null),
                new LogEvent(LoggingLevel.INFO, "name=5 qty=5", null),

                new LogEvent(LoggingLevel.TRACE, "name=1 qty=1 is trace", null),
                new LogEvent(LoggingLevel.DEBUG, "name=2 qty=2 is debug", null),
                new LogEvent(LoggingLevel.WARN, "name=3 qty=3 is warn", null),
                new LogEvent(LoggingLevel.ERROR, "name=4 qty=4 is error", null),
                new LogEvent(LoggingLevel.INFO, "name=5 qty=5 is info", null),

                new LogEvent(LoggingLevel.TRACE, "name=1 qty=1", trace),
                new LogEvent(LoggingLevel.DEBUG, "name=2 qty=2", debug),
                new LogEvent(LoggingLevel.WARN, "name=3 qty=3", warn),
                new LogEvent(LoggingLevel.ERROR, "name=4 qty=4", error),
                new LogEvent(LoggingLevel.INFO, "name=5 qty=5", info),

                new LogEvent(LoggingLevel.TRACE, "name=1 qty=1 is trace", trace),
                new LogEvent(LoggingLevel.DEBUG, "name=2 qty=2 is debug", debug),
                new LogEvent(LoggingLevel.WARN, "name=3 qty=3 is warn", warn),
                new LogEvent(LoggingLevel.ERROR, "name=4 qty=4 is error", error),
                new LogEvent(LoggingLevel.INFO, "name=5 qty=5 is info", info)
        );
    }

    @Test
    void testConcat() {
        MockMapleLogger<BasicSchema> logger = MockMapleLogger.get(BasicSchema.class);

        Statement<BasicSchema> partial = s -> s.name("me");

        logger.warn(partial.concat(s -> s.qty(1234)));
        logger.error(partial.concat(s -> s.qty(468).name("other")));
        assertThat(logger.logging()).containsSequence(
                new LogEvent(LoggingLevel.WARN, "name=me qty=1234", null),
                new LogEvent(LoggingLevel.ERROR, "name=other qty=468", null)
        );
    }

    @Test
    void testInvalidSchema() {
        Stream.of(BadReturnType.class,
                Duplicates.class,
                InvalidMethod.class,
                CannotBeAClass.class,
                MustTake1ArgumentB.class,
                MustReturnRightTypeInheritance.class
        ).forEach(clazz -> assertThatThrownBy(() -> MockMapleLogger.get(clazz)).isInstanceOf(InvalidSchemaException.class));
    }
}
