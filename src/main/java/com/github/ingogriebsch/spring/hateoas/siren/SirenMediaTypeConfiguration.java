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

import static com.github.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.http.MediaType;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
class SirenMediaTypeConfiguration implements HypermediaMappingInformation {

    @NonNull
    private final ObjectProvider<SirenConfiguration> configuration;
    @NonNull
    private final ObjectProvider<RepresentationModelFactories> representationModelFactories;
    @NonNull
    private final ObjectProvider<SirenEntityClassProvider> entityClassProvider;
    @NonNull
    private final ObjectProvider<SirenEntityRelProvider> entityRelProvider;
    @NonNull
    private final MessageResolver messageResolver;

    @Override
    public List<MediaType> getMediaTypes() {
        return newArrayList(SIREN_JSON);
    }

    @Override
    public Module getJacksonModule() {
        return new Jackson2SirenModule();
    }

    @Override
    public ObjectMapper configureObjectMapper(@NonNull ObjectMapper mapper) {
        mapper = HypermediaMappingInformation.super.configureObjectMapper(mapper);

        SirenHandlerInstantiator instantiator =
            new SirenHandlerInstantiator(configuration(), deserializerFacilities(), serializerFacilities());
        mapper.setHandlerInstantiator(instantiator);

        return mapper;
    }

    private SirenConfiguration configuration() {
        return configuration.getIfAvailable(() -> new SirenConfiguration());
    }

    private SirenDeserializerFacilities deserializerFacilities() {
        return new SirenDeserializerFacilities(representationModelFactories(), linkConverter());
    }

    private SirenSerializerFacilities serializerFacilities() {
        return new SirenSerializerFacilities(entityClassProvider(), entityRelProvider(), linkConverter(), messageResolver);
    }

    private SirenLinkConverter linkConverter() {
        return new SirenLinkConverter(messageResolver);
    }

    private RepresentationModelFactories representationModelFactories() {
        return representationModelFactories.getIfAvailable(() -> new RepresentationModelFactories() {
        });
    }

    private SirenEntityClassProvider entityClassProvider() {
        return entityClassProvider.getIfAvailable(() -> new SirenEntityClassProvider() {
        });
    }

    private SirenEntityRelProvider entityRelProvider() {
        return entityRelProvider.getIfAvailable(() -> new SirenEntityRelProvider() {
        });
    }

}
