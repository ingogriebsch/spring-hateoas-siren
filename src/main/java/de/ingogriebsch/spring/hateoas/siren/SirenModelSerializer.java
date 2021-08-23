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

import static java.util.Optional.ofNullable;

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.ingogriebsch.spring.hateoas.siren.SirenModel.EmbeddedRepresentation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * {@link JsonSerializer} implementation which is able to serialize a {@link SirenModel} into a Siren entity.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see SirenModel
 */
class SirenModelSerializer extends AbstractSirenSerializer<SirenModel> {

    private static final long serialVersionUID = 1621319645104596013L;

    SirenModelSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities) {
        this(configuration, serializerFacilities, null);
    }

    SirenModelSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities,
        @Nullable BeanProperty property) {
        super(SirenModel.class, configuration, serializerFacilities, property);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenModelSerializer(configuration, serializerFacilities, property);
    }

    @Override
    public void serialize(SirenModel model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenNavigables navigables = getLinkConverter().to(model.getLinks());

        SirenEntity sirenEntity = SirenEntity.builder() //
            .classes(classes(model)) //
            .rels(rels(model, provider)) //
            .properties(properties(model.getProperties())) //
            .entities(model.getEntities()) //
            .links(navigables.getLinks()) //
            .actions(navigables.getActions()) //
            .title(title(model)) //
            .build();

        JsonSerializer<Object> serializer = provider.findValueSerializer(SirenEntity.class, property);

        Object parent = setAttribute(ATTR_KEY_PARENT, model, provider);
        try {
            serializer.serialize(sirenEntity, gen, provider);
        } finally {
            setAttribute(ATTR_KEY_PARENT, parent, provider);
        }
    }

    private String title(SirenModel model) {
        return model.getTitle() != null ? model.getTitle() : title(RepresentationModel.class);
    }

    private List<String> classes(SirenModel model) {
        return CollectionUtils.isEmpty(model.getClasses()) ? super.classes(model) : model.getClasses();
    }

    private Map<String, Object> properties(Object model) {
        Map<String, Object> properties = ofNullable(model).map(m -> extractProperties(m)).orElse(newHashMap());
        return properties.isEmpty() ? null : properties;
    }

    static class EmbeddedRepresentationSerializer extends AbstractSirenSerializer<EmbeddedRepresentation> {

        private static final long serialVersionUID = 5908856821949616351L;

        EmbeddedRepresentationSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities) {
            this(configuration, serializerFacilities, null);
        }

        EmbeddedRepresentationSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities,
            @Nullable BeanProperty property) {
            super(SirenModel.EmbeddedRepresentation.class, configuration, serializerFacilities, property);
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider prov, @Nullable BeanProperty property) {
            return new EmbeddedRepresentationSerializer(configuration, serializerFacilities, property);
        }

        @Override
        public void serialize(EmbeddedRepresentation representation, JsonGenerator gen, SerializerProvider provider)
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
}
