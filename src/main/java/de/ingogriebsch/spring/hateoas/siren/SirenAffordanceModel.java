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

import java.util.List;

import lombok.EqualsAndHashCode;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.QueryParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * {@link AffordanceModel} for a Siren {@link MediaType}.
 *
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see AffordanceModel
 */
@EqualsAndHashCode(callSuper = true)
class SirenAffordanceModel extends AffordanceModel {

    SirenAffordanceModel(String name, Link link, HttpMethod httpMethod, InputPayloadMetadata inputType,
        List<QueryParameter> queryMethodParameters, PayloadMetadata outputType) {
        super(name, link, httpMethod, inputType, queryMethodParameters, outputType);
    }

}
