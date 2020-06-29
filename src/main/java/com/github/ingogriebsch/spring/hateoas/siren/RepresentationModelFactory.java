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

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.springframework.util.Assert.isAssignable;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.Nullable;

import lombok.NonNull;

/**
 * Factory to create a {@link RepresentationModel} instance based on the given input evaluated during deserialization.
 * 
 * @author Ingo Griebsch
 */
public interface RepresentationModelFactory {

    /**
     * Creates a {@link RepresentationModel} based on the given input.
     * <p>
     * The default implementation instantiates the concrete {@link RepresentationModel} based on the assumption that the
     * {@link RepresentationModel#RepresentationModel()} constructors is accessible, add the given links to the
     * {@link RepresentationModel model} and applies the properties (if given).
     * 
     * @param type the type of the instance that should be created. Must be assignable to {@link RepresentationModel}. Is never
     *        {@literal null}.
     * @param links the links which should be use to instantiate the {@link RepresentationModel model}. Is never {@literal null}.
     * @param properties the properties which should be applied on the instantiated {@link RepresentationModel model}. Can be
     *        {@literal null}.
     * @return the created {@link RepresentationModel} instance.
     */
    default RepresentationModel<?> create(@NonNull JavaType type, @NonNull Iterable<Link> links,
        @Nullable Map<String, Object> properties) {
        Class<?> modelType = type.getRawClass();
        isAssignable(RepresentationModel.class, modelType);

        Class<?>[] types = new Class[] {};
        Object[] args = new Object[] {};
        RepresentationModel<?> model = (RepresentationModel<?>) instantiate(modelType, types, args);
        model.add(links);

        if (properties != null) {
            applyProperties(model, properties);
        }

        return model;
    }
}
