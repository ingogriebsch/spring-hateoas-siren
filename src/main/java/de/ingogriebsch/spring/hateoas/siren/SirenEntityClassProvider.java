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
import static de.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.getRepresentationModelClass;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.util.List;

import lombok.NonNull;
import org.springframework.hateoas.RepresentationModel;

/**
 * SPI to provide information about the nature of the content of a Siren entity.
 *
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see <a href="https://github.com/kevinswiber/siren#class" target="_blank">Siren Entity Class</a>
 */
public interface SirenEntityClassProvider {

    /**
     * The default instance that can be used if no specific implementation of this interface is necessary.
     */
    SirenEntityClassProvider DEFAULT_INSTANCE = new SirenEntityClassProvider() {
    };

    /**
     * Returns the class(es) explaining the nature of the content of a Siren entity.
     * <p>
     * The default implementation returns a simplified form of the class name of the {@link RepresentationModel} instance (not
     * containing 'Model').
     * 
     * @param model the {@link RepresentationModel} that is transfered into a Siren entity. Is never {@literal null}.
     * @return the classes explaining the nature of the content of a Siren entity.
     */
    default List<String> get(@NonNull RepresentationModel<?> model) {
        return newArrayList(
            uncapitalize(substringBeforeLast(getRepresentationModelClass(model.getClass()).getSimpleName(), "Model")));
    }

}
