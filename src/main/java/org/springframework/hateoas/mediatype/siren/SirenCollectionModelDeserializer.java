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
package org.springframework.hateoas.mediatype.siren;

import static java.lang.String.format;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.FIELD_NAME;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;

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

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.util.CollectionUtils;

import lombok.NonNull;

class SirenCollectionModelDeserializer extends AbstractSirenDeserializer<CollectionModel<?>> {

    private static final long serialVersionUID = 4364222303241126575L;
    private static final JavaType TYPE = defaultInstance().constructType(CollectionModel.class);

    public SirenCollectionModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter) {
        this(sirenConfiguration, linkConverter, TYPE);
    }

    public SirenCollectionModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, JavaType contentType) {
        super(sirenConfiguration, linkConverter, contentType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        return new SirenCollectionModelDeserializer(sirenConfiguration, linkConverter,
            property == null ? ctxt.getContextualType() : property.getType().getContentType());
    }

    @Override
    public CollectionModel<?> deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        List<Object> entities = null;
        List<SirenLink> sirenLinks = newArrayList();
        List<SirenAction> sirenActions = newArrayList();

        while (jp.nextToken() != null) {
            if (FIELD_NAME.equals(jp.currentToken())) {
                if ("entities".equals(jp.getText())) {
                    entities = deserializeEntities(jp, ctxt);
                }

                if ("links".equals(jp.getText())) {
                    sirenLinks = deserializeLinks(jp, ctxt);
                }

                if ("actions".equals(jp.getText())) {
                    sirenActions = deserializeActions(jp, ctxt);
                }
            }
        }

        entities = entities != null ? entities : newArrayList();
        List<Link> links = linkConverter.from(SirenNavigables.of(sirenLinks, sirenActions));
        return new CollectionModel<>(entities, links);
    }

    private List<Object> deserializeEntities(JsonParser jp, DeserializationContext ctxt) throws IOException {
        List<JavaType> bindings = contentType.getBindings().getTypeParameters();
        if (CollectionUtils.isEmpty(bindings)) {
            throw new JsonParseException(jp, format("No bindings available through content type '%s'!", contentType));
        }

        JavaType binding = bindings.iterator().next();
        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(binding);
        if (deserializer == null) {
            throw new JsonParseException(jp, format("No deserializer available for binding '%s'!", binding));
        }

        List<Object> content = newArrayList();
        if (START_ARRAY.equals(jp.nextToken())) {
            while (!END_ARRAY.equals(jp.nextToken())) {
                content.add(deserializer.deserialize(jp, ctxt));
            }
        }
        return content;
    }

    private List<SirenLink> deserializeLinks(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JavaType type = defaultInstance().constructType(SirenLink.class);
        JsonDeserializer<Object> deserializer = ctxt.findContextualValueDeserializer(type, null);
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
        JsonDeserializer<Object> deserializer = ctxt.findContextualValueDeserializer(type, null);
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
