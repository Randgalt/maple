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

import io.soabase.maple.api.NamesValues;
import io.soabase.maple.api.Statement;
import io.soabase.maple.core.StandardMapleLogger;
import io.soabase.maple.spi.MetaInstance;
import org.slf4j.Logger;
import org.slf4j.MDC;

class MapleLoggerImpl<T> extends StandardMapleLogger<T, Logger> implements MapleLogger<T> {
    MapleLoggerImpl(MetaInstance<T> metaInstance, Logger logger) {
        super(metaInstance, logger, Utils::isEnabled, Utils::levelLogger);
    }

    @Override
    public MdcCloseable mdc(Statement<T> statement) {
        NamesValues namesValues = statement.toNamesValues(getMetaInstance());
        namesValues.stream().forEach(nameValue -> MDC.put(nameValue.name(), String.valueOf(nameValue.value())));
        return () -> namesValues.stream().forEach(nameValue -> MDC.remove(nameValue.name()));
    }
}
