package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static com.github.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
class SirenRepresentationModelBuilder {

    @NonNull
    private final Class<?> type;
    @NonNull
    private final SirenLinkConverter linkConverter;

    private Map<String, Object> properties = newHashMap();
    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();

    static SirenRepresentationModelBuilder builder(@NonNull Class<?> type, @NonNull SirenLinkConverter linkConverter) {
        return new SirenRepresentationModelBuilder(type, linkConverter);
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
        RepresentationModel<?> model = (RepresentationModel<?>) instantiate(type, new Class[] {}, new Object[] {});
        applyProperties(model, properties);
        model.add(links());
        return model;
    }
}