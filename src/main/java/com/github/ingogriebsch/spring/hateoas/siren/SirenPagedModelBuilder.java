package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static com.github.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static com.google.common.collect.Lists.newArrayList;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PRIVATE)
class SirenPagedModelBuilder {

    @NonNull
    private final Class<?> type;
    @NonNull
    private final SirenLinkConverter linkConverter;

    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();
    private List<Object> content = newArrayList();
    private PageMetadata metadata;

    static SirenPagedModelBuilder builder(@NonNull Class<?> type, @NonNull SirenLinkConverter linkConverter) {
        return new SirenPagedModelBuilder(type, linkConverter);
    }

    SirenPagedModelBuilder metadata(@NonNull PageMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    SirenPagedModelBuilder content(@NonNull List<Object> content) {
        this.content = content;
        return this;
    }

    SirenPagedModelBuilder links(List<SirenLink> links) {
        this.links = links != null ? links : newArrayList();
        return this;
    }

    SirenPagedModelBuilder actions(List<SirenAction> actions) {
        this.actions = actions != null ? actions : newArrayList();
        return this;
    }

    private Iterable<Link> links() {
        return linkConverter.from(navigables(links, actions));
    }

    PagedModel<?> build() {
        PagedModel<?> model = (PagedModel<?>) instantiate(type,
            new Class[] { Collection.class, PageMetadata.class, Iterable.class }, new Object[] { content, metadata, links() });
        return model;
    }

}
