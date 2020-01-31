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

import static java.lang.String.format;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

public class SimpleSirenEntityClassProvider implements SirenEntityClassProvider {

    private static final List<Class<?>> RESOURCE_TYPES =
        newArrayList(PagedModel.class, CollectionModel.class, EntityModel.class, RepresentationModel.class);

    @Override
    public List<String> get(@NonNull RepresentationModel<?> model) {
        return newArrayList(uncapitalize(substringBeforeLast(modelClass(model.getClass()).getSimpleName(), "Model")));
    }

    private static Class<?> modelClass(Class<?> modelType) {
        for (Class<?> type : RESOURCE_TYPES) {
            if (type.isAssignableFrom(modelType)) {
                return type;
            }
        }
        throw new IllegalStateException(format("Weird things happen! Type of model is not of type '%s' [but of type '%s']!",
            RepresentationModel.class.getName(), modelType.getName()));
    }

}
