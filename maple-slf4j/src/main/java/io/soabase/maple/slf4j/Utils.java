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

import io.soabase.maple.api.LoggingLevel;
import io.soabase.maple.api.LevelLogger;
import org.slf4j.Logger;

class Utils {
    static String isEnabledLoggerName(LoggingLevel level, Logger logger) {
        switch (level) {
            case ERROR:
                return logger.isErrorEnabled() ? logger.getName() : null;
            case WARN:
                return logger.isWarnEnabled() ? logger.getName() : null;
            case INFO:
                return logger.isInfoEnabled() ? logger.getName() : null;
            case DEBUG:
                return logger.isDebugEnabled() ? logger.getName() : null;
            case TRACE:
                return logger.isTraceEnabled() ? logger.getName() : null;
        }
        throw new IllegalStateException();  // should never get here
    }

    static LevelLogger levelLogger(LoggingLevel level, Logger logger) {
        switch (level) {
            case ERROR:
                return (msg, t) -> error(logger, msg, t);
            case WARN:
                return (msg, t) -> warn(logger, msg, t);
            case INFO:
                return (msg, t) -> info(logger, msg, t);
            case DEBUG:
                return (msg, t) -> debug(logger, msg, t);
            case TRACE:
                return (msg, t) -> trace(logger, msg, t);
        }
        throw new IllegalStateException();  // should never get here
    }

    private static void error(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.error(msg, t);
        } else {
            logger.error(msg);
        }
    }

    private static void warn(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.warn(msg, t);
        } else {
            logger.warn(msg);
        }
    }

    private static void info(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.info(msg, t);
        } else {
            logger.info(msg);
        }
    }

    private static void debug(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.debug(msg, t);
        } else {
            logger.debug(msg);
        }
    }

    private static void trace(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.trace(msg, t);
        } else {
            logger.trace(msg);
        }
    }

    private Utils() {
    }
}
