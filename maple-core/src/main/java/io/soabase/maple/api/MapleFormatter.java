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

/**
 * Responsible for taking generated name/value pairs and calling the underlying logger
 */
@FunctionalInterface
public interface MapleFormatter {
    /**
     * Apply generated name/value pairs and calling the underlying logger
     *
     * @param logger the logger proxy
     * @param namesValues name/value pairs
     * @param mainMessage the main message to output or {@code ""}
     * @param t the exception to output or {@code null}
     */
    void apply(LevelLogger logger, NamesValues namesValues, String mainMessage, Throwable t);
}
