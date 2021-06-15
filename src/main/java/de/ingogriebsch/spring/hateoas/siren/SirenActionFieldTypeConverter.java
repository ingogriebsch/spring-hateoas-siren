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

import static java.util.Optional.ofNullable;

import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.TEXT;

import java.util.Optional;

import lombok.NonNull;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.html.HtmlInputType;
import org.springframework.http.MediaType;

/**
 * SPI to convert a property that is part of an {@link AffordanceModel} and the type of the surrounding Siren action into a
 * {@link SirenActionFieldType}.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see SirenActionFieldType
 * @see <a href="https://github.com/kevinswiber/siren#type-3" target="_blank">Siren Action Field Type</a>
 * @see <a href="https://github.com/kevinswiber/siren#type-2" target="_blank">Siren Action Type</a>
 */
public interface SirenActionFieldTypeConverter {

    /**
     * The default instance that can be used if no specific implementation of this interface is necessary.
     */
    SirenActionFieldTypeConverter DEFAULT_INSTANCE = new SirenActionFieldTypeConverter() {
    };

    /**
     * Converts the given {@link PropertyMetadata property metadata} and the {@link MediaType type} of the respective Siren action
     * into a {@link SirenActionFieldType}.
     * <p>
     * The default implementation always returns the fallback, as specified through the specification.
     * 
     * @param fieldMetadata the metadata of the property. Is never {@literal null}.
     * @param actionType the type of the action. Is never {@literal null}.
     * @return the matching {@link SirenActionFieldType}.
     * @deprecated use {@link SirenActionFieldTypeConverter#execute(PropertyMetadata, MediaType)} instead.
     */
    @Deprecated
    default SirenActionFieldType convert(@NonNull PropertyMetadata fieldMetadata, @NonNull MediaType actionType) {
        return TEXT;
    }

    /**
     * Converts the given {@link PropertyMetadata property metadata} and the {@link MediaType type} of the respective Siren action
     * into a {@link HtmlInputType}, if possible.
     * <p>
     * The default implementation always returns the fallback, as specified through the specification.
     * 
     * @param fieldMetadata the metadata of the property. Is never {@literal null}.
     * @param actionType the type of the action. Is never {@literal null}.
     * @return the matching {@link HtmlInputType} or an empty {@link Optional}.
     */
    @SuppressWarnings("deprecation")
    default Optional<HtmlInputType> execute(@NonNull PropertyMetadata fieldMetadata, @NonNull MediaType actionType) {
        return ofNullable(convert(fieldMetadata, actionType)).map(SirenActionFieldType::getType);
    }
}
