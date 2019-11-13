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

import io.soabase.maple.api.MdcCloseable;
import io.soabase.maple.slf4j.MapleFactory;
import io.soabase.maple.slf4j.MapleLogger;

public class RestEndpoint {
    // uses a specialized logging schema solely for MDC
    private final MapleLogger<MdcSchema> log = MapleFactory.getLogger(getClass(), MdcSchema.class);

    public void putRequest(Request request) {
        // sets the MDC context for the request (automatically clearing using try-with-resources)
        // given MdcSchema, the MDC fields are "transactionId" and "transactionOwner"
        // see logback.xml for the logging spec
        try (MdcCloseable mdc = log.mdc(s -> s.transactionId(request.getMetaData().getId()).transactionOwner(request.getMetaData().getOwner())) ) {
            new RequestHandler().handleRequest(request);
        }
    }
}
