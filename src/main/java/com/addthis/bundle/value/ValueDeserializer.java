/*
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
package com.addthis.bundle.value;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ValueDeserializer extends StdDeserializer<ValueObject> {

    public ValueDeserializer() {
        super(ValueObject.class);
    }

    @Override
    public ValueObject deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        switch (t) {
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_STRING:
                return jp.readValueAs(ValueString.class);
            case VALUE_NUMBER_INT:
                return jp.readValueAs(ValueLong.class);
            case VALUE_NUMBER_FLOAT:
                return jp.readValueAs(ValueDouble.class);
            case START_ARRAY:
                return jp.readValueAs(ValueArray.class);
            case START_OBJECT:
            case FIELD_NAME:
                return jp.readValueAs(ValueMap.class);
            case END_OBJECT:
                // calling jp.readValueAs here will return null rather than an empty map, so make empty map in tokens
                jp.nextToken();
                ObjectNode objectNode = ctxt.getNodeFactory().objectNode();
                JsonParser emptyObjectParser = jp.getCodec().treeAsTokens(objectNode);
                emptyObjectParser.nextToken();
                return emptyObjectParser.readValueAs(ValueMap.class);
            case VALUE_EMBEDDED_OBJECT:
            default:
                throw ctxt.mappingException(handledType());
        }
    }
}
