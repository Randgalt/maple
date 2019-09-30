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

import io.soabase.maple.api.annotations.DoNotLog;

import java.util.UUID;

/**
 * An example model. When {@link io.soabase.maple.formatters.ModelFormatter} or your own custom formatter
 * that uses Jackson is used, the {@link DoNotLog} annotation can be used to prevent logging of sensitive information
 */
public class PayloadModel {
    private final UUID payloadId;
    private final String userSecretToken;
    private final String data;

    public PayloadModel(UUID payloadId, String userSecretToken, String data) {
        this.payloadId = payloadId;
        this.userSecretToken = userSecretToken;
        this.data = data;
    }

    public UUID getPayloadId() {
        return payloadId;
    }

    @DoNotLog   // userSecretToken will not be logged
    public String getUserSecretToken() {
        return userSecretToken;
    }

    public String getData() {
        return data;
    }
}
