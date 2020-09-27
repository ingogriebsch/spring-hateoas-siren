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

import static com.google.common.collect.Lists.newArrayList;
import static de.ingogriebsch.spring.hateoas.siren.SirenNavigables.navigables;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.lang.Nullable;

/**
 * Builder that helps the {@link SirenPagedModelDeserializer} to deserialize a Siren entity into a {@link PagedModel}.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see PagedModel
 */
@RequiredArgsConstructor(access = PRIVATE)
class SirenPagedModelBuilder {

    private final JavaType type;
    private final PagedModelFactory modelFactory;
    private final SirenLinkConverter linkConverter;

    private List<SirenAction> actions = newArrayList();
    private List<SirenLink> links = newArrayList();
    private List<Object> content = newArrayList();
    private PageMetadata metadata;

    static SirenPagedModelBuilder builder(JavaType type, PagedModelFactory modelFactory, SirenLinkConverter linkConverter) {
        return new SirenPagedModelBuilder(type, modelFactory, linkConverter);
    }

    SirenPagedModelBuilder metadata(PageMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    SirenPagedModelBuilder content(List<Object> content) {
        this.content = content;
        return this;
    }

    SirenPagedModelBuilder links(@Nullable List<SirenLink> links) {
        this.links = links != null ? links : newArrayList();
        return this;
    }

    SirenPagedModelBuilder actions(@Nullable List<SirenAction> actions) {
        this.actions = actions != null ? actions : newArrayList();
        return this;
    }

    PagedModel<?> build() {
        return modelFactory.create(type, links(), content, metadata);
    }

    private Iterable<Link> links() {
        return linkConverter.from(navigables(links, actions));
    }
}
