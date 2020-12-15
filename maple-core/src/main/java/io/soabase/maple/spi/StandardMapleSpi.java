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
import io.soabase.maple.core.SpecializedNamesValues;

@SuppressWarnings({"PMD.CollapsibleIfStatements", "PMD.UselessParentheses"})
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
    public <T> void consume(LevelLogger levelLogger, String loggerName, String mainMessage, Throwable t, Statement<T> statement, MetaInstance<T> metaInstance) {
        NamesValues namesValues = applySpecializations(statement.toNamesValues(metaInstance));
        metaInstance.formatter().apply(levelLogger, loggerName, namesValues, mainMessage, t);
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
    public NamesValues applySpecializations(NamesValues namesValues) {
        NamesValues specializedNamesValues = null;
        for (int i = 0; i < namesValues.qty(); ++i) {
            if (!productionMode && namesValues.nthSpecializations(i).contains(Specialization.REQUIRED)) {
                if (namesValues.nthValue(i) == null) {
                    throw new MissingSchemaValueException("Entire schema must be specified. Missing: " + namesValues.nthName(i));
                }
            } else if ((specializedNamesValues == null) && namesValues.nthSpecializations(i).contains(Specialization.DEFAULT_FROM_MDC)) {
                specializedNamesValues = new SpecializedNamesValues(namesValues, index -> getSpecializedValue(namesValues, index));
            }
        }
        return (specializedNamesValues != null) ? specializedNamesValues : namesValues;
    }

    private Object getSpecializedValue(NamesValues namesValues, int index) {
        Object value = namesValues.nthValue(index);
        if ((value == null) && namesValues.nthSpecializations(index).contains(Specialization.DEFAULT_FROM_MDC)) {
            return getMdcValue(namesValues.nthName(index));
        }
        return value;
    }
}
