/*-
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ingogriebsch.spring.hateoas.siren;

import static java.lang.String.format;

import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.FIELD_NAME;
import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.hateoas.CollectionModel;
import org.springframework.lang.Nullable;

/**
 * {@link JsonDeserializer} implementation which is able to deserialize a Siren entity into a {@link CollectionModel}.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see CollectionModel
 */
class SirenCollectionModelDeserializer extends AbstractSirenDeserializer<CollectionModel<?>> {

    private static final long serialVersionUID = 4364222303241126575L;
    private static final JavaType TYPE = defaultInstance().constructType(CollectionModel.class);

    SirenCollectionModelDeserializer(SirenConfiguration configuration, SirenDeserializerFacilities deserializerFacilities) {
        this(configuration, deserializerFacilities, TYPE);
    }

    SirenCollectionModelDeserializer(SirenConfiguration configuration, SirenDeserializerFacilities deserializerFacilities,
        JavaType contentType) {
        super(configuration, deserializerFacilities, contentType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, @Nullable BeanProperty property) {
        JavaType contentType = property == null ? ctxt.getContextualType() : property.getType().getContentType();
        return new SirenCollectionModelDeserializer(configuration, deserializerFacilities, contentType);
    }

    @Override
    protected CollectionModel<?> deserializeModel(JsonParser jp, DeserializationContext ctxt) throws IOException {
        SirenCollectionModelBuilder builder = SirenCollectionModelBuilder.builder(contentType,
            getRepresentationModelFactories().forCollectioModel(), getLinkConverter());

        while (!END_OBJECT.equals(jp.nextToken())) {
            if (FIELD_NAME.equals(jp.currentToken())) {
                String text = jp.getText();
                if ("properties".equals(text)) {
                    builder.properties(deserializeProperties(jp, ctxt));
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> deserializeProperties(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonDeserializer<Object> deserializer =
            getDeserializer(defaultInstance().constructMapType(Map.class, String.class, Object.class), jp, ctxt);

        JsonToken nextToken = jp.nextToken();
        if (!START_OBJECT.equals(nextToken)) {
            throw new JsonParseException(jp, format("Token does not represent '%s' [but '%s']!", START_OBJECT, nextToken));
        }

        return (Map<String, Object>) deserializer.deserialize(jp, ctxt);
    }
}
