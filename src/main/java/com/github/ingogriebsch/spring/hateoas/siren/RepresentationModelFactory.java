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

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.springframework.util.Assert.isAssignable;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

public interface RepresentationModelFactory {

    default RepresentationModel<?> create(@NonNull JavaType type, @NonNull Iterable<Link> links, Map<String, Object> properties) {
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