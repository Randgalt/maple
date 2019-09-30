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
package com.myco.app.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.soabase.maple.api.MapleFormatter;
import io.soabase.maple.formatters.DoNotLogAnnotationIntrospector;
import io.soabase.maple.formatters.ModelFormatter;
import io.soabase.maple.slf4j.MapleFactory;
import io.soabase.maple.slf4j.MapleLogger;
import io.soabase.maple.spi.MapleSpi;

import static io.soabase.maple.formatters.StandardFormatter.Option.*;

/**
 * A factory utility for accessing/creating structured logging instances in your project
 */
public class Logging {
    static {
        // do required value checks for tests - reset to true in your Application's main method so that tests are not done in Production
        MapleSpi.instance().setProductionMode(false);

        // typically, you'll have an internal library that generates an ObjectMapper - ideally, you'd use an instance from that
        ObjectMapper mapper = new ObjectMapper();

        // make the @DoNotLog annotation available for your models
        DoNotLogAnnotationIntrospector.register(mapper);

        // use the ModelFormatter instead of the default StandardFormatter
        MapleFormatter loggingFormatter = new ModelFormatter(ModelFormatter.NodeMapper.forMapper(mapper),
                SNAKE_CASE,              // outputs field names in snake_case_format
                QUOTE_VALUES_IF_NEEDED, // quotes values only if they have a space
                ESCAPE_VALUES,           // escapes quotes, backslashes, etc.
                SKIP_NULL_VALUES         // don't output anything if the schema value is null
        );
        MapleSpi.instance().setFormatter(loggingFormatter);
    }

    /**
     * @param clazz the returned logger will be named after clazz - passed to {@link org.slf4j.LoggerFactory#getLogger(Class)}
     * @return structured logger
     */
    public static MapleLogger<LoggingSchema> get(Class<?> clazz) {
        return MapleFactory.getLogger(clazz, LoggingSchema.class);
    }
}
