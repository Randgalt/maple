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
package io.soabase.maple.spi;

import io.soabase.maple.api.*;

public interface MapleSpi {
    static MapleSpi instance() {
        return Loaders.mapleSpiLoader.instance();
    }

    <T> void consume(LevelLogger levelLogger, LoggingLevel loggingLevel, String loggerName, String mainMessage, Throwable t, Statement<T> statement, MetaInstance<T> metaInstance);

    <T> MetaInstance<T> generate(Class<T> schemaClass);

    void setProductionMode(boolean newValue);

    boolean getProductionMode();

    void setFormatter(MapleFormatter formatter);

    MapleFormatter getFormatter();

    NamesValues applySpecializations(NamesValues namesValues);

    default void reset() {
        setFormatter(null);
    }

    default Object getMdcValue(String name) {
        return null;
    }

    default void putMdcValue(String name, Object value) {
        // NOP
    }

    default void removeMdcValue(String name) {
        // NOP
    }
}
