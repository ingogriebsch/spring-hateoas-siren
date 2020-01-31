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
package org.springframework.hateoas.mediatype.siren;

import static org.springframework.beans.BeanUtils.instantiateClass;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.lang.Nullable;

import lombok.NonNull;

class SirenHandlerInstantiator extends HandlerInstantiator {

    private final Map<Class<?>, Object> serializers = new HashMap<>();
    private final AutowireCapableBeanFactory beanFactory;

    public SirenHandlerInstantiator(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenEntityClassProvider sirenEntityClassProvider, @NonNull MessageResolver messageResolver) {
        this(sirenConfiguration, sirenEntityClassProvider, messageResolver, null);
    }

    public SirenHandlerInstantiator(@NonNull SirenConfiguration sirenConfiguration,
        @NonNull SirenEntityClassProvider sirenEntityClassProvider, @NonNull MessageResolver messageResolver,
        AutowireCapableBeanFactory beanFactory) {
        SirenLinkConverter sirenLinkConverter = new SirenLinkConverter(messageResolver);

        serializers.put(SirenRepresentationModelSerializer.class, new SirenRepresentationModelSerializer(sirenConfiguration,
            sirenLinkConverter, sirenEntityClassProvider, messageResolver));
        serializers.put(SirenRepresentationModelDeserializer.class,
            new SirenRepresentationModelDeserializer(sirenConfiguration, sirenLinkConverter));

        serializers.put(SirenEntityModelSerializer.class,
            new SirenEntityModelSerializer(sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider, messageResolver));
        serializers.put(SirenEntityModelDeserializer.class,
            new SirenEntityModelDeserializer(sirenConfiguration, sirenLinkConverter));

        serializers.put(SirenCollectionModelSerializer.class, new SirenCollectionModelSerializer(sirenConfiguration,
            sirenLinkConverter, sirenEntityClassProvider, messageResolver));
        serializers.put(SirenCollectionModelDeserializer.class,
            new SirenCollectionModelDeserializer(sirenConfiguration, sirenLinkConverter));

        serializers.put(SirenPagedModelSerializer.class,
            new SirenPagedModelSerializer(sirenConfiguration, sirenLinkConverter, sirenEntityClassProvider, messageResolver));
        serializers.put(SirenPagedModelDeserializer.class,
            new SirenPagedModelDeserializer(sirenConfiguration, sirenLinkConverter));

        this.beanFactory = beanFactory;
    }

    @Override
    public JsonDeserializer<?> deserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> deserClass) {
        return (JsonDeserializer<?>) findInstance(deserClass);
    }

    @Override
    public KeyDeserializer keyDeserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> keyDeserClass) {
        return (KeyDeserializer) findInstance(keyDeserClass);
    }

    @Override
    public JsonSerializer<?> serializerInstance(SerializationConfig config, Annotated annotated, Class<?> serClass) {
        return (JsonSerializer<?>) findInstance(serClass);
    }

    @Override
    public TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> config, Annotated annotated,
        Class<?> builderClass) {
        return (TypeResolverBuilder<?>) findInstance(builderClass);
    }

    @Override
    public TypeIdResolver typeIdResolverInstance(MapperConfig<?> config, Annotated annotated, Class<?> resolverClass) {
        return (TypeIdResolver) findInstance(resolverClass);
    }

    @Nullable
    private Object findInstance(Class<?> type) {
        Object result = serializers.get(type);
        if (result != null) {
            return result;
        }

        if (beanFactory != null) {
            return beanFactory.createBean(type);
        }

        return instantiateClass(type);
    }
}
