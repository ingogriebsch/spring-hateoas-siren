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

import static de.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static de.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.springframework.util.Assert.isAssignable;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import lombok.NonNull;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.lang.Nullable;

/**
 * A factory to create a {@link CollectionModel} instance based on the given input that is evaluated during deserialization.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see CollectionModel
 */
public interface CollectionModelFactory {

    /**
     * Creates a {@link CollectionModel} based on the given input.
     * <p>
     * The default implementation instantiates the concrete {@link CollectionModel} based on the assumption that the
     * {@link CollectionModel#CollectionModel(Iterable, Iterable, ResolvableType)} constructor is accessible.
     * 
     * @param type the type of the instance that should be created. Must be assignable to {@link CollectionModel}. Is never
     *        {@literal null}.
     * @param links the links which should be used to instantiate the {@link CollectionModel model}. Is never {@literal null}.
     * @param content the objects which should be used to instantiate the {@link CollectionModel model}. Is never {@literal null}.
     * @param properties the properties which should be applied on the instantiated {@link CollectionModel model}. Can be
     *        {@literal null}.
     * @return the created {@link CollectionModel} instance.
     */
    default CollectionModel<?> create(@NonNull JavaType type, @NonNull Iterable<Link> links, @NonNull Iterable<Object> content,
        @Nullable Map<String, Object> properties) {
        Class<?> modelType = type.getRawClass();
        isAssignable(CollectionModel.class, modelType);

        // TODO Check if the type of one of the content objects matches the contained type of the given type.

        Class<?>[] types = new Class[] { Iterable.class, Iterable.class, ResolvableType.class };
        Object[] args = new Object[] { content, links, null };
        CollectionModel<?> model = (CollectionModel<?>) instantiate(modelType, types, args);

        if (properties != null) {
            applyProperties(model, properties);
        }
        return model;
    }
}
