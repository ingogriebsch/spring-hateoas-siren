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

import static com.github.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModel;
import static com.github.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModelSubclass;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.hateoas.EntityModel;

import lombok.NonNull;

/**
 * {@link JsonSerializer} implementation which is able to serialize a {@link EntityModel} into a Siren entity.
 *
 * @author Ingo Griebsch
 */
class SirenEntityModelSerializer extends AbstractSirenSerializer<EntityModel<?>> {

    private static final long serialVersionUID = 2893716845519287714L;

    SirenEntityModelSerializer(@NonNull SirenConfiguration configuration,
        @NonNull SirenSerializerFacilities serializerFacilities) {
        this(configuration, serializerFacilities, null);
    }

    SirenEntityModelSerializer(@NonNull SirenConfiguration configuration, @NonNull SirenSerializerFacilities serializerFacilities,
        BeanProperty property) {
        super(EntityModel.class, configuration, serializerFacilities, property);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        return new SirenEntityModelSerializer(configuration, serializerFacilities, property);
    }

    @Override
    public void serialize(EntityModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenNavigables navigables = getLinkConverter().to(model.getLinks());

        SirenEntity sirenEntity = SirenEntity.builder() //
            .actions(navigables.getActions()) //
            .classes(classes(model)) //
            .links(navigables.getLinks()) //
            .entities(entities(model)) //
            .properties(properties(model)) //
            .rels(rels(model, provider)) //
            .title(title(model.getContent().getClass())) //
            .build();

        JsonSerializer<Object> serializer = provider.findValueSerializer(SirenEntity.class, property);

        Object parent = setAttribute(ATTR_KEY_PARENT, model, provider);
        try {
            serializer.serialize(sirenEntity, gen, provider);
        } finally {
            setAttribute(ATTR_KEY_PARENT, parent, provider);
        }
    }

    private static List<Object> entities(EntityModel<?> model) {
        // TODO NPE, model.getContent() is nullable
        Object content = model.getContent();
        if (isRepresentationModel(content.getClass())) {
            return newArrayList(content);
        } else {
            return newArrayList();
        }
    }

    private static Object properties(EntityModel<?> model) {
        // TODO NPE, model.getContent() is nullable
        Object content = model.getContent();
        if (!isRepresentationModel(content.getClass())) {
            return model.getContent();
        }

        if (isRepresentationModelSubclass(model.getClass())) {
            return BeanUtils.extractProperties(model, "links", "content");
        }

        return null;
    }
}
