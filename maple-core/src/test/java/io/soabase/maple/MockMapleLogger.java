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
package io.soabase.maple;

import io.soabase.maple.core.StandardMapleLogger;
import io.soabase.maple.spi.MapleSpi;
import io.soabase.maple.spi.MetaInstance;

import java.util.ArrayList;
import java.util.List;

class MockMapleLogger<T> extends StandardMapleLogger<T, Object> {
    private final List<LogEvent> logging;

    static <T> MockMapleLogger<T> get(Class<T> schemaClass) {
        MetaInstance<T> metaInstance = MapleSpi.instance().generate(schemaClass);
        final List<LogEvent> logging = new ArrayList<>();
        return new MockMapleLogger<>(metaInstance, logging);
    }

    List<LogEvent> logging() {
        return new ArrayList<>(logging);
    }

    void clear() {
        logging.clear();
    }

    private MockMapleLogger(MetaInstance<T> metaInstance, List<LogEvent> logging) {
        super(metaInstance, new Object(), (level, o) -> "dummy", (level, o) -> (msg, t) -> logging.add(new LogEvent(level, msg, t)));
        this.logging = logging;
    }
}
