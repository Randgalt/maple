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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import io.soabase.maple.api.LevelLogger;
import io.soabase.maple.api.MapleFormatter;
import io.soabase.maple.api.NameValue;
import io.soabase.maple.api.NamesValues;

import java.util.*;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.node.JsonNodeType.*;

/**
 * <p>
 * Formats all schema arguments as flattened model values. All arguments are passed to
 * a provided Jackson {@link ObjectMapper} to serialize to a tree. The tree components
 * are flattened into schema values.
 * </p>
 *
 * <p>
 * E.g. for this model and schema:
 * </p>
 *
 * <pre>
 * public class Model {
 *     String name;
 *     List&lt;String&gt; values;
 *     int qty;
 * }
 *
 * ...
 *
 * public interface Schema {
 *     Schema code(String c);
 *
 *     Schema model(Model m);
 * }
 *
 * ...
 *
 * Model modelInstance = new Model("Joe", List.of("one", "two", "three"), 100);
 * log.info(s -&gt; s.code("123").model(modelInstance));
 * </pre>
 *
 * <p>
 * The above will log something like:
 * </p>
 *
 * <pre>
 * code="123" model.name="Joe" model.values.0="one" model.values.1="two" model.values.2="three" model.qty="100"
 * </pre>
 */
@SuppressWarnings("PMD.UnusedFormalParameter")
public class ModelFormatter implements MapleFormatter {
    private final MapleFormatter formatter;
    private final NodeMapper mapper;
    private final Map<JsonNodeType, Handler> handlers;
    private final String separator;

    @FunctionalInterface
    private interface Handler {
        void handle(String name, JsonNode node, List<String> appliedNames, List<Object> appliedValues);
    }

    @FunctionalInterface
    public interface NodeMapper {
        <T extends JsonNode> T valueToTree(Object fromValue);

        static NodeMapper forMapper(ObjectMapper mapper) {
            return mapper::valueToTree;
        }
    }

    public ModelFormatter(NodeMapper mapper, StandardFormatter.Option... options) {
        this(mapper, ".", options);
    }

    public ModelFormatter(NodeMapper mapper, String separator, StandardFormatter.Option... options) {
        this(mapper, separator, new StandardFormatter(options));
    }

    public ModelFormatter(NodeMapper mapper, StandardFormatter formatter) {
        this(mapper, ".", formatter);
    }

    public ModelFormatter(NodeMapper mapper, String separator, MapleFormatter formatter) {
        this.formatter = formatter;
        this.mapper = mapper;
        this.separator = separator;

        Map<JsonNodeType, Handler> map = new HashMap<>();
        map.put(ARRAY, this::handleArray);
        map.put(OBJECT, this::handleObject);
        map.put(STRING, this::handleString);
        map.put(NUMBER, this::handleNumber);
        map.put(BOOLEAN, this::handleBoolean);
        map.put(NULL, this::handleNull);
        handlers = Collections.unmodifiableMap(map);
    }

    @Override
    public void apply(LevelLogger logger, NamesValues namesValues, String mainMessage, Throwable t) {
        List<String> appliedNames = new ArrayList<>();
        List<Object> appliedValues = new ArrayList<>();
        for (int i = 0; i < namesValues.qty(); ++i) {
            String schemaName = namesValues.nthName(i);
            Object argument = namesValues.nthValue(i);
            JsonNode node = mapper.valueToTree(argument);
            getHandler(node).handle(schemaName, node, appliedNames, appliedValues);
        }
        NamesValues applied = new NamesValues() {
            @Override
            public int qty() {
                return appliedNames.size();
            }

            @Override
            public String nthName(int n) {
                return appliedNames.get(n);
            }

            @Override
            public boolean nthIsRequired(int n) {
                return false;
            }

            @Override
            public Object nthValue(int n) {
                return appliedValues.get(n);
            }

            @Override
            public Stream<NameValue> stream() {
                return Stream.empty();
            }
        };
        formatter.apply(logger, applied, mainMessage, t);
    }

    private Handler getHandler(JsonNode node) {
        JsonNodeType nodeType = (node != null) ? node.getNodeType() : NULL;
        return handlers.getOrDefault(nodeType, this::handleString);
    }

    private void handleString(String schemaName, JsonNode node, List<String> appliedNames, List<Object> appliedValues) {
        appliedNames.add(schemaName);
        appliedValues.add(node.asText());
    }

    private void handleNumber(String schemaName, JsonNode node, List<String> appliedNames, List<Object> appliedValues) {
        NumericNode numericNode = (NumericNode) node;
        appliedNames.add(schemaName);
        appliedValues.add(numericNode.numberValue());
    }

    private void handleBoolean(String schemaName, JsonNode node, List<String> appliedNames, List<Object> appliedValues) {
        BooleanNode booleanNode = (BooleanNode) node;
        appliedNames.add(schemaName);
        appliedValues.add(booleanNode.booleanValue());
    }

    private void handleNull(String schemaName, JsonNode node, List<String> appliedNames, List<Object> appliedValues) {
        appliedNames.add(schemaName);
        appliedValues.add(null);
    }

    private void handleArray(String schemaName, JsonNode node, List<String> appliedNames, List<Object> appliedValues) {
        ArrayNode arrayNode = (ArrayNode) node;
        for (int i = 0; i < arrayNode.size(); ++i) {
            JsonNode field = arrayNode.get(i);
            getHandler(field).handle(schemaName + separator + i, field, appliedNames, appliedValues);
        }
    }

    private void handleObject(String schemaName, JsonNode node, List<String> appliedNames, List<Object> appliedValues) {
        ObjectNode objectNode = (ObjectNode) node;
        objectNode.fields().forEachRemaining(entry -> {
            String name = entry.getKey();
            JsonNode field = entry.getValue();
            getHandler(field).handle(schemaName + separator + name, field, appliedNames, appliedValues);
        });
    }
}
