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

import io.soabase.maple.api.*;
import io.soabase.maple.api.exceptions.MissingSchemaValueException;
import io.soabase.maple.core.Generator;

@SuppressWarnings("PMD.CollapsibleIfStatements")
public class StandardMapleSpi implements MapleSpi {
    private final Generator generator = new Generator();
    private volatile boolean productionMode = false;
    private volatile MapleFormatter formatter = Loaders.mapleFormatterLoader.instance();

    @Override
    public <T> MetaInstance<T> generate(Class<T> schemaClass) {
        Names names = StandardNamesBuilder.build(schemaClass, Generator.getReservedMethodNames());
        return generator.generate(names, schemaClass, ClassLoader.getSystemClassLoader(), formatter);
    }

    @Override
    public <T> void consume(LevelLogger levelLogger, String mainMessage, Throwable t, Statement<T> statement, MetaInstance<T> metaInstance) {
        NamesValues namesValues = statement.toNamesValues(metaInstance);
        validateRequired(namesValues);
        metaInstance.formatter().apply(levelLogger, namesValues, mainMessage, t);
    }

    @Override
    public void setProductionMode(boolean newValue) {
        productionMode = newValue;
    }

    @Override
    public boolean getProductionMode() {
        return productionMode;
    }

    @Override
    public void setFormatter(MapleFormatter formatter) {
        this.formatter = (formatter != null) ? formatter : Loaders.mapleFormatterLoader.instance();
    }

    @Override
    public MapleFormatter getFormatter() {
        return formatter;
    }

    @Override
    public void validateRequired(NamesValues namesValues) {
        if (!getProductionMode()) {
            for (int i = 0; i < namesValues.qty(); ++i) {
                if (namesValues.nthIsRequired(i)) {
                    if (namesValues.nthValue(i) == null) {
                        throw new MissingSchemaValueException("Entire schema must be specified. Missing: " + namesValues.nthName(i));
                    }
                }
            }
        }
    }
}
