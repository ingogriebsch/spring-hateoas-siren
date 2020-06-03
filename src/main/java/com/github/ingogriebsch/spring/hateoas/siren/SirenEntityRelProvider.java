package com.github.ingogriebsch.spring.hateoas.siren;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.IanaLinkRelations.ITEM;

import java.util.List;

import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;

public interface SirenEntityRelProvider {

    default List<LinkRelation> get(@NonNull RepresentationModel<?> model, RepresentationModel<?> parent) {
        return parent != null ? newArrayList(ITEM) : newArrayList();
    }
}
