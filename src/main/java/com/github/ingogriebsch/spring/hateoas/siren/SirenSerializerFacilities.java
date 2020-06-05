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

import static lombok.AccessLevel.PACKAGE;

import org.springframework.hateoas.mediatype.MessageResolver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

/**
 * Collection of facilities needed to serialize a Siren entity.
 *
 * @author Ingo Griebsch
 */
@AllArgsConstructor(access = PACKAGE)
@Getter(PACKAGE)
@Value
class SirenSerializerFacilities {

    SirenEntityClassProvider entityClassProvider;
    SirenEntityRelProvider entityRelProvider;
    SirenLinkConverter linkConverter;
    MessageResolver messageResolver;

}
