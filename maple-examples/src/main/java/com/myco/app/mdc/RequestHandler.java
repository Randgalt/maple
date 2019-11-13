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
package com.myco.app.mdc;

import com.myco.app.logging.Logging;
import com.myco.app.logging.LoggingEventType;
import com.myco.app.logging.LoggingSchema;
import io.soabase.maple.slf4j.MapleLogger;

public class RequestHandler {
    private final MapleLogger<LoggingSchema> log = Logging.get(getClass());

    public void handleRequest(Request request) {
        // normal logging. The underlying logging framework will add the MDC values
        log.info(s -> s.event(LoggingEventType.USER_REQUEST).eventDetail(request.getName()).customerId(request.getId()));

        // etc.
    }
}
