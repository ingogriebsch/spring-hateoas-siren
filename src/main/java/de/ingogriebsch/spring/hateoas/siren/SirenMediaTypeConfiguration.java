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
import static de.ingogriebsch.spring.hateoas.siren.SimpleObjectProvider.objectProvider;

import java.util.List;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
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
public class SirenMediaTypeConfiguration implements HypermediaMappingInformation {

    private final MessageResolver messageResolver;
    private final SirenConfiguration configuration;
    private final SirenEntityClassProvider entityClassProvider;
    private final SirenEntityRelProvider entityRelProvider;
    private final SirenActionFieldTypeConverter actionFieldTypeConverter;
    private final RepresentationModelFactories representationModelFactories;

    public SirenMediaTypeConfiguration( //
        @NonNull ObjectProvider<MessageResolver> messageResolver, //
        @NonNull ObjectProvider<SirenConfiguration> configuration, //
        @NonNull ObjectProvider<SirenEntityClassProvider> entityClassProvider, //
        @NonNull ObjectProvider<SirenEntityRelProvider> entityRelProvider, //
        @NonNull ObjectProvider<SirenActionFieldTypeConverter> actionFieldTypeConverter, //
        @NonNull ObjectProvider<RepresentationModelFactories> representationModelFactories) {

        this.messageResolver = messageResolver(messageResolver);
        this.configuration = configuration(configuration);
        this.entityClassProvider = entityClassProvider(entityClassProvider);
        this.entityRelProvider = entityRelProvider(entityRelProvider);
        this.actionFieldTypeConverter =
            actionFieldTypeConverter(actionFieldTypeConverter, this.configuration.getActionFieldTypeMappings());
        this.representationModelFactories = representationModelFactories(representationModelFactories);
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
        ObjectMapper configured = HypermediaMappingInformation.super.configureObjectMapper(mapper);

        SirenHandlerInstantiator instantiator = sirenHandlerInitiator();
        configured.setHandlerInstantiator(instantiator);

        return configured;
    }

    @Bean
    LinkDiscoverer sirenLinkDisocoverer() {
        return new SirenLinkDiscoverer();
    }

    static SirenMediaTypeConfiguration of( //
        MessageResolver messageResolver, //
        SirenConfiguration configuration, //
        SirenEntityClassProvider entityClassProvider, //
        SirenEntityRelProvider entityRelProvider, //
        SirenActionFieldTypeConverter actionFieldTypeConverter, //
        RepresentationModelFactories representationModelFactories) {

        return new SirenMediaTypeConfiguration(//
            objectProvider(messageResolver), //
            objectProvider(configuration), //
            objectProvider(entityClassProvider), //
            objectProvider(entityRelProvider), //
            objectProvider(actionFieldTypeConverter), //
            objectProvider(representationModelFactories) //
        );
    }

    private SirenHandlerInstantiator sirenHandlerInitiator() {
        SirenLinkConverter linkConverter = new SirenLinkConverter(messageResolver, actionFieldTypeConverter);

        SirenDeserializerFacilities deserializerFacilities =
            new SirenDeserializerFacilities(representationModelFactories, linkConverter);

        SirenSerializerFacilities serializerFacilities =
            new SirenSerializerFacilities(entityClassProvider, entityRelProvider, linkConverter, messageResolver);

        return new SirenHandlerInstantiator(configuration, serializerFacilities, deserializerFacilities);
    }

    private static MessageResolver messageResolver(ObjectProvider<MessageResolver> messageResolver) {
        return new NoSuchMessageExceptionSuppressingMessageResolver(
            messageResolver.getIfAvailable(() -> MessageResolver.of(null)));
    }

    private static SirenConfiguration configuration(ObjectProvider<SirenConfiguration> configuration) {
        return configuration.getIfAvailable(SirenConfiguration::new);
    }

    private static SirenEntityClassProvider entityClassProvider(ObjectProvider<SirenEntityClassProvider> entityClassProvider) {
        return entityClassProvider.getIfAvailable(() -> new SirenEntityClassProvider() {
        });
    }

    private static SirenEntityRelProvider entityRelProvider(ObjectProvider<SirenEntityRelProvider> entityRelProvider) {
        return entityRelProvider.getIfAvailable(() -> new SirenEntityRelProvider() {
        });
    }

    private static SirenActionFieldTypeConverter actionFieldTypeConverter(ObjectProvider<SirenActionFieldTypeConverter> provider,
        List<TypeMapping> actionFieldTypeMappings) {
        return provider.getIfAvailable(() -> {
            return new TypeBasedSirenActionFieldTypeConverter(actionFieldTypeMappings);
        });
    }

    private static RepresentationModelFactories
        representationModelFactories(ObjectProvider<RepresentationModelFactories> provider) {
        return provider.getIfAvailable(() -> new RepresentationModelFactories() {
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
