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

import io.soabase.maple.api.*;
import io.soabase.maple.spi.MapleSpi;
import io.soabase.maple.spi.MetaInstance;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class StandardMapleLogger<T, LOGGER> implements MapleLoggerBase<T> {
    private final MetaInstance<T> metaInstance;
    private final LOGGER logger;
    private final BiFunction<LoggingLevel, LOGGER, LevelLogger> levelLoggerProc;
    private final BiPredicate<LoggingLevel, LOGGER> isEnabledProc;

    public StandardMapleLogger(MetaInstance<T> metaInstance,
                               LOGGER logger,
                               BiPredicate<LoggingLevel, LOGGER> isEnabledProc,
                               BiFunction<LoggingLevel, LOGGER, LevelLogger> levelLoggerProc) {
        this.metaInstance = metaInstance;
        this.logger = logger;
        this.isEnabledProc = isEnabledProc;
        this.levelLoggerProc = levelLoggerProc;
    }

    public LOGGER logger() {
        return logger;
    }

    @Override
    public void consume(LoggingLevel loggingLevel, String mainMessage, Throwable t, Statement<T> statement) {
        if (isEnabledProc.test(loggingLevel, logger)) {
            MapleSpi.instance().consume(levelLoggerProc.apply(loggingLevel, logger), mainMessage, t, statement, metaInstance);
        }
    }

    @Override
    public MdcCloseable mdc(Statement<T> statement) {
        NamesValues namesValues = statement.toNamesValues(getMetaInstance());
        namesValues.stream().forEach(nameValue -> MapleSpi.instance().putMdcValue(nameValue.name(), String.valueOf(nameValue.value())));
        return () -> namesValues.stream().forEach(nameValue -> MapleSpi.instance().removeMdcValue(nameValue.name()));
    }

    public MetaInstance<T> getMetaInstance() {
        return metaInstance;
    }
}
