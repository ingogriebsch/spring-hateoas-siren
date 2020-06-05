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
package com.github.ingogriebsch.spring.hateoas.siren.support;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class State extends EntityModel<Capital> {

    @NonNull
    private String name;

    // TODO Need to clarify with the Spring gals if having a protected ctor to allow subclassing is possible.
    @SuppressWarnings("deprecation")
    public State(@NonNull Capital content, @NonNull Iterable<Link> links) {
        super(content, links);
    }

    // TODO Need to clarify with the Spring gals if having a protected ctor to allow subclassing is possible.
    @SuppressWarnings("deprecation")
    public State(@NonNull Capital content, @NonNull Link... links) {
        super(content, links);
    }

    public State(@NonNull String name, @NonNull Capital content, @NonNull Link... links) {
        this(content);
        this.name = name;
    }
}
