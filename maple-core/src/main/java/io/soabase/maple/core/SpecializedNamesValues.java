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

import io.soabase.maple.api.NameValue;
import io.soabase.maple.api.Names;
import io.soabase.maple.api.NamesValues;
import io.soabase.maple.api.Specialization;

import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import static io.soabase.maple.core.NamesValuesImp.newStream;

public class SpecializedNamesValues implements NamesValues {
    private final Names names;
    private final IntFunction<Object> valueProc;

    public SpecializedNamesValues(Names names, IntFunction<Object> valueProc) {
        this.names = names;
        this.valueProc = valueProc;
    }

    @Override
    public int qty() {
        return names.qty();
    }

    @Override
    public String nthName(int n) {
        return names.nthName(n);
    }

    @Override
    public Object nthValue(int n) {
        return valueProc.apply(n);
    }

    @Override
    public Set<Specialization> nthSpecializations(int n) {
        return names.nthSpecializations(n);
    }

    @Override
    public Stream<NameValue> stream() {
        return newStream(this);
    }
}
