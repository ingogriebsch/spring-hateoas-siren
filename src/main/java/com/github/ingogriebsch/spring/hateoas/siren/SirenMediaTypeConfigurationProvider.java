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

import java.util.Collection;

import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.config.MediaTypeConfigurationProvider;
import org.springframework.http.MediaType;

import lombok.NonNull;

class SirenMediaTypeConfigurationProvider implements MediaTypeConfigurationProvider {

    @Override
    public Class<? extends HypermediaMappingInformation> getConfiguration() {
        return SirenMediaTypeConfiguration.class;
    }

    @Override
    public boolean supportsAny(@NonNull Collection<MediaType> mediaTypes) {
        // FIXME Need to clarify how to configure this custom media type to be active!
        return true; // mediaTypes.contains(MediaTypes.SIREN_JSON);
    }

}
