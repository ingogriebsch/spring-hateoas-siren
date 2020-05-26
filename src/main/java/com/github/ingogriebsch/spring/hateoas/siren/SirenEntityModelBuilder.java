package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static com.github.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static com.google.common.collect.Lists.newArrayList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
class SirenEntityModelBuilder {

    @NonNull
    private final Class<?> type;
    @NonNull
    private final SirenLinkConverter linkConverter;

    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();
    private Object content;

    static SirenEntityModelBuilder builder(@NonNull Class<?> type, @NonNull SirenLinkConverter linkConverter) {
        return new SirenEntityModelBuilder(type, linkConverter);
    }

    SirenEntityModelBuilder content(@NonNull Object content) {
        this.content = content;
        return this;
    }

    SirenEntityModelBuilder links(List<SirenLink> links) {
        this.links = links != null ? links : newArrayList();
        return this;
    }

    SirenEntityModelBuilder actions(List<SirenAction> actions) {
        this.actions = actions != null ? actions : newArrayList();
        return this;
    }

    private Iterable<Link> links() {
        return linkConverter.from(navigables(links, actions));
    }

    EntityModel<?> build() {
        EntityModel<?> model =
            (EntityModel<?>) instantiate(type, new Class[] { Object.class, Iterable.class }, new Object[] { content, links() });
        return model;
    }
}
