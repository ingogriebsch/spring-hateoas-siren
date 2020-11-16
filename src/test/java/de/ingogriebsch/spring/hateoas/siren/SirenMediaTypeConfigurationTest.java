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

import static de.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;

import java.text.DateFormat;
import java.util.Locale;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SirenMediaTypeConfigurationTest {

    private static SirenMediaTypeConfiguration sirenMediaTypeConfiguration;

    @BeforeAll
    static void beforeAll() {
        sirenMediaTypeConfiguration = SirenMediaTypeConfiguration.of(DEFAULTS_ONLY, new SirenConfiguration(),
            SirenEntityClassProvider.DEFAULT_INSTANCE, SirenEntityRelProvider.DEFAULT_INSTANCE,
            new TypeBasedSirenActionFieldTypeConverter(), RepresentationModelFactories.DEFAULT_INSTANCE);
    }

    @Nested
    class GetMediaTypes {

        @Test
        void should_return_matching_media_type() {
            assertThat(sirenMediaTypeConfiguration.getMediaTypes()).containsExactly(SIREN_JSON);
        }
    }

    @Nested
    class GetJacksonModule {

        @Test
        void should_return_matching_jackson_module() {
            assertThat(sirenMediaTypeConfiguration.getJacksonModule()).isNotNull().isInstanceOf(Jackson2SirenModule.class);
        }
    }

    @Nested
    class ConfigureObjectMapper {

        @Test
        void should_apply_customizer() {
            DateFormat flag = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);

            Consumer<ObjectMapper> objectMapperCustomizer = (objectMapper) -> {
                objectMapper.setDateFormat(flag);
            };

            SirenMediaTypeConfiguration configuration = SirenMediaTypeConfiguration.of( //
                DEFAULTS_ONLY, //
                new SirenConfiguration().withObjectMapperCustomizer(objectMapperCustomizer), //
                SirenEntityClassProvider.DEFAULT_INSTANCE, //
                SirenEntityRelProvider.DEFAULT_INSTANCE, //
                new TypeBasedSirenActionFieldTypeConverter(), //
                RepresentationModelFactories.DEFAULT_INSTANCE //
            );

            ObjectMapper objectMapper = new ObjectMapper();
            assertThat(objectMapper.getDateFormat()).isNotEqualTo(flag);
            configuration.configureObjectMapper(objectMapper);
            assertThat(objectMapper.getDateFormat()).isEqualTo(flag);
        }
    }
}
