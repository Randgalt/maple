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
package com.myco.app.request;

import com.myco.app.logging.Logging;
import com.myco.app.logging.LoggingSchema;
import io.soabase.maple.api.Statement;
import io.soabase.maple.slf4j.MapleLogger;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.myco.app.logging.LoggingEventType.UPDATE;

public class UpdateService {
    private final MapleLogger<LoggingSchema> logger = Logging.get(getClass());

    public void doUpdate(UUID customerId, PayloadModel payload) {
        // An example of composed logging. partialLog is "pre filled" with fields/values
        Statement<LoggingSchema> partialLog = s -> s.event(UPDATE).customerId(customerId);
        try {
            Instant start = Instant.now();
            writeToDataSource(customerId, payload);
            // partialLog is used along with the additional fields "eventDetail", "duration" and "payload"
            // NOTE: PayloadModel#userSecretToken is NOT logged due to the @DoNotLog annotation
            logger.info(partialLog.concat(s -> s.eventDetail("success")
                    .duration(start, Instant.now()) // default method that calcs the duration and calls the schema method with it
                    .payload(payload)
            ));
        } catch (Exception e) {
            // partialLog is used along with the additional fields "eventDetail" and "payload"
            // NOTE: PayloadModel#userSecretToken is NOT logged due to the @DoNotLog annotation
            logger.error(e, partialLog.concat(s -> s.eventDetail("failure").payload(payload)));
        }
    }

    private void writeToDataSource(UUID customerId, PayloadModel payload) {
        // etc
    }
}
