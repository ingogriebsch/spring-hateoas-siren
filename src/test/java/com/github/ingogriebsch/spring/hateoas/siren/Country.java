package com.github.ingogriebsch.spring.hateoas.siren;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
class Country extends CollectionModel<State> {

    @NonNull
    private String name;

    Country(@NonNull Iterable<State> content, @NonNull Iterable<Link> links) {
        super(content, links);
    }

    Country(@NonNull Iterable<State> content, Link... links) {
        super(content, links);
    }

    Country(@NonNull String name, @NonNull Iterable<State> content, @NonNull Link... links) {
        this(content, links);
        this.name = name;
    }
}
