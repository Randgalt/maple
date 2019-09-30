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
import io.soabase.maple.slf4j.MapleLogger;

import java.util.UUID;

import static com.myco.app.logging.LoggingEventType.CUSTOMER_REQUEST;
import static com.myco.app.logging.LoggingEventType.USER_REQUEST;

/**
 * Example of a request handler
 */
public class RequestHandler {
    private final MapleLogger<LoggingSchema> logger = Logging.get(getClass());

    public void processCustomerRequest(UUID requestId, UUID customerId) {
        logger.info(s -> s.event(CUSTOMER_REQUEST).requestId(requestId).customerId(customerId));

        // etc etc handle the request
    }

    public void processUserRequest(UUID requestId, UUID userId) {
        logger.info(s -> s.event(USER_REQUEST).requestId(requestId).userId(userId));

        // etc etc handle the request
    }
}
