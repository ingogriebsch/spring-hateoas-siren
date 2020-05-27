package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
class SirenRepresentationModelBuilder {

    @NonNull
    private final JavaType type;
    @NonNull
    private final RepresentationModelFactory modelFactory;
    @NonNull
    private final SirenLinkConverter linkConverter;

    private Map<String, Object> properties = newHashMap();
    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();

    static SirenRepresentationModelBuilder builder(@NonNull JavaType type, @NonNull RepresentationModelFactory modelFactory,
        @NonNull SirenLinkConverter linkConverter) {
        return new SirenRepresentationModelBuilder(type, modelFactory, linkConverter);
    }

    SirenRepresentationModelBuilder properties(@NonNull Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    SirenRepresentationModelBuilder links(List<SirenLink> links) {
        this.links = links != null ? links : newArrayList();
        return this;
    }

    SirenRepresentationModelBuilder actions(List<SirenAction> actions) {
        this.actions = actions != null ? actions : newArrayList();
        return this;
    }

    private List<Link> links() {
        return linkConverter.from(navigables(links, actions));
    }

    RepresentationModel<?> build() {
        return modelFactory.create(type, links(), properties);
    }
}
