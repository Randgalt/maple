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
package io.soabase.maple.api;

public interface MapleLoggerApi<T> {
    /**
     * If the log level is TRACE, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param statement structured logging statement
     */
    void trace(Statement<T> statement);

    /**
     * If the log level is DEBUG, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param statement structured logging statement
     */
    void debug(Statement<T> statement);

    /**
     * If the log level is WARN, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param statement structured logging statement
     */
    void warn(Statement<T> statement);

    /**
     * If the log level is INFO, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param statement structured logging statement
     */
    void info(Statement<T> statement);

    /**
     * If the log level is ERROR, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param statement structured logging statement
     */
    void error(Statement<T> statement);

    /**
     * If the log level is TRACE, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param statement structured logging statement
     */
    void trace(String mainMessage, Statement<T> statement);

    /**
     * If the log level is DEBUG, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param statement structured logging statement
     */
    void debug(String mainMessage, Statement<T> statement);

    /**
     * If the log level is WARN, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param statement structured logging statement
     */
    void warn(String mainMessage, Statement<T> statement);

    /**
     * If the log level is INFO, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param statement structured logging statement
     */
    void info(String mainMessage, Statement<T> statement);

    /**
     * If the log level is ERROR, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param statement structured logging statement
     */
    void error(String mainMessage, Statement<T> statement);

    /**
     * If the log level is TRACE, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void trace(Throwable t, Statement<T> statement);

    /**
     * If the log level is DEBUG, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void debug(Throwable t, Statement<T> statement);

    /**
     * If the log level is WARN, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void warn(Throwable t, Statement<T> statement);

    /**
     * If the log level is INFO, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void info(Throwable t, Statement<T> statement);

    /**
     * If the log level is ERROR, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void error(Throwable t, Statement<T> statement);

    /**
     * If the log level is TRACE, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void trace(String mainMessage, Throwable t, Statement<T> statement);

    /**
     * If the log level is DEBUG, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void debug(String mainMessage, Throwable t, Statement<T> statement);

    /**
     * If the log level is WARN, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void warn(String mainMessage, Throwable t, Statement<T> statement);

    /**
     * If the log level is INFO, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void info(String mainMessage, Throwable t, Statement<T> statement);

    /**
     * If the log level is ERROR, complete the statement using a generated schema
     * instance, pass the generated name/values to the current logging formatter
     * which will then log via the underlying logging system.
     *
     * @param mainMessage passed as the main message to the logging formatter - cannot be {@code null}
     * @param t passed as the exception to the logging formatter - can be {@code null}
     * @param statement structured logging statement
     */
    void error(String mainMessage, Throwable t, Statement<T> statement);

    /**
     * Pass the generated name/values to the logger's MDC system
     *
     * @param statement structured logging statement
     * @return a closeable - when closed the schema names are removed from MDC
     */
    MdcCloseable mdc(Statement<T> statement);
}
