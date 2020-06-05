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

import java.util.List;

import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.AffordanceModel.PayloadMetadata;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.QueryParameter;
import org.springframework.hateoas.mediatype.AffordanceModelFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Factory for creating {@link SirenAffordanceModel}s.
 *
 * @author Ingo Griebsch
 */
class SirenAffordanceModelFactory implements AffordanceModelFactory {

    /*
     * (non-Javadoc)
     * @see org.springframework.hateoas.AffordanceModelFactory#getMediaType()
     */
    @Override
    public MediaType getMediaType() {
        return SIREN_JSON;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.hateoas.AffordanceModelFactory#getAffordanceModel(java.lang.String,
     * org.springframework.hateoas.Link, org.springframework.http.HttpMethod, org.springframework.core.ResolvableType,
     * java.util.List, org.springframework.core.ResolvableType)
     */
    @Override
    public AffordanceModel getAffordanceModel(String name, Link link, HttpMethod httpMethod, InputPayloadMetadata inputType,
        List<QueryParameter> queryMethodParameters, PayloadMetadata outputType) {
        return new SirenAffordanceModel(name, link, httpMethod, inputType, queryMethodParameters, outputType);
    }

}
