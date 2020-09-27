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

import static de.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.springframework.util.Assert.isAssignable;

import java.util.Collection;

import com.fasterxml.jackson.databind.JavaType;
import lombok.NonNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;

/**
 * A factory to create a {@link PagedModel} instance based on the given input that is evaluated during deserialization.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see PagedModel
 */
public interface PagedModelFactory {

    /**
     * Creates a {@link PagedModel} based on the given input.
     * <p>
     * The default implementation instantiates the concrete {@link PagedModel} based on the assumption that the
     * {@link PagedModel#PagedModel(Collection, PageMetadata, Iterable) constructors} of the {@link PagedModel model} is
     * accessible.
     * 
     * @param type the type of the instance that should be created. Must be assignable to {@link PagedModel}.Is never
     *        {@literal null}.
     * @param links the links which should be used to instantiate the {@link PagedModel model}. Is never {@literal null}.
     * @param content the objects which should be used to instantiate the {@link PagedModel model}. Is never {@literal null}.
     * @param metadata the {@link PageMetadata} which should be used to the instantiate {@link PagedModel model}. Is never
     *        {@literal null}.
     * @return the created {@link PagedModel} instance.
     */
    default PagedModel<?> create(@NonNull JavaType type, @NonNull Iterable<Link> links, @NonNull Iterable<Object> content,
        @NonNull PageMetadata metadata) {
        Class<?> modelType = type.getRawClass();
        isAssignable(PagedModel.class, modelType);

        // TODO Check if the type of one of the content objects matches the contained type of the given type.

        Class<?>[] types = new Class[] { Collection.class, PageMetadata.class, Iterable.class };
        Object[] args = new Object[] { content, metadata, links };
        return (PagedModel<?>) instantiate(modelType, types, args);
    }
}
