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
package io.soabase.maple.api;

import io.soabase.maple.spi.MetaInstance;

import java.util.stream.Stream;

/**
 * Consumer of logging schema instances.
 */
@FunctionalInterface
public interface Statement<T> {
    void handle(T schemaInstance);

    /**
     * Chain statements together to achieve partial logging, composed logging, etc. E.g.
     *
     * <pre>
     * // partialLog is "pre filled" with fields/values
     * Statement&lt;LoggingSchema&gt; partialLog = s -&gt; s.event(UPDATE).customerId(customerId);
     *
     * ... later ...
     *
     * // partialLog is used along with the additional field "eventDetail"
     * logger.info(partialLog.concat(s -&gt; s.eventDetail("success"));
     * </pre>
     *
     * @param partialStatement - statement to concat
     * @return new composed statement
     */
    default Statement<T> concat(Statement<T> partialStatement) {
        return schemaInstance -> {
            handle(schemaInstance);
            partialStatement.handle(schemaInstance);
        };
    }

    /**
     * Utility - given a meta instance, apply this statement to produce a names/values instance
     *
     * @param metaInstance meta instance
     * @return names/values
     */
    default NamesValues toNamesValues(MetaInstance<T> metaInstance) {
        T instance = metaInstance.newSchemaInstance();
        handle(instance);
        return metaInstance.toNamesValues(instance);
    }

    /**
     * Utility, given a meta instance, apply this statement to produce a names/values instance
     * and return a stream of each name/value pair
     *
     * @param metaInstance meta instance
     * @return stream of names/values
     */
    default Stream<NameValue> stream(MetaInstance<T> metaInstance) {
        return toNamesValues(metaInstance).stream();
    }
}
