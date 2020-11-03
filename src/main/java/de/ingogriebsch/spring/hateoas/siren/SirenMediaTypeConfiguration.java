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
import static de.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;

import java.util.List;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.http.MediaType;

/**
 * A Spring based configuration that enables Siren support in the surrounding environment.
 *
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see Configuration
 * @see HypermediaMappingInformation
 * @see Module
 * @see ObjectMapper
 */
@Configuration
@RequiredArgsConstructor
public class SirenMediaTypeConfiguration implements HypermediaMappingInformation {

    private final ObjectProvider<MessageResolver> messageResolver;
    private final ObjectProvider<SirenConfiguration> configuration;
    private final ObjectProvider<SirenEntityClassProvider> entityClassProvider;
    private final ObjectProvider<SirenEntityRelProvider> entityRelProvider;
    private final ObjectProvider<SirenActionFieldTypeConverter> sirenActionFieldTypeConverter;
    private final ObjectProvider<RepresentationModelFactories> representationModelFactories;

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
    public ObjectMapper configureObjectMapper(ObjectMapper mapper) {
        mapper = HypermediaMappingInformation.super.configureObjectMapper(mapper);

        SirenHandlerInstantiator instantiator =
            new SirenHandlerInstantiator(configuration(), deserializerFacilities(), serializerFacilities());
        mapper.setHandlerInstantiator(instantiator);

        return mapper;
    }

    private SirenConfiguration configuration() {
        return configuration.getIfAvailable(SirenConfiguration::new);
    }

    private SirenDeserializerFacilities deserializerFacilities() {
        return new SirenDeserializerFacilities(representationModelFactories(), linkConverter());
    }

    private SirenSerializerFacilities serializerFacilities() {
        return new SirenSerializerFacilities(entityClassProvider(), entityRelProvider(), linkConverter(), messageResolver());
    }

    private SirenLinkConverter linkConverter() {
        return new SirenLinkConverter(messageResolver(), sirenActionFieldTypeConverter());
    }

    private MessageResolver messageResolver() {
        return new NoSuchMessageExceptionSuppressingMessageResolver(
            messageResolver.getIfAvailable(() -> MessageResolver.of(null)));
    }

    private SirenEntityClassProvider entityClassProvider() {
        return entityClassProvider.getIfAvailable(() -> new SirenEntityClassProvider() {
        });
    }

    private SirenEntityRelProvider entityRelProvider() {
        return entityRelProvider.getIfAvailable(() -> new SirenEntityRelProvider() {
        });
    }

    private SirenActionFieldTypeConverter sirenActionFieldTypeConverter() {
        return sirenActionFieldTypeConverter.getIfAvailable(() -> {
            return new TypeBasedSirenActionFieldTypeConverter(configuration().getActionFieldTypeMappings());
        });

    }

    private RepresentationModelFactories representationModelFactories() {
        return representationModelFactories.getIfAvailable(() -> new RepresentationModelFactories() {
        });
    }

    @Value
    private static class NoSuchMessageExceptionSuppressingMessageResolver implements MessageResolver {

        MessageResolver delegate;

        @Override
        public String resolve(MessageSourceResolvable resolvable) {
            try {
                return delegate.resolve(resolvable);
            } catch (NoSuchMessageException e) {
                return null;
            }
        }
    }
}
