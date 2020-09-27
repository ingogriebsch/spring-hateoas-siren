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

import static java.util.stream.Collectors.toList;

import static de.ingogriebsch.spring.hateoas.siren.BeanUtils.extractProperties;
import static de.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModelSubclass;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.hateoas.CollectionModel;
import org.springframework.lang.Nullable;

/**
 * {@link JsonSerializer} implementation which is able to serialize a {@link CollectionModel} into a Siren entity.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 */
class SirenCollectionModelSerializer extends AbstractSirenSerializer<CollectionModel<?>> {

    private static final long serialVersionUID = 9054285190464802945L;

    SirenCollectionModelSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities) {
        this(configuration, serializerFacilities, null);
    }

    SirenCollectionModelSerializer(SirenConfiguration configuration, SirenSerializerFacilities serializerFacilities,
        @Nullable BeanProperty property) {
        super(CollectionModel.class, configuration, serializerFacilities, property);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, @Nullable BeanProperty property) {
        return new SirenCollectionModelSerializer(configuration, serializerFacilities, property);
    }

    @Override
    public void serialize(CollectionModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        assertSubclassingIsEnabledIfModelIsSubclassed(model);

        SirenNavigables navigables = getLinkConverter().to(model.getLinks());

        SirenEntity sirenEntity = SirenEntity.builder() //
            .actions(navigables.getActions()) //
            .classes(classes(model)) //
            .entities(entities(model)) //
            .links(navigables.getLinks()) //
            .properties(properties(model)) //
            .rels(rels(model, provider)) //
            .title(title(model.getClass())) //
            .build();

        JsonSerializer<Object> serializer = provider.findValueSerializer(SirenEntity.class, property);

        Object parent = setAttribute(ATTR_KEY_PARENT, model, provider);
        try {
            serializer.serialize(sirenEntity, gen, provider);
        } finally {
            setAttribute(ATTR_KEY_PARENT, parent, provider);
        }
    }

    private void assertSubclassingIsEnabledIfModelIsSubclassed(CollectionModel<?> model) {
        Class<?> clazz = model.getClass();
        if (isRepresentationModelSubclass(clazz) && !configuration.isEntityAndCollectionModelSubclassingEnabled()) {
            throw new IllegalStateException(String.format(
                "You did not configure the module to enable subclassing but want to serialize a subclassed %s, namely %s!",
                CollectionModel.class.getSimpleName(), clazz.getName()));
        }
    }

    private static Map<String, Object> properties(CollectionModel<?> model) {
        Map<String, Object> properties = extractProperties(model, "links", "content");
        return properties.isEmpty() ? null : properties;
    }

    private static List<Object> entities(CollectionModel<?> model) {
        return model.getContent().stream().map(SirenCollectionModelSerializer::entity).collect(toList());
    }

    private static Object entity(Object embeddable) {
        return embeddable;
    }

}
