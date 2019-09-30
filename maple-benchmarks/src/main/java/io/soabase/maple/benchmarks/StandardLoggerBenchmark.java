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

import org.openjdk.jmh.annotations.Benchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardLoggerBenchmark {
    private static final Logger savedLogger = getLogger();

    @Benchmark
    public void testFreshLogger() {
        testAllLevels(getLogger());
    }

    @Benchmark
    public void testSavedLogger() {
        testAllLevels(savedLogger);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(StandardLoggerBenchmark.class);
    }

    private void testAllLevels(Logger logger) {
        logger.trace("message id={} qty={}", Utils.str(), Utils.value());
        logger.warn("message id={} qty={}", Utils.str(), Utils.value());
        logger.debug("message id={} qty={}", Utils.str(), Utils.value());
        logger.error("message id={} qty={}", Utils.str(), Utils.value());
        logger.info("message id={} qty={}", Utils.str(), Utils.value());
    }
}
