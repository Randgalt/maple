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
import io.soabase.maple.api.MapleFormatter;
import io.soabase.maple.spi.MapleSpi;
import io.soabase.maple.spi.MetaInstance;

/**
 * Main accessor for structured logging instances
 */
public class MapleFactory {
    /**
     * Return a structured logger by first calling {@link Logger#get(String)} and
     * then wrapping it in a {@link MapleLogger}
     *
     * @param name the name to use to get the logger
     * @param schemaClass logging schema
     * @return {@link MapleLogger}
     */
    public static <T> MapleLogger<T> getLogger(String name, Class<T> schemaClass) {
        java.util.logging.Logger javaLogger = java.util.logging.Logger.getLogger(name);
        Logger airliftLogger = Logger.get(name);
        return getLogger(new AirliftLogger(airliftLogger, javaLogger), schemaClass);
    }

    /**
     * Return a structured logger by first calling {@link Logger#get(Class)} and
     * then wrapping it in a {@link MapleLogger}
     *
     * @param clazz the class to use to get the logger
     * @param schemaClass logging schema
     * @return {@link MapleLogger}
     */
    public static <T> MapleLogger<T> getLogger(Class<?> clazz, Class<T> schemaClass) {
        return getLogger(clazz.getName(), schemaClass);
    }

    /**
     * Return a {@link MapleLogger} that wraps the given logger
     *
     * @param logger logger to wrap
     * @param schemaClass logging schema
     * @return {@link MapleLogger}
     */
    public static <T> MapleLogger<T> getLogger(AirliftLogger logger, Class<T> schemaClass) {
        MetaInstance<T> metaInstance = MapleSpi.instance().generate(schemaClass);
        return new MapleLoggerImpl<>(metaInstance, logger);
    }

    /**
     * Change the global production mode value. When production mode is {@code false}, schema methods
     * annotation with {@link io.soabase.maple.api.annotations.Required} with throw an exception
     * when they are {@code null}. When production mode is {@code true} this doesn't occur.
     *
     * @param newValue new production mode value
     */
    public static void setProductionMode(boolean newValue) {
        MapleSpi.instance().setProductionMode(newValue);
    }

    /**
     * Return the current value of production mode
     *
     * @return true/false
     */
    public static boolean getProductionMode() {
        return MapleSpi.instance().getProductionMode();
    }

    /**
     * Change the logging formatter in use.
     *
     * @param formatter new formatter. Pass {@code null} to use the default formatter
     */
    public static void setFormatter(MapleFormatter formatter) {
        MapleSpi.instance().setFormatter(formatter);
    }

    /**
     * Return the current logging formatter
     *
     * @return logging formatter
     */
    public static MapleFormatter getFormatter() {
        return MapleSpi.instance().getFormatter();
    }
}
