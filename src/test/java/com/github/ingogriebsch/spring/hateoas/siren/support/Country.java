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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

@Data
@EqualsAndHashCode(callSuper = true)
public class Country extends CollectionModel<State> {

    @NonNull
    private String name;

    // TODO Need to clarify with the Spring gals if having a protected ctor to allow subclassing is possible.
    @SuppressWarnings("deprecation")
    public Country(@NonNull Iterable<State> content, @NonNull Iterable<Link> links) {
        super(content, links);
    }

    // TODO Need to clarify with the Spring gals if having a protected ctor to allow subclassing is possible.
    @SuppressWarnings("deprecation")
    public Country(@NonNull Iterable<State> content, Link... links) {
        super(content, links);
    }

    public Country(@NonNull String name, @NonNull Iterable<State> content, @NonNull Link... links) {
        this(content, links);
        this.name = name;
    }
}
