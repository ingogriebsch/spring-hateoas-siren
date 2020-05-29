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

import static com.github.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModel;
import static com.github.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModelSubclass;
import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.IanaLinkRelations.ITEM;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.NonNull;

class SirenEntityModelSerializer extends AbstractSirenSerializer<EntityModel<?>> {

    private static final long serialVersionUID = 2893716845519287714L;

    public SirenEntityModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter sirenLinkConverter, @NonNull SirenEntityClassProvider sirenEntityClassProvider,
        @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider, messageResolver, null);
    }

    public SirenEntityModelSerializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter sirenLinkConverter, @NonNull SirenEntityClassProvider sirenEntityClassProvider,
        @NonNull MessageResolver messageResolver, BeanProperty property) {
        super(EntityModel.class, sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider, messageResolver, property);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        return new SirenEntityModelSerializer(sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider, messageResolver,
            property);
    }

    @Override
    public void serialize(EntityModel<?> model, JsonGenerator gen, SerializerProvider provider) throws IOException {
        SirenNavigables navigables = sirenLinkConverter.to(model.getLinks());

        SirenEntity sirenEntity = SirenEntity.builder() //
            .actions(navigables.getActions()) //
            .classes(classes(model)) //
            .links(navigables.getLinks()) //
            .entities(entities(model)) //
            .properties(properties(model)) //
            .rels(rels(model, gen)) //
            .title(title(model.getContent().getClass())) //
            .build();

        JsonSerializer<Object> serializer = provider.findValueSerializer(SirenEntity.class, property);
        serializer.serialize(sirenEntity, gen, provider);
    }

    private static List<LinkRelation> rels(EntityModel<?> model, JsonGenerator gen) {
        return !gen.getOutputContext().inRoot() ? newArrayList(ITEM) : newArrayList();
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
