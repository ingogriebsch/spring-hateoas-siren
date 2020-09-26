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

import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.TEXT;

import lombok.NonNull;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.http.MediaType;

/**
 * A converter that is responsible for determine a {@link SirenActionFieldType} based on the property that is part of an
 * {@link AffordanceModel} and the type of the surrounding action.
 * 
 * @author Ingo Griebsch
 * @see <a href="https://github.com/kevinswiber/siren#type-3" target="_top">https://github.com/kevinswiber/siren#type-3</a>
 * @see <a href="https://github.com/kevinswiber/siren#type-2" target="_top">https://github.com/kevinswiber/siren#type-2</a>
 */
public interface SirenActionFieldTypeConverter {

    /**
     * Converts some {@link PropertyMetadata field metadata} and {@link MediaType action type} into a
     * {@link SirenActionFieldType}.
     * <p>
     * The default implementation always returns the fallback, as specified through the specification.
     * 
     * @param fieldMetadata the metadata of the field. Is never {@literal null}.
     * @param actionType the type of the action. Is never {@literal null}.
     * @return the matching {@link SirenActionFieldType}.
     */
    default SirenActionFieldType convert(@NonNull PropertyMetadata fieldMetadata, @NonNull MediaType actionType) {
        return TEXT;
    }
}
