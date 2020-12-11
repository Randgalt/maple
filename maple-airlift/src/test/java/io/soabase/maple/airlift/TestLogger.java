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
package io.soabase.maple.airlift;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class TestLogger extends Logger {
    private final List<Event> events = new CopyOnWriteArrayList<>();

    static class Event {
        final Level level;
        final String message;

        Event(Level level, String message) {
            this.level = level;
            this.message = message;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "level=" + level +
                    ", message='" + message + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Event event = (Event) o;
            return level.equals(event.level) &&
                    message.equals(event.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, message);
        }
    }

    TestLogger(String name) {
        super(name, null);
    }

    @Override
    public void log(Level level, String msg) {
        events.add(new Event(level, msg));
    }

    List<Event> getAndClearEvents() {
        List<Event> result = new ArrayList<>(events);
        events.clear();
        return result;
    }
}
