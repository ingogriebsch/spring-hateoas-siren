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

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
import org.springframework.hateoas.RepresentationModel;

/**
 * Base class supporting the implementation of deserializers which are able to deserialize a specific Siren entity into a
 * {@link RepresentationModel}.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 */
abstract class AbstractSirenDeserializer<T extends RepresentationModel<?>> extends ContainerDeserializerBase<T>
    implements ContextualDeserializer {

    private static final long serialVersionUID = 3796755247545654672L;

    protected final SirenDeserializerFacilities deserializerFacilities;
    protected final SirenConfiguration configuration;
    protected final JavaType contentType;

    protected AbstractSirenDeserializer(SirenConfiguration configuration, SirenDeserializerFacilities deserializerFacilities,
        JavaType contentType) {
        super(contentType);
        this.deserializerFacilities = deserializerFacilities;
        this.configuration = configuration;
        this.contentType = contentType;
    }

    @Override
    public JavaType getContentType() {
        return contentType;
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return null;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken token = jp.currentToken();
        if (!START_OBJECT.equals(token)) {
            throw new JsonParseException(jp, format("Current token does not represent '%s' (but '%s')!", START_OBJECT, token));
        }
        return deserializeModel(jp, ctxt);
    }

    protected abstract T deserializeModel(JsonParser jp, DeserializationContext ctxt) throws IOException;

    @SuppressWarnings("unchecked")
    protected <E> List<E> deserializeEntries(JavaType javaType, JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonDeserializer<E> deserializer = (JsonDeserializer<E>) getDeserializer(javaType, jp, ctxt);

        List<E> entries = newArrayList();
        if (START_ARRAY.equals(jp.nextToken())) {
            while (!END_ARRAY.equals(jp.nextToken())) {
                entries.add(deserializer.deserialize(jp, ctxt));
            }
        }
        return entries;
    }

    protected List<Object> deserializeEntities(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return deserializeEntries(obtainContainedType(), jp, ctxt);
    }

    protected List<SirenLink> deserializeLinks(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return deserializeEntries(defaultInstance().constructType(SirenLink.class), jp, ctxt);
    }

    protected List<SirenAction> deserializeActions(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return deserializeEntries(defaultInstance().constructType(SirenAction.class), jp, ctxt);
    }

    protected JavaType obtainContainedType() {
        List<JavaType> typeParameters;
        JavaType type = contentType;
        do {
            typeParameters = type.getBindings().getTypeParameters();
            if (!typeParameters.isEmpty()) {
                break;
            }
            type = type.getSuperClass();
        } while (type != null);

        if (typeParameters.isEmpty()) {
            throw new IllegalArgumentException(format("No type parameters available through content type '%s'!", contentType));
        }

        if (typeParameters.size() > 1) {
            throw new IllegalArgumentException(
                format("No unique type parameter available through content type '%s'!", contentType));
        }
        return typeParameters.iterator().next();
    }

    protected JsonDeserializer<Object> getDeserializer(JavaType type, JsonParser jp, DeserializationContext ctxt)
        throws JsonMappingException, JsonParseException {
        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(type);
        if (deserializer == null) {
            throw new JsonParseException(jp, format("No deserializer available for type '%s'!", type));
        }
        return deserializer;
    }

    protected Map<String, Object> extractProperties(Object object, String... excludes) {
        return deserializerFacilities.getPropertiesFacility().extract(object, excludes);
    }

    protected SirenLinkConverter getLinkConverter() {
        return deserializerFacilities.getLinkConverter();
    }

    protected RepresentationModelFactories getRepresentationModelFactories() {
        return deserializerFacilities.getRepresentationModelFactories();
    }
}
