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

import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.hateoas.mediatype.PropertyUtils.createObjectFromProperties;
import static org.springframework.hateoas.mediatype.PropertyUtils.extractPropertyValues;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

class SirenRepresentationModelDeserializer extends AbstractSirenDeserializer<RepresentationModel<?>> {

    private static final long serialVersionUID = -3683235541542548855L;
    private static final JavaType TYPE = defaultInstance().constructType(RepresentationModel.class);

    public SirenRepresentationModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter) {
        this(sirenConfiguration, linkConverter, TYPE);
    }

    public SirenRepresentationModelDeserializer(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenLinkConverter linkConverter, @NonNull JavaType contentType) {
        super(sirenConfiguration, linkConverter, contentType);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType contentType = property == null ? ctxt.getContextualType() : property.getType().getContentType();
        return new SirenRepresentationModelDeserializer(sirenConfiguration, linkConverter, contentType);
    }

    @Override
    public RepresentationModel<?> deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        SirenEntity sirenEntity = jp.getCodec().readValue(jp, SirenEntity.class);

        Map<String, Object> properties = obtainProperties(sirenEntity);
        List<Link> links = obtainlinks(sirenEntity.getLinks(), sirenEntity.getActions());

        return createModel(properties, links);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> obtainProperties(SirenEntity sirenEntity) {
        Object properties = sirenEntity.getProperties();
        if (properties == null) {
            return newHashMap();
        }

        if (Map.class.isAssignableFrom(properties.getClass())) {
            return (Map<String, Object>) properties;
        }
        return extractPropertyValues(properties);
    }

    private List<Link> obtainlinks(List<SirenLink> links, List<SirenAction> actions) {
        List<SirenLink> sirenLinks = links != null ? links : newArrayList();
        List<SirenAction> sirenActions = actions != null ? actions : newArrayList();
        return linkConverter.from(SirenNavigables.of(sirenLinks, sirenActions));
    }

    private RepresentationModel<?> createModel(Map<String, Object> properties, List<Link> links) {
        RepresentationModel<?> model = (RepresentationModel<?>) createObjectFromProperties(contentType.getRawClass(), properties);
        model.add(links);
        return model;
    }

}
