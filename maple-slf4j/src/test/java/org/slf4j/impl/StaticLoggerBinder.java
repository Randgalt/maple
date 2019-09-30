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
package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.spi.LoggerFactoryBinder;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class StaticLoggerBinder implements LoggerFactoryBinder, ILoggerFactory {
    private static final StaticLoggerBinder instance = new StaticLoggerBinder();
    private static final Queue<SubstituteLoggingEvent> eventQueue = new LinkedBlockingQueue<>();

    public static StaticLoggerBinder getSingleton() {
        return instance;
    }

    public static Queue<SubstituteLoggingEvent> getEventQueue() {
        return eventQueue;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return this;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return this.getClass().getName();
    }

    @Override
    public Logger getLogger(String name) {
        return new SubstituteLogger(name, eventQueue, false);
    }
}
