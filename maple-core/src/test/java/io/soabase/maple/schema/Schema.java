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
package io.soabase.maple.schema;

import io.soabase.maple.Address;
import io.soabase.maple.api.annotations.Required;
import io.soabase.maple.api.annotations.SortOrder;

public interface Schema {
    @Required
    @SortOrder(1)
    Schema firstName(String s);

    @Required
    @SortOrder(2)
    Schema lastName(String s);

    default Schema name(String first, String last) {
        return firstName(first).lastName(last);
    }

    @SortOrder(4)
    Schema age(int n);

    @SortOrder(3)
    Schema address(Address address);
}
