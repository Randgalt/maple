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
package io.soabase.maple.spi;

import io.soabase.maple.api.MapleFormatter;
import io.soabase.maple.formatters.StandardFormatter;

import static io.soabase.maple.formatters.StandardFormatter.Option.*;

class Loaders {
    // must be defined before mapleSpiLoader
    static final Loader<MapleFormatter> mapleFormatterLoader = new Loader<>(MapleFormatter.class, new StandardFormatter(MAIN_MESSAGE_IS_LAST, SNAKE_CASE, ESCAPE_VALUES, QUOTE_VALUES_IF_NEEDED, SKIP_NULL_VALUES));
    static final Loader<MapleSpi> mapleSpiLoader = new Loader<>(MapleSpi.class, new StandardMapleSpi());
}
