/*-
 * #%L
 * Spring HATEOAS Siren
 * %%
 * Copyright (C) 2018 - 2020 Ingo Griebsch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.ingogriebsch.spring.hateoas.siren;

import static java.lang.String.format;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.FIELD_NAME;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.MoreCollectors.toOptional;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.hateoas.EntityModel;

import lombok.NonNull;

class SirenEntityModelDeserializer extends AbstractSirenDeserializer<EntityModel<?>> {

    private static final long serialVersionUID = -3683235541542548855L;
    private static final JavaType TYPE = defaultInstance().constructType(EntityModel.class);

    public SirenEntityModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter) {
        this(sirenConfiguration, linkConverter, TYPE);
    }

    public SirenEntityModelDeserializer(@NonNull SirenConfiguration sirenConfiguration, @NonNull SirenLinkConverter linkConverter,
        @NonNull JavaType contentType) {
        super(sirenConfiguration, linkConverter, contentType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType contentType = property == null ? ctxt.getContextualType() : property.getType().getContentType();
        return new SirenEntityModelDeserializer(sirenConfiguration, linkConverter, contentType);
    }

    @Override
    protected EntityModel<?> deserializeModel(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        SirenEntityModelBuilder builder = SirenEntityModelBuilder.builder(contentType.getRawClass(), linkConverter);
        while (!END_OBJECT.equals(jp.nextToken())) {
            if (FIELD_NAME.equals(jp.currentToken())) {
                String text = jp.getText();
                if ("properties".equals(text)) {
                    builder.content(deserializeProperties(jp, ctxt));
                }

                if ("entities".equals(text)) {
                    builder.content(deserializeEntities(jp, ctxt));
                }

                if ("links".equals(text)) {
                    builder.links(deserializeLinks(jp, ctxt));
                }

                if ("actions".equals(text)) {
                    builder.actions(deserializeActions(jp, ctxt));
                }
            }
        }
        return builder.build();
    }

    private Object deserializeProperties(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JavaType type = obtainContainedType();
        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(type);
        if (deserializer == null) {
            throw new JsonParseException(jp, format("No deserializer available for type '%s'!", type));
        }

        jp.nextToken();
        return deserializer.deserialize(jp, ctxt);
    }

    private Object deserializeEntities(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JavaType type = obtainContainedType();
        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(type);
        if (deserializer == null) {
            throw new JsonParseException(jp, format("No deserializer available for binding '%s'!", type));
        }

        List<Object> content = newArrayList();
        if (START_ARRAY.equals(jp.nextToken())) {
            while (!END_ARRAY.equals(jp.nextToken())) {
                content.add(deserializer.deserialize(jp, ctxt));
            }
        }

        return content.stream().collect(toOptional()).orElse(null);
    }

    private List<SirenLink> deserializeLinks(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JavaType type = defaultInstance().constructType(SirenLink.class);
        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(type);
        if (deserializer == null) {
            throw new JsonParseException(jp, format("No deserializer available for type '%s'!", type));
        }

        List<SirenLink> links = newArrayList();
        if (START_ARRAY.equals(jp.nextToken())) {
            while (!END_ARRAY.equals(jp.nextToken())) {
                links.add((SirenLink) deserializer.deserialize(jp, ctxt));
            }
        }

        return links;
    }

    private List<SirenAction> deserializeActions(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JavaType type = defaultInstance().constructType(SirenAction.class);
        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(type);
        if (deserializer == null) {
            throw new JsonParseException(jp, format("No deserializer available for type '%s'!", type));
        }

        List<SirenAction> actions = newArrayList();
        if (START_ARRAY.equals(jp.nextToken())) {
            while (!END_ARRAY.equals(jp.nextToken())) {
                actions.add((SirenAction) deserializer.deserialize(jp, ctxt));
            }
        }

        return actions;
    }

}
