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
import static de.ingogriebsch.spring.hateoas.siren.TypeMapping.typeMapping;
import static lombok.AccessLevel.PACKAGE;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.html.HtmlInputType;

/**
 * A Siren specific configuration that allows to configure specific parts of the library.
 *
 * @author Ingo Griebsch
 * @since 1.0.0
 */
@Getter(PACKAGE)
public class SirenConfiguration {

    private boolean entityAndCollectionModelSubclassingEnabled = false;
    private List<TypeMapping> actionFieldTypeMappings = newArrayList();
    private Consumer<ObjectMapper> objectMapperCustomizer = objectMapper -> {
    };

    /**
     * Configures whether it is possible to subclass {@link EntityModel}s and {@link CollectionModel}s.
     * Defaults to {@literal false}.
     * 
     * @param enabled whether subclassing of {@link EntityModel}s and {@link CollectionModel}s should be enabled or not.
     * @return the updated configuration reflecting the setting
     */
    public SirenConfiguration withEntityAndCollectionModelSubclassingEnabled(boolean enabled) {
        entityAndCollectionModelSubclassingEnabled = enabled;
        return this;
    }

    /**
     * Configures additional mappings that are used to convert the type of a payload field into a Siren action field type.
     * <p>
     * These mappings override the default mappings that are defined inside the library. The order of the defined mappings is
     * important because the class hierarchy is taken into consideration.
     * 
     * @param source the type of the member that is part of the payload that is transformed into Siren action fields.
     * @param target the {@link SirenActionFieldType type} of the Siren Action field that should represent the {@literal source}
     *        type in the Siren representation.
     * @return the updated configuration reflecting the setting
     * @deprecated use {@link SirenConfiguration#withActionFieldTypeMappging(Class, HtmlInputType)} instead.
     */
    @Deprecated
    public SirenConfiguration withActionFieldTypeMappging(@NonNull Class<?> source, @NonNull SirenActionFieldType target) {
        actionFieldTypeMappings.add(typeMapping(source, target.getType()));
        return this;
    }

    /**
     * Configures additional mappings that are used to convert the type of a payload field into a Siren action field type.
     * <p>
     * These mappings override the default mappings that are defined inside the library. The order of the defined mappings is
     * important because the class hierarchy is taken into consideration.
     * 
     * @param source the type of the member that is part of the payload that is transformed into Siren action fields.
     * @param target the {@link HtmlInputType type} of the Siren Action field that should represent the {@literal source}
     *        type in the Siren representation.
     * @return the updated configuration reflecting the setting
     */
    public SirenConfiguration withActionFieldTypeMappging(@NonNull Class<?> source, @NonNull HtmlInputType target) {
        actionFieldTypeMappings.add(typeMapping(source, target));
        return this;
    }

    /**
     * Configures the {@link ObjectMapper} instance that is used for serialization.
     * 
     * @param objectMapperCustomizer the customizer which is used to apply additional customizations to the {@link ObjectMapper}
     *        instance that is used for serialization.
     * @return the updated configuration reflecting the setting
     */
    public SirenConfiguration withObjectMapperCustomizer(@NonNull Consumer<ObjectMapper> objectMapperCustomizer) {
        this.objectMapperCustomizer = objectMapperCustomizer;
        return this;
    }
}
