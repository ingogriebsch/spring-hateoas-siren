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
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.DATE;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.DATETIME_LOCAL;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.FILE;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.MONTH;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.NUMBER;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.TEXT;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.URL;
import static de.ingogriebsch.spring.hateoas.siren.TypeMapping.typeMapping;
import static lombok.AccessLevel.PACKAGE;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.http.MediaType;

/**
 * A {@link SirenActionFieldTypeConverter} implementation the determines the {@link SirenActionFieldType} based on the type of the
 * field (i.e. the specific property of the payload).
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 */
class TypeBasedSirenActionFieldTypeConverter implements SirenActionFieldTypeConverter {

    private static final List<TypeMapping> DEFAULT_MAPPINGS = newArrayList( //
        typeMapping(Date.class, DATE), //
        typeMapping(LocalDateTime.class, DATETIME_LOCAL), //
        typeMapping(File.class, FILE), //
        typeMapping(Path.class, FILE), //
        typeMapping(Month.class, MONTH), //
        typeMapping(Number.class, NUMBER), //
        typeMapping(URL.class, URL), //
        typeMapping(URI.class, URL) //
    );

    private final List<TypeMapping> mappings;

    TypeBasedSirenActionFieldTypeConverter() {
        this(null);
    }

    TypeBasedSirenActionFieldTypeConverter(List<TypeMapping> mappings) {
        this.mappings = mappings != null ? mappings : newArrayList();
    }

    @Override
    public SirenActionFieldType convert(@NonNull PropertyMetadata fieldMetadata, @NonNull MediaType actionType) {
        Class<?> type = obtainType(fieldMetadata);

        SirenActionFieldType target = map(type, mappings);
        target = target != null ? target : map(type, DEFAULT_MAPPINGS);
        return target = target != null ? target : TEXT;
    }

    private static Class<?> obtainType(PropertyMetadata fieldMetadata) {
        return fieldMetadata.getType().getRawClass();
    }

    private static SirenActionFieldType map(Class<?> type, List<TypeMapping> mappings) {
        for (TypeMapping mapping : mappings) {
            if (mapping.getSource().isAssignableFrom(type)) {
                return mapping.getTarget();
            }
        }
        return null;
    }
}

@Data
@Getter(PACKAGE)
@RequiredArgsConstructor(access = PACKAGE)
class TypeMapping {

    private final Class<?> source;
    private final SirenActionFieldType target;

    static TypeMapping typeMapping(Class<?> source, SirenActionFieldType target) {
        return new TypeMapping(source, target);
    }
}
