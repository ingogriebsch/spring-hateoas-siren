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

import static com.github.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.hateoas.mediatype.MessageResolver.DEFAULTS_ONLY;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.support.SimpleObjectProvider;

class SirenMediaTypeConfigurationTest {

    private static SirenMediaTypeConfiguration sirenMediaTypeConfiguration;

    @BeforeAll
    static void beforeAll() {
        SimpleObjectProvider<SirenConfiguration> sirenConfiguration = new SimpleObjectProvider<>(new SirenConfiguration());
        SimpleObjectProvider<SirenEntityClassProvider> sirenEntityClassProvider =
            new SimpleObjectProvider<>(new SimpleSirenEntityClassProvider());

        sirenMediaTypeConfiguration =
            new SirenMediaTypeConfiguration(sirenConfiguration, sirenEntityClassProvider, DEFAULTS_ONLY);
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
        void should_throw_exception_if_input_is_null() {
            assertThrows(IllegalArgumentException.class, () -> sirenMediaTypeConfiguration.configureObjectMapper(null));
        }
    }

}
