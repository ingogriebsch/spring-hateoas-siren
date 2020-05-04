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

import java.util.List;

import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.QueryParameter;
import org.springframework.http.HttpMethod;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
class SirenAffordanceModel extends AffordanceModel {

    public SirenAffordanceModel(String name, Link link, HttpMethod httpMethod, InputPayloadMetadata inputType,
        List<QueryParameter> queryMethodParameters, PayloadMetadata outputType) {
        super(name, link, httpMethod, inputType, queryMethodParameters, outputType);
    }

}
