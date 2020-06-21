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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

/**
 * {@link JsonSerializer} implementation which is able to serialize a {@link SirenEmbeddedRepresentation}.
 * 
 * @author Ingo Griebsch
 */
class SirenEmbeddedRepresentationSerializer extends AbstractSirenSerializer<SirenEmbeddedRepresentation> {

    private static final long serialVersionUID = 5908856821949616351L;
    private static final String ATTR_KEY_REL = "__SIREN_ENTITY_REL__";

    SirenEmbeddedRepresentationSerializer(@NonNull SirenConfiguration configuration,
        @NonNull SirenSerializerFacilities serializerFacilities) {
        this(configuration, serializerFacilities, null);
    }

    SirenEmbeddedRepresentationSerializer(@NonNull SirenConfiguration configuration,
        @NonNull SirenSerializerFacilities serializerFacilities, BeanProperty property) {
        super(SirenEmbeddedRepresentation.class, configuration, serializerFacilities, property);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenEmbeddedRepresentationSerializer(configuration, serializerFacilities, property);
    }

    @Override
    public void serialize(SirenEmbeddedRepresentation representation, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        RepresentationModel<?> model = representation.getModel();

        JsonSerializer<Object> serializer = provider.findValueSerializer(model.getClass(), property);

        Object former = setAttribute(ATTR_KEY_REL, representation.getRels(), provider);
        try {
            serializer.serialize(model, gen, provider);
        } finally {
            setAttribute(ATTR_KEY_REL, former, provider);
        }
    }
}
