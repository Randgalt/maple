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

import io.soabase.maple.core.StandardMapleLogger;
import io.soabase.maple.spi.MetaInstance;

class MapleLoggerImpl<T> extends StandardMapleLogger<T, AirliftLogger> implements MapleLogger<T> {
    MapleLoggerImpl(MetaInstance<T> metaInstance, AirliftLogger logger) {
        super(metaInstance, logger, Utils::isEnabledLoggerName, Utils::levelLogger);
    }
}
