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
package com.github.ingogriebsch.spring.hateoas.siren;

import static java.lang.String.format;

import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;

import org.springframework.hateoas.RepresentationModel;

/**
 * Base class supporting the implementation of deserializers which are able to deserialize a specific Siren entity into a
 * {@link RepresentationModel}.
 * 
 * @author Ingo Griebsch
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
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken token = jp.currentToken();
        if (!START_OBJECT.equals(token)) {
            throw new JsonParseException(jp, format("Current token does not represent '%s' (but '%s')!", START_OBJECT, token));
        }
        return deserializeModel(jp, ctxt);
    }

    protected abstract T deserializeModel(JsonParser jp, DeserializationContext ctxt) throws IOException;

    @Override
    public JavaType getContentType() {
        return contentType;
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        //TODO Warning: 'null' is returned by the method declared as @NonNullApi
        return null;
    }

    protected SirenLinkConverter getLinkConverter() {
        return deserializerFacilities.getLinkConverter();
    }

    protected RepresentationModelFactories getRepresentationModelFactories() {
        return deserializerFacilities.getRepresentationModelFactories();
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
}
