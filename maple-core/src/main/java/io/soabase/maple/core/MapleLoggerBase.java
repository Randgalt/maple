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
package io.soabase.maple.core;

import io.soabase.maple.api.LoggingLevel;
import io.soabase.maple.api.MapleLoggerApi;
import io.soabase.maple.api.Statement;

public interface MapleLoggerBase<T> extends MapleLoggerApi<T> {
    default void trace(Statement<T> statement) {
        consume(LoggingLevel.TRACE,  "", null, statement);
    }

    default void debug(Statement<T> statement) {
        consume(LoggingLevel.DEBUG, "", null, statement);
    }

    default void warn(Statement<T> statement) {
        consume(LoggingLevel.WARN, "", null, statement);
    }

    default void info(Statement<T> statement) {
        consume(LoggingLevel.INFO, "", null, statement);
    }

    default void error(Statement<T> statement) {
        consume(LoggingLevel.ERROR, "", null, statement);
    }

    default void trace(String mainMessage, Statement<T> statement) {
        consume(LoggingLevel.TRACE, mainMessage, null, statement);
    }

    default void debug(String mainMessage, Statement<T> statement) {
        consume(LoggingLevel.DEBUG, mainMessage, null, statement);
    }

    default void warn(String mainMessage, Statement<T> statement) {
        consume(LoggingLevel.WARN, mainMessage, null, statement);
    }

    default void info(String mainMessage, Statement<T> statement) {
        consume(LoggingLevel.INFO, mainMessage, null, statement);
    }

    default void error(String mainMessage, Statement<T> statement) {
        consume(LoggingLevel.ERROR, mainMessage, null, statement);
    }

    default void trace(Throwable t, Statement<T> statement) {
        consume(LoggingLevel.TRACE, "", t, statement);
    }

    default void debug(Throwable t, Statement<T> statement) {
        debug("", t, statement);
    }

    default void warn(Throwable t, Statement<T> statement) {
        consume(LoggingLevel.WARN, "", t, statement);
    }

    default void info(Throwable t, Statement<T> statement) {
        consume(LoggingLevel.INFO, "", t, statement);
    }

    default void error(Throwable t, Statement<T> statement) {
        consume(LoggingLevel.ERROR, "", t, statement);
    }

    default void trace(String mainMessage, Throwable t, Statement<T> statement) {
        consume(LoggingLevel.TRACE, mainMessage, t, statement);
    }

    default void debug(String mainMessage, Throwable t, Statement<T> statement) {
        consume(LoggingLevel.DEBUG, mainMessage, t, statement);
    }

    default void warn(String mainMessage, Throwable t, Statement<T> statement) {
        consume(LoggingLevel.WARN, mainMessage, t, statement);
    }

    default void info(String mainMessage, Throwable t, Statement<T> statement) {
        consume(LoggingLevel.INFO, mainMessage, t, statement);
    }

    default void error(String mainMessage, Throwable t, Statement<T> statement) {
        consume(LoggingLevel.ERROR, mainMessage, t, statement);
    }

    void consume(LoggingLevel loggingLevel, String mainMessage, Throwable t, Statement<T> statement);
}
