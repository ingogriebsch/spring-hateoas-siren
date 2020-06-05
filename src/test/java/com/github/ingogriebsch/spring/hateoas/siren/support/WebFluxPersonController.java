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
package com.github.ingogriebsch.spring.hateoas.siren.support;

import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static reactor.function.TupleUtils.function;

import java.util.Map;
import java.util.Optional;

import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Sample controller using {@link WebFluxLinkBuilder} to create {@link Affordance}s.
 *
 * @author Greg Turnquist
 * @author Oliver Drotbohm
 */
@RestController
public class WebFluxPersonController {

    private static Map<Integer, Person> PERSONS;

    public static void reset() {
        PERSONS = newHashMap();
        PERSONS.put(0, new Person("Peter", 33));
        PERSONS.put(1, new Person("Paul", 44));
        PERSONS.put(2, new Person("Mary", 55));
    }

    @GetMapping("/persons")
    public Mono<CollectionModel<EntityModel<Person>>> findAll() {
        WebFluxPersonController controller = methodOn(WebFluxPersonController.class);

        return Flux.fromIterable(PERSONS.keySet()) //
            .flatMap(this::findOne) //
            .collectList() //
            .flatMap(resources -> linkTo(controller.findAll()).withSelfRel() //
                .andAffordance(controller.insert(null)) //
                .andAffordance(controller.search(null, null)) //
                .toMono() //
                .map(selfLink -> new CollectionModel<>(resources, selfLink)));
    }

    @GetMapping("/persons/search")
    public Mono<CollectionModel<EntityModel<Person>>> search(@RequestParam Optional<String> name,
        @RequestParam Optional<Integer> age) {
        WebFluxPersonController controller = methodOn(WebFluxPersonController.class);

        return Flux.fromIterable(PERSONS.keySet()) //
            .flatMap(this::findOne) //
            .filter(resource -> {
                boolean nameMatches = name //
                    .map(s -> resource.getContent().getName().contains(s)) //
                    .orElse(true);
                boolean ageMatches = age.map(s -> resource.getContent().getAge().equals(s)) //
                    .orElse(true);

                return nameMatches && ageMatches;
            }).collectList().flatMap(resources -> linkTo(controller.findAll()) //
                .withSelfRel() //
                .andAffordance(controller.insert(null)) //
                .andAffordance(controller.search(null, null)) //
                .toMono() //
                .map(selfLink -> new CollectionModel<>(resources, selfLink)));
    }

    @GetMapping("/persons/{id}")
    public Mono<EntityModel<Person>> findOne(@PathVariable Integer id) {
        WebFluxPersonController controller = methodOn(WebFluxPersonController.class);

        Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel() //
            .andAffordance(controller.update(id, null)) //
            .andAffordance(controller.patch(id, null)) //
            .toMono();

        Mono<Link> personsLink = linkTo(controller.findAll()).withRel("persons") //
            .toMono();

        return selfLink.zipWith(personsLink) //
            .map(function((left, right) -> Links.of(left, right))) //
            .map(links -> new EntityModel<>(PERSONS.get(id), links));
    }

    @PostMapping("/persons")
    public Mono<ResponseEntity<?>> insert(@RequestBody Mono<EntityModel<Person>> person) {
        return person //
            .flatMap(resource -> {
                int personId = PERSONS.size();
                PERSONS.put(personId, resource.getContent());
                return findOne(personId);
            }).map(findOne -> created(findOne.getRequiredLink(SELF).toUri()).build());
    }

    @PutMapping("/persons/{id}")
    public Mono<ResponseEntity<?>> update(@PathVariable Integer id, @RequestBody Mono<EntityModel<Person>> person) {
        return person.flatMap(resource -> {
            PERSONS.put(id, resource.getContent());
            return findOne(id);
        }).map(findOne -> noContent().location(findOne.getRequiredLink(SELF).toUri()).build());
    }

    @PatchMapping("/persons/{id}")
    public Mono<ResponseEntity<?>> patch(@PathVariable Integer id, @RequestBody Mono<EntityModel<Person>> person) {
        return person //
            .flatMap(resource -> {
                Person patchedPerson = PERSONS.get(id);
                if (resource.getContent().getName() != null) {
                    patchedPerson.setName(resource.getContent().getName());
                }
                if (resource.getContent().getAge() != null) {
                    patchedPerson.setAge(resource.getContent().getAge());
                }
                return findOne(id);
            }).map(findOne -> noContent().location(findOne.getRequiredLink(SELF).toUri()).build());
    }
}
