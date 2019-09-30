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
package io.soabase.maple;

import io.soabase.maple.api.LoggingLevel;

class LogEvent {
    final LoggingLevel level;
    final String message;
    final Throwable t;

    LogEvent(LoggingLevel level, String message, Throwable t) {
        this.level = level;
        this.message = message;
        this.t = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEvent logEvent = (LogEvent) o;

        if (level != logEvent.level) return false;
        if (message != null ? !message.equals(logEvent.message) : logEvent.message != null) return false;
        return t != null ? t.equals(logEvent.t) : logEvent.t == null;
    }

    @Override
    public int hashCode() {
        int result = level != null ? level.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (t != null ? t.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "level=" + level +
                ", message='" + message + '\'' +
                ", t=" + t +
                '}';
    }
}
