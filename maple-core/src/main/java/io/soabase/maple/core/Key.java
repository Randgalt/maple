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
package io.soabase.maple.core;

import io.soabase.maple.api.MapleFormatter;

// TODO(test different combinations of schema class and logging formatter)
class Key {
    private final Class clazz;
    private final MapleFormatter formatter;
    private final int hash;

    Key(Class clazz, MapleFormatter formatter) {
        this.clazz = clazz;
        this.formatter = formatter;
        hash = calcHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        Key key = (Key) o;

        if (!clazz.equals(key.clazz)){
            return false;
        }
        return formatter.equals(key.formatter);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private int calcHash() {
        int result = clazz.hashCode();
        result = 31 * result + formatter.hashCode();
        return result;
    }
}
