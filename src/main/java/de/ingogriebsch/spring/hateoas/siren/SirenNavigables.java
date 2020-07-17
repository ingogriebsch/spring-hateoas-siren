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

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.Link;

/**
 * Representation to hold specific {@link SirenLink}s and their {@link SirenAction}s together.
 *
 * @see Link
 * @see Affordance
 * @author Ingo Griebsch
 */
// TODO Find a better name for this class.
@AllArgsConstructor(access = PRIVATE)
@EqualsAndHashCode
@Getter
class SirenNavigables {

    private final List<SirenLink> links;
    private final List<SirenAction> actions;

    static SirenNavigables navigables(List<SirenLink> links, List<SirenAction> actions) {
        return of(links, actions);
    }

    static SirenNavigables of(List<SirenLink> links, List<SirenAction> actions) {
        return new SirenNavigables(links, actions);
    }

    static SirenNavigables merge(Iterable<SirenNavigables> navigables) {
        List<SirenLink> links = stream(navigables.spliterator(), false).flatMap(n -> n.getLinks().stream()).collect(toList());
        List<SirenAction> actions =
            stream(navigables.spliterator(), false).flatMap(n -> n.getActions().stream()).collect(toList());
        return SirenNavigables.of(links, actions);
    }
}
