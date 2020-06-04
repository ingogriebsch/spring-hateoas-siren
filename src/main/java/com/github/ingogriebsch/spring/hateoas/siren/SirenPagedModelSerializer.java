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

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

class SirenPagedModelSerializer extends AbstractSirenSerializer<PagedModel<?>> {

    private static final long serialVersionUID = 9054285190464802945L;

    SirenPagedModelSerializer(@NonNull SirenConfiguration sirenConfiguration, @NonNull SirenLinkConverter sirenLinkConverter,
        @NonNull SirenEntityClassProvider sirenEntityClassProvider, @NonNull SirenEntityRelProvider sirenEntityRelProvider,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider, sirenEntityRelProvider, messageResolver, null);
    }

    SirenPagedModelSerializer(@NonNull SirenConfiguration sirenConfiguration, @NonNull SirenLinkConverter sirenLinkConverter,
        @NonNull SirenEntityClassProvider sirenEntityClassProvider, @NonNull SirenEntityRelProvider sirenEntityRelProvider,
        @NonNull MessageResolver messageResolver, BeanProperty property) {
        super(PagedModel.class, sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider, sirenEntityRelProvider,
            messageResolver, property);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenPagedModelSerializer(sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider,
            sirenEntityRelProvider, messageResolver, property);
    }

    @Override
    public void serialize(PagedModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenNavigables navigables = sirenLinkConverter.to(model.getLinks());

        SirenEntity sirenEntity = SirenEntity.builder() //
            .actions(navigables.getActions()) //
            .classes(classes(model)) //
            .entities(entities(model)) //
            .links(navigables.getLinks()) //
            .properties(model.getMetadata()) //
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

    private static List<Object> entities(CollectionModel<?> model) {
        return model.getContent().stream().map(c -> entity(c)).collect(toList());
    }

    private static Object entity(Object embeddable) {
        return embeddable;
    }
}
