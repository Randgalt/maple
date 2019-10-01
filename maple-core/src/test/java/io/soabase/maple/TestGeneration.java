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

import io.soabase.maple.api.Names;
import io.soabase.maple.api.NamesValues;
import io.soabase.maple.api.Statement;
import io.soabase.maple.api.exceptions.InvalidSchemaException;
import io.soabase.maple.api.exceptions.MissingSchemaValueException;
import io.soabase.maple.core.Generator;
import io.soabase.maple.formatters.StandardFormatter;
import io.soabase.maple.schema.BasicSchema;
import io.soabase.maple.schema.HasRequired;
import io.soabase.maple.schema.HasSortOrder;
import io.soabase.maple.schema.MultiMethodDefaults;
import io.soabase.maple.schema.invalid.*;
import io.soabase.maple.spi.MapleSpi;
import io.soabase.maple.spi.MetaInstance;
import io.soabase.maple.spi.StandardNamesBuilder;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestGeneration {
    private final Generator generator = new Generator();
    private final StandardFormatter formatter = new StandardFormatter();

    @Test
    void testBasicSchemaGeneration() {
        Names names = buildNames(BasicSchema.class);
        MetaInstance<BasicSchema> metaInstance = generate(names, BasicSchema.class);
        assertThat(metaInstance.schemaNames()).isSameAs(names);
        assertThat(metaInstance.formatter().getClass()).isEqualTo(StandardFormatter.class);

        BasicSchema instance = metaInstance.newSchemaInstance();
        instance.name("test").qty(10);
        NamesValues namesValues = metaInstance.toNamesValues(instance);
        assertThat(namesValues.qty()).isEqualTo(2);
        assertThat(namesValues.nthName(0)).isEqualTo("name");
        assertThat(namesValues.nthName(1)).isEqualTo("qty");
        assertThat(namesValues.nthValue(0)).isEqualTo("test");
        assertThat(namesValues.nthValue(1)).isEqualTo(10);
    }

    @Test
    void testInvalidSchema() {
        Stream.of(BadReturnType.class,
                Duplicates.class,
                InvalidMethod.class,
                CannotBeAClass.class,
                MustTake1ArgumentB.class,
                MustReturnRightTypeInheritance.class
        ).forEach(clazz -> assertThatThrownBy(() -> buildNames(clazz)).isInstanceOf(InvalidSchemaException.class));
    }

    @Test
    void testMultiMethodDefaults() {
        Names names = buildNames(MultiMethodDefaults.class);
        MetaInstance<MultiMethodDefaults> metaInstance = generate(names, MultiMethodDefaults.class);

        MultiMethodDefaults instance = metaInstance.newSchemaInstance();
        instance.name("n").foo(10).name(10.20).name("hey");
        NamesValues namesValues = metaInstance.toNamesValues(instance);
        assertThat(namesValues.qty()).isEqualTo(1);
        assertThat(namesValues.nthName(0)).isEqualTo("name");
        assertThat(namesValues.nthValue(0)).isEqualTo("hey");

        instance = metaInstance.newSchemaInstance();
        instance.name("n").foo(10).name(10.20);
        namesValues = metaInstance.toNamesValues(instance);
        assertThat(namesValues.qty()).isEqualTo(1);
        assertThat(namesValues.nthName(0)).isEqualTo("name");
        assertThat(namesValues.nthValue(0)).isEqualTo("foo(double): " + 10.20);
    }

    @Test
    void testRequired() {
        Names names = buildNames(HasRequired.class);
        assertThat(names.nthIsRequired(1)).isTrue();

        MetaInstance<HasRequired> metaInstance = generate(names, HasRequired.class);
        HasRequired instance = metaInstance.newSchemaInstance();
        instance.name("test");

        boolean saveProductionMode = MapleSpi.instance().getProductionMode();
        try {
            MapleSpi.instance().setProductionMode(false);
            NamesValues namesValues = metaInstance.toNamesValues(instance);
            assertThatThrownBy(() -> MapleSpi.instance().validateRequired(namesValues)).isInstanceOf(MissingSchemaValueException.class);
        } catch (Exception e) {
            MapleSpi.instance().setProductionMode(saveProductionMode);
        }
    }

    @Test
    void testSortOrder() {
        Names names = buildNames(HasSortOrder.class);
        assertThat(names.qty()).isEqualTo(4);
        assertThat(names.nthName(0)).isEqualTo("d");
        assertThat(names.nthName(1)).isEqualTo("a");
        assertThat(names.nthName(2)).isEqualTo("b");
        assertThat(names.nthName(3)).isEqualTo("c");
    }

    @Test
    void testStreamingNamesValues() {
        Names names = buildNames(BasicSchema.class);
        MetaInstance<BasicSchema> metaInstance = generate(names, BasicSchema.class);
        Statement<BasicSchema> statement = s -> s.name("n").qty(10);
        String result = statement.toNamesValues(metaInstance).stream()
                .map(nameValue -> nameValue.name() + "=" + nameValue.value())
                .collect(Collectors.joining("|"));
        assertThat(result).isEqualTo("name=n|qty=10");
    }

    private <T> MetaInstance<T> generate(Names names, Class<T> clazz) {
        return generator.generate(names, clazz, ClassLoader.getSystemClassLoader(), formatter);
    }

    private Names buildNames(Class<?> clazz) {
        return StandardNamesBuilder.build(clazz, Generator.getReservedMethodNames());
    }
}
