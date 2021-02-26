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
package io.soabase.maple.formatters;

import io.soabase.maple.api.LoggingLevel;
import io.soabase.maple.api.MapleFormatter;
import io.soabase.maple.api.NamesValues;
import io.soabase.maple.api.LevelLogger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static io.soabase.maple.formatters.StandardFormatter.Option.*;

@SuppressWarnings({"PMD.CollapsibleIfStatements", "PMD.UselessParentheses"})
public class StandardFormatter implements MapleFormatter {
    private final boolean mainMessageIsLast;
    private final boolean quoteValues;
    private final boolean quoteValuesOnlyIfNeeded;
    private final boolean escapeValues;
    private final boolean snakeCase;
    private final boolean skipNullValues;

    public static final char SPACE = ' ';
    public static final char QUOTE = '"';
    public static final char ESCAPE = '\\';
    public static final char RETURN = '\r';
    public static final char NEWLINE = '\n';
    public static final char UNDERSCORE = '_';
    public static final char TAB = '\t';
    public static final char[] ESCAPED_ESCAPE = {ESCAPE, ESCAPE};
    public static final char[] ESCAPED_QUOTE = {ESCAPE, QUOTE};
    public static final char[] ESCAPED_SPACE = {ESCAPE, SPACE};

    public static final int STRING_BUILDER_CAPACITY = 128;

    public enum Option {
        MAIN_MESSAGE_IS_LAST,       // main message when provided is formatted to the end of the string
        QUOTE_VALUES,               // all values are quoted
        ESCAPE_VALUES,              // special characters in values are escaped
        SNAKE_CASE,                 // names are reformatted to snake case
        QUOTE_VALUES_IF_NEEDED,    // quote values only if the contain spaces, special characters, etc.
        SKIP_NULL_VALUES           // if a value is null for a given name don't output anything
    }

    public StandardFormatter(Option... options) {
        Collection<Option> optionsSet = new HashSet<>(Arrays.asList(options));

        this.mainMessageIsLast = optionsSet.contains(MAIN_MESSAGE_IS_LAST);
        this.quoteValuesOnlyIfNeeded = optionsSet.contains(QUOTE_VALUES_IF_NEEDED);
        this.quoteValues = optionsSet.contains(QUOTE_VALUES);
        this.escapeValues = optionsSet.contains(ESCAPE_VALUES);
        this.snakeCase = optionsSet.contains(SNAKE_CASE);
        this.skipNullValues = optionsSet.contains(SKIP_NULL_VALUES);
        if (quoteValuesOnlyIfNeeded && quoteValues) {
            throw new IllegalArgumentException("QUOTE_VALUES and QUOTE_VALUES_IF_NEEDED cannot be combined");
        }
        if (quoteValuesOnlyIfNeeded && !escapeValues) {
            throw new IllegalArgumentException("QUOTE_VALUES_IF_NEEDED requires ESCAPE_VALUES");
        }
    }

    @Override
    public void apply(LevelLogger logger, LoggingLevel loggingLevel, String loggerName, NamesValues namesValues, String mainMessage, Throwable t) {
        StringBuilder logMessage = new StringBuilder(STRING_BUILDER_CAPACITY);
        boolean needsSpace = false;
        boolean hasMainMessage = !mainMessage.isEmpty();
        if (hasMainMessage) {
            if (!mainMessageIsLast) {
                needsSpace = true;
                logMessage.append(mainMessage);
            }
        }

        for (int i = 0; i < namesValues.qty(); ++i) {
            Object value = namesValues.nthValue(i);
            if (skipNullValues && (value == null)) {
                continue;
            }
            if (needsSpace) {
                logMessage.append(SPACE);
            } else {
                needsSpace = true;
            }
            formatSchemaName(logMessage, namesValues.nthName(i));
            logMessage.append('=');
            if (quoteValues) {
                logMessage.append(QUOTE);
            }
            if (escapeValues) {
                if (quoteValuesOnlyIfNeeded) {
                    StringBuilder escapedValue = new StringBuilder(STRING_BUILDER_CAPACITY);
                    if (addEscapedValue(escapedValue, value, false)) {
                        logMessage.append(QUOTE).append(escapedValue).append(QUOTE);
                    } else {
                        logMessage.append(escapedValue);
                    }
                } else {
                    addEscapedValue(logMessage, value, !quoteValues);
                }
            } else {
                logMessage.append(value);
            }
            if (quoteValues) {
                logMessage.append(QUOTE);
            }
        }

        if (hasMainMessage && mainMessageIsLast) {
            if (namesValues.qty() > 0) {
                logMessage.append(SPACE);
            }
            logMessage.append(mainMessage);
        }
        logger.log(logMessage.toString(), t);
    }

    // mostly copied from PropertyNamingStrategy#SnakeCaseStrategy#translate()
    public static void toSnakeCase(StringBuilder logMessage, String name) {
        boolean wasPrevTranslated = false;
        int logMessageLength = logMessage.length();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (i > 0 || c != '_')  { // skip first starting underscore
                if (Character.isUpperCase(c)) {
                    if (!wasPrevTranslated && (logMessageLength > 0) && (logMessage.charAt(logMessageLength - 1) != '_')) {
                        logMessage.append(UNDERSCORE);
                        logMessageLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                } else {
                    wasPrevTranslated = false;
                }
                logMessage.append(c);
                logMessageLength++;
            }
        }
    }

    private void formatSchemaName(StringBuilder logMessage, String name) {
        if (snakeCase) {
            toSnakeCase(logMessage, name);
        } else {
            logMessage.append(name);
        }
    }

    // note: this method has been manually optimized after profiling
    public static boolean addEscapedValue(StringBuilder logMessage, Object value, boolean escapeSpaces) {
        boolean hasEscapes = false;
        char[] str = String.valueOf(value).toCharArray();
        int currentStart = 0;
        for (int i = 0; i < str.length; ++i) {
            char c = str[i];
            switch (str[i]) {
                case QUOTE: {
                    hasEscapes = true;
                    logMessage.append(str, currentStart, i - currentStart);
                    currentStart = i + 1;
                    logMessage.append(ESCAPED_QUOTE);
                    break;
                }

                case ESCAPE: {
                    hasEscapes = true;
                    logMessage.append(str, currentStart, i - currentStart);
                    currentStart = i + 1;
                    logMessage.append(ESCAPED_ESCAPE);
                    break;
                }

                case SPACE:
                case RETURN:
                case NEWLINE:
                case TAB: {
                    hasEscapes = true;
                    if (escapeSpaces) {
                        logMessage.append(str, currentStart, i - currentStart);
                        currentStart = i + 1;
                        logMessage.append(ESCAPED_SPACE);
                    } else if (c != SPACE) {
                        logMessage.append(str, currentStart, i - currentStart);
                        currentStart = i + 1;
                        logMessage.append(SPACE);
                    }
                    break;
                }
            }
        }
        logMessage.append(str, currentStart, str.length - currentStart);
        return hasEscapes;
    }
}
