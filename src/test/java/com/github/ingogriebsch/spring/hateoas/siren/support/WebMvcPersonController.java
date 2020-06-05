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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.hateoas.IanaLinkRelations.SELF;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebMvcPersonController {

    private static Map<Integer, Person> PERSONS;

    public static void reset() {
        PERSONS = newHashMap();
        PERSONS.put(0, new Person("Peter", 33));
        PERSONS.put(1, new Person("Paul", 44));
        PERSONS.put(2, new Person("Mary", 55));
    }

    @GetMapping("/persons")
    public CollectionModel<EntityModel<Person>> findAll() {
        WebMvcPersonController controller = methodOn(WebMvcPersonController.class);
        Link selfLink = linkTo(controller.findAll()).withSelfRel() //
            .andAffordance(afford(controller.insert(null))) //
            .andAffordance(afford(controller.search(null, null)));

        return range(0, PERSONS.size()) //
            .mapToObj(this::findOne) //
            .collect(collectingAndThen(toList(), it -> new CollectionModel<>(it, selfLink)));
    }

    @GetMapping("/persons/search")
    public CollectionModel<EntityModel<Person>> search(@RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "age", required = false) Integer age) {

        List<EntityModel<Person>> persons = new ArrayList<>();
        for (int i = 0; i < PERSONS.size(); i++) {
            EntityModel<Person> personModel = findOne(i);

            boolean nameMatches = Optional.ofNullable(name) //
                .map(s -> personModel.getContent().getName().contains(s)) //
                .orElse(true);
            boolean ageMatches = Optional.ofNullable(age) //
                .map(s -> personModel.getContent().getAge().equals(s)) //
                .orElse(true);
            if (nameMatches && ageMatches) {
                persons.add(personModel);
            }
        }

        WebMvcPersonController controller = methodOn(WebMvcPersonController.class);
        Link selfLink = linkTo(controller.findAll()).withSelfRel() //
            .andAffordance(afford(controller.insert(null))) //
            .andAffordance(afford(controller.search(null, null)));

        return new CollectionModel<>(persons, selfLink);
    }

    @GetMapping("/persons/{id}")
    public EntityModel<Person> findOne(@PathVariable Integer id) {
        WebMvcPersonController controller = methodOn(WebMvcPersonController.class);
        Link findOneLink = linkTo(controller.findOne(id)).withSelfRel();
        Link personsLink = linkTo(controller.findAll()).withRel("persons");

        return new EntityModel<>(PERSONS.get(id), findOneLink //
            .andAffordance(afford(controller.update(id, null))) //
            .andAffordance(afford(controller.patch(id, null))), //
            personsLink);
    }

    @PostMapping("/persons")
    public ResponseEntity<?> insert(@RequestBody EntityModel<Person> person) {
        int personId = PERSONS.size();
        PERSONS.put(personId, person.getContent());
        return created(linkTo(methodOn(getClass()).findOne(personId)).withSelfRel().expand().toUri()).build();
    }

    @PutMapping("/persons/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody EntityModel<Person> person) {
        PERSONS.put(id, person.getContent());
        return noContent().location(linkTo(methodOn(getClass()).findOne(id)).withSelfRel().expand().toUri()).build();
    }

    @PatchMapping("/persons/{id}")
    public ResponseEntity<?> patch(@PathVariable Integer id, @RequestBody EntityModel<Person> person) {
        Person patchedPerson = PERSONS.get(id);
        if (person.getContent().getAge() != null) {
            patchedPerson.setAge(person.getContent().getAge());
        }
        if (person.getContent().getName() != null) {
            patchedPerson.setName(person.getContent().getName());
        }

        return noContent().location(findOne(id).getRequiredLink(SELF).toUri()).build();
    }
}
