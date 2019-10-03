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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.soabase.maple.api.LoggingLevel;
import io.soabase.maple.formatters.ModelFormatter;
import io.soabase.maple.formatters.StandardFormatter;
import io.soabase.maple.schema.Schema;
import io.soabase.maple.spi.MapleSpi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.soabase.maple.formatters.ModelFormatter.NodeMapper.forMapper;
import static io.soabase.maple.formatters.StandardFormatter.Option.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestFormatters {
    @AfterEach
    void cleanUp() {
        MapleSpi.instance().reset();
    }

    @Test
    void testToSnakeCase() {
        assertThat(toSnakeCase("oneTwoThree")).isEqualTo("one_two_three");
        assertThat(toSnakeCase("")).isEqualTo("");
        assertThat(toSnakeCase("OneTwo")).isEqualTo("one_two");
        assertThat(toSnakeCase("O")).isEqualTo("o");
        assertThat(toSnakeCase("OO")).isEqualTo("oo");
        assertThat(toSnakeCase("ONEtwoTHREE")).isEqualTo("onetwo_three");
    }

    @Test
    void testToEscapedValue() {
        assertThat(toEscapedValue("one", false)).isEqualTo("one");
        assertThat(toEscapedValue("", false)).isEqualTo("");
        assertThat(toEscapedValue("one two  ", false)).isEqualTo("one two  ");
        assertThat(toEscapedValue("\"one\"", false)).isEqualTo("\\\"one\\\"");
        assertThat(toEscapedValue("\"", false)).isEqualTo("\\\"");
        assertThat(toEscapedValue("\\", false)).isEqualTo("\\\\");
        assertThat(toEscapedValue("one\ttwo\rthree\nfour", false)).isEqualTo("one two three four");

        assertThat(toEscapedValue("\\", true)).isEqualTo("\\\\");
        assertThat(toEscapedValue("", true)).isEqualTo("");
        assertThat(toEscapedValue("  ", true)).isEqualTo("\\ \\ ");
        assertThat(toEscapedValue(" ", true)).isEqualTo("\\ ");
        assertThat(toEscapedValue(" one a b ", true)).isEqualTo("\\ one\\ a\\ b\\ ");
        assertThat(toEscapedValue("one\ttwo\rthree\nfour", true)).isEqualTo("one\\ two\\ three\\ four");
    }

    @Test
    void testIllegalOptions() {
        assertThatThrownBy(() -> new StandardFormatter(QUOTE_VALUES_IF_NEEDED, QUOTE_VALUES)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StandardFormatter(QUOTE_VALUES_IF_NEEDED)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNoOptions() {
        MapleSpi.instance().setFormatter(new StandardFormatter());
        MockMapleLogger<Schema> logger = doLogging();
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "the message firstName=first name lastName=last \"name\" address=null age=100", null));
    }

    @Test
    void testMessageLast() {
        MapleSpi.instance().setFormatter(new StandardFormatter(MAIN_MESSAGE_IS_LAST));
        MockMapleLogger<Schema> logger = doLogging();
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "firstName=first name lastName=last \"name\" address=null age=100 the message", null));
    }

    @Test
    void testSkipNulls() {
        MapleSpi.instance().setFormatter(new StandardFormatter(SKIP_NULL_VALUES));
        MockMapleLogger<Schema> logger = doLogging();
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "the message firstName=first name lastName=last \"name\" age=100", null));
    }

    @Test
    void testQuoting() {
        MapleSpi.instance().setFormatter(new StandardFormatter(QUOTE_VALUES));
        MockMapleLogger<Schema> logger = doLogging();
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "the message firstName=\"first name\" lastName=\"last \"name\"\" address=\"null\" age=\"100\"", null));
    }

    @Test
    void testQuotingIfNeeded() {
        MapleSpi.instance().setFormatter(new StandardFormatter(QUOTE_VALUES_IF_NEEDED, ESCAPE_VALUES));
        MockMapleLogger<Schema> logger = doLogging();
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "the message firstName=\"first name\" lastName=\"last \\\"name\\\"\" address=null age=100", null));
    }

    @Test
    void testEscaping() {
        MapleSpi.instance().setFormatter(new StandardFormatter(ESCAPE_VALUES));
        MockMapleLogger<Schema> logger = doLogging();
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "the message firstName=first\\ name lastName=last\\ \\\"name\\\" address=null age=100", null));
    }

    @Test
    void testSnakeCase() {
        MapleSpi.instance().setFormatter(new StandardFormatter(SNAKE_CASE));
        MockMapleLogger<Schema> logger = doLogging();
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "the message first_name=first name last_name=last \"name\" address=null age=100", null));
    }

    @Test
    void testModelFormatter() {
        ObjectMapper mapper = new ObjectMapper();
        MapleSpi.instance().setFormatter(new ModelFormatter(forMapper(mapper), SNAKE_CASE, QUOTE_VALUES_IF_NEEDED, ESCAPE_VALUES));

        Address address = new Address("123 Main St", "Newtown", "New Fornia", "12225", "do not show");
        MockMapleLogger<Schema> logger = MockMapleLogger.get(Schema.class);
        logger.info(s -> s.age(10).name("Hay", "You").address(address));
        assertThat(logger.logging()).containsExactly(new LogEvent(LoggingLevel.INFO, "first_name=Hay last_name=You address.street=\"123 Main St\" address.city=Newtown address.state=\"New Fornia\" address.zip=12225 age=10", null));
    }

    private MockMapleLogger<Schema> doLogging() {
        MockMapleLogger<Schema> logger = MockMapleLogger.get(Schema.class);
        logger.info("the message", s -> s.firstName("first name").lastName("last \"name\"").age(100));
        return logger;
    }

    private String toSnakeCase(String value) {
        StringBuilder str = new StringBuilder();
        StandardFormatter.toSnakeCase(str, value);
        return str.toString();
    }

    private String toEscapedValue(String value, boolean escapeSpaces) {
        StringBuilder str = new StringBuilder();
        StandardFormatter.addEscapedValue(str, value, escapeSpaces);
        return str.toString();
    }
}
