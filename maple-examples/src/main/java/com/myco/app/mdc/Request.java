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

import java.util.UUID;

public class Request {
    private final TransactionMetaData metaData;
    private final String name;
    private final UUID id;

    public Request(TransactionMetaData metaData, String name, UUID id) {
        this.metaData = metaData;
        this.name = name;
        this.id = id;
    }

    public TransactionMetaData getMetaData() {
        return metaData;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}
