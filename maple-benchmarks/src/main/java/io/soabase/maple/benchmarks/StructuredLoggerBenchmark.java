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
package io.soabase.maple.benchmarks;

import io.soabase.maple.slf4j.MapleFactory;
import io.soabase.maple.slf4j.MapleLogger;
import org.openjdk.jmh.annotations.Benchmark;

public class StructuredLoggerBenchmark {
    private static final MapleLogger<Schema> savedLogger = getLogger();

    @Benchmark
    public void testFreshLogger() {
        testAllLevels(getLogger());
    }

    @Benchmark
    public void testSavedLogger() {
        testAllLevels(savedLogger);
    }

    private static MapleLogger<Schema> getLogger() {
        return MapleFactory.getLogger(StructuredLoggerBenchmark.class, Schema.class);
    }

    private void testAllLevels(MapleLogger<Schema> logger) {
        logger.trace("message", schema -> schema.id(Utils.str()).qty(Utils.value()));
        logger.warn("message", schema -> schema.id(Utils.str()).qty(Utils.value()));
        logger.debug("message", schema -> schema.id(Utils.str()).qty(Utils.value()));
        logger.error("message", schema -> schema.id(Utils.str()).qty(Utils.value()));
        logger.info("message", schema -> schema.id(Utils.str()).qty(Utils.value()));
    }
}
