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

import static com.github.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.http.MediaType;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Spring configuration for Siren support.
 *
 * @author Ingo Griebsch
 */
@Configuration
@RequiredArgsConstructor
class SirenMediaTypeConfiguration implements HypermediaMappingInformation {


    // Hint: Optional fields are somewhat discouraged
    /*
     * Optional is primarily intended for use as a method return type where there is a clear need
     * to represent "no result," and where using null is likely to cause errors. A variable whose
     * type is Optional should never itself be null; it should always point to an Optional instance.
     */

    @NonNull
    private final Optional<SirenConfiguration> configuration;
    @NonNull
    private final Optional<RepresentationModelFactories> representationModelFactories;
    @NonNull
    private final Optional<SirenEntityClassProvider> entityClassProvider;
    @NonNull
    private final Optional<SirenEntityRelProvider> entityRelProvider;
    @NonNull
    private final MessageResolver messageResolver;

    @Bean
    LinkDiscoverer sirenLinkDisocoverer() {
        return new SirenLinkDiscoverer();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.hateoas.config.HypermediaMappingInformation#getMediaTypes()
     */
    @Override
    public List<MediaType> getMediaTypes() {
        return newArrayList(SIREN_JSON);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.hateoas.config.HypermediaMappingInformation#getJacksonModule()
     */
    @Override
    public Module getJacksonModule() {
        return new Jackson2SirenModule();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.hateoas.config.HypermediaMappingInformation#configureObjectMapper(com.fasterxml.jackson.databind.
     * ObjectMapper)
     */
    @Override
    public ObjectMapper configureObjectMapper(@NonNull ObjectMapper mapper) {
        mapper = HypermediaMappingInformation.super.configureObjectMapper(mapper);

        SirenHandlerInstantiator instantiator =
                new SirenHandlerInstantiator(configuration(), deserializerFacilities(), serializerFacilities());
        mapper.setHandlerInstantiator(instantiator);

        return mapper;
    }

    private SirenConfiguration configuration() {
        return configuration.orElse(new SirenConfiguration());
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
        return representationModelFactories.orElse(new RepresentationModelFactories() {
        });
    }

    private SirenEntityClassProvider entityClassProvider() {
        return entityClassProvider.orElse(new SirenEntityClassProvider() {
        });
    }

    private SirenEntityRelProvider entityRelProvider() {
        return entityRelProvider.orElse(new SirenEntityRelProvider() {
        });
    }

}
