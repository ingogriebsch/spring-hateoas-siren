package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static com.github.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
class SirenCollectionModelBuilder {

    @NonNull
    private final Class<?> type;
    @NonNull
    private final SirenLinkConverter linkConverter;

    private Map<String, Object> properties = newHashMap();
    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();
    private List<Object> content = newArrayList();

    static SirenCollectionModelBuilder builder(@NonNull Class<?> type, @NonNull SirenLinkConverter linkConverter) {
        return new SirenCollectionModelBuilder(type, linkConverter);
    }

    SirenCollectionModelBuilder properties(@NonNull Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    SirenCollectionModelBuilder content(@NonNull List<Object> content) {
        this.content = content;
        return this;
    }

    SirenCollectionModelBuilder links(List<SirenLink> links) {
        this.links = links != null ? links : newArrayList();
        return this;
    }

    SirenCollectionModelBuilder actions(List<SirenAction> actions) {
        this.actions = actions != null ? actions : newArrayList();
        return this;
    }

    private Iterable<Link> links() {
        return linkConverter.from(navigables(links, actions));
    }

    CollectionModel<?> build() {
        CollectionModel<?> model = (CollectionModel<?>) instantiate(type, new Class[] { Iterable.class, Iterable.class },
            new Object[] { content, links() });
        BeanUtils.applyProperties(model, properties);
        return model;
    }

}
