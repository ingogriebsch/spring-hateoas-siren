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

import static com.google.common.collect.Lists.newArrayList;
import static de.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModel;
import static de.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModelSubclass;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.hateoas.EntityModel;
import org.springframework.lang.Nullable;

/**
 * {@link JsonSerializer} implementation which is able to serialize a {@link EntityModel} into a Siren entity.
 * 
 * @author Ingo Griebsch
 */
class SirenEntityModelSerializer extends AbstractSirenSerializer<EntityModel<?>> {

    private static final long serialVersionUID = 2893716845519287714L;

    SirenEntityModelSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities) {
        this(configuration, serializerFacilities, null);
    }

    SirenEntityModelSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities,
        @Nullable BeanProperty property) {
        super(EntityModel.class, configuration, serializerFacilities, property);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, @Nullable BeanProperty property) {
        return new SirenEntityModelSerializer(configuration, serializerFacilities, property);
    }

    @Override
    public void serialize(EntityModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        assertSubclassingIsEnabledIfModelIsSubclassed(model);

        SirenNavigables navigables = getLinkConverter().to(model.getLinks());
        Class<?> contentType = model.getContent().getClass();
        Class<?> titleType = !isRepresentationModel(contentType) ? contentType : model.getClass();

        SirenEntity sirenEntity = SirenEntity.builder() //
            .actions(navigables.getActions()) //
            .classes(classes(model)) //
            .links(navigables.getLinks()) //
            .entities(entities(model)) //
            .properties(properties(model)) //
            .rels(rels(model, provider)) //
            .title(title(titleType)) //
            .build();

        JsonSerializer<Object> serializer = provider.findValueSerializer(SirenEntity.class, property);

        Object parent = setAttribute(ATTR_KEY_PARENT, model, provider);
        try {
            serializer.serialize(sirenEntity, gen, provider);
        } finally {
            setAttribute(ATTR_KEY_PARENT, parent, provider);
        }
    }

    private void assertSubclassingIsEnabledIfModelIsSubclassed(EntityModel<?> model) {
        Class<?> clazz = model.getClass();
        if (isRepresentationModelSubclass(clazz) && !configuration.isEntityAndCollectionModelSubclassingEnabled()) {
            throw new IllegalStateException(String.format(
                "You did not configure the module to enable subclassing but want to serialize a subclassed %s, namely %s!",
                EntityModel.class.getSimpleName(), clazz.getName()));
        }
    }

    private static List<Object> entities(EntityModel<?> model) {
        Object content = model.getContent();
        if (isRepresentationModel(content.getClass())) {
            return newArrayList(content);
        } else {
            return newArrayList();
        }
    }

    private static Object properties(EntityModel<?> model) {
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
