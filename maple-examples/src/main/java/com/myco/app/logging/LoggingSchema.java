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

import com.myco.app.request.PayloadModel;
import io.soabase.maple.api.annotations.Required;
import io.soabase.maple.api.annotations.SortOrder;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * An "uber" logging schema. Has all the fields/methods for your project.
 * {@link Logging} sets {@link io.soabase.maple.formatters.StandardFormatter.Option#SKIP_NULL_VALUES} therefore unspecified
 * schema methods are ignored (i.e. not output to the SLF4J)
 */
public interface LoggingSchema {
    @Required           // causes event() to be required
    @SortOrder(0)       // causes event() to be output first
    LoggingSchema event(LoggingEventType type);

    @SortOrder(1)       // causes eventDetail() to be output second if used
    LoggingSchema eventDetail(String detail);

    LoggingSchema userId(UUID id);

    LoggingSchema requestId(UUID id);

    LoggingSchema customerId(UUID id);

    LoggingSchema payload(PayloadModel payload);

    LoggingSchema duration(Duration duration);

    // example of using default methods to add custom behavior
    default LoggingSchema duration(Instant start, Instant end) {
        return duration(Duration.between(start, end));
    }
}
