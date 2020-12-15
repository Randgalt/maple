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

public class AirliftLogger {
    private final Logger airliftLogger;
    private final java.util.logging.Logger javaLogger;

    public AirliftLogger(Logger airliftLogger, java.util.logging.Logger javaLogger) {
        this.airliftLogger = airliftLogger;
        this.javaLogger = javaLogger;
    }

    public void debug(Throwable exception, String message)
    {
        airliftLogger.debug(exception, message);
    }

    public void debug(String message)
    {
        airliftLogger.debug(message);
    }

    public void debug(String format, Object... args)
    {
        airliftLogger.debug(format, args);
    }

    public void debug(Throwable exception, String format, Object... args)
    {
        airliftLogger.debug(exception, format, args);
    }

    public void info(String message)
    {
        airliftLogger.info(message);
    }

    public void info(String format, Object... args)
    {
        airliftLogger.info(format, args);
    }

    public void warn(Throwable exception, String message)
    {
        airliftLogger.warn(exception, message);
    }

    public void warn(String message)
    {
        airliftLogger.warn(message);
    }

    public void warn(Throwable exception, String format, Object... args)
    {
        airliftLogger.warn(exception, format, args);
    }

    public void warn(String format, Object... args)
    {
        airliftLogger.warn(format, args);
    }

    public void error(Throwable exception, String message)
    {
        airliftLogger.error(exception, message);
    }

    public void error(String message)
    {
        airliftLogger.error(message);
    }

    public void error(Throwable exception, String format, Object... args)
    {
        airliftLogger.error(exception, format, args);
    }

    public void error(Throwable exception)
    {
        airliftLogger.error(exception);
    }

    public void error(String format, Object... args)
    {
        airliftLogger.error(format, args);
    }

    public boolean isDebugEnabled()
    {
        return airliftLogger.isDebugEnabled();
    }

    public boolean isInfoEnabled()
    {
        return airliftLogger.isInfoEnabled();
    }

    public java.util.logging.Logger getJavaLogger() {
        return javaLogger;
    }
}
