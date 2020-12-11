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

import io.airlift.log.Logger;
import io.soabase.maple.api.LevelLogger;
import io.soabase.maple.api.LoggingLevel;

class Utils {
    static boolean isEnabled(LoggingLevel level, Logger logger) {
        switch (level) {
            case ERROR:
                return true;    // Airlift doesn't currently support this
            case WARN:
                return true;    // Airlift doesn't currently support this
            case INFO:
                return logger.isInfoEnabled();
            case DEBUG:
                return logger.isDebugEnabled();
            case TRACE:
                return false;
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
                return (msg, t) -> {
                };  // airlift doesn't support trace
        }
        throw new IllegalStateException();  // should never get here
    }

    private static void error(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.error(t, msg);
        } else {
            logger.error(msg);
        }
    }

    private static void warn(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.warn(t, msg);
        } else {
            logger.warn(msg);
        }
    }

    private static void info(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.info(msg + " %s", t);
        } else {
            logger.info(msg);
        }
    }

    private static void debug(Logger logger, String msg, Throwable t) {
        if (t != null) {
            logger.debug(t, msg, t);
        } else {
            logger.debug(msg);
        }
    }

    private Utils() {
    }
}
