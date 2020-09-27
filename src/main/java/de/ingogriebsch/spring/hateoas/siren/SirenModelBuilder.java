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

import static java.lang.String.format;

import static com.google.common.collect.Lists.newArrayList;
import static de.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.isRepresentationModel;
import static de.ingogriebsch.spring.hateoas.siren.RepresentationModelUtils.wrap;
import static org.apache.commons.lang3.Validate.noNullElements;
import static org.springframework.hateoas.Links.of;
import static org.springframework.hateoas.Links.MergeMode.REPLACE_BY_REL;

import java.util.List;

import de.ingogriebsch.spring.hateoas.siren.SirenModel.EmbeddedRepresentation;
import lombok.NonNull;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

/**
 * A builder that allows to build complex Siren entity structures.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see <a href="https://github.com/kevinswiber/siren#entity" target="_blank">Siren Entity</a>
 */
public final class SirenModelBuilder {

    private final List<EmbeddedRepresentation> entities = newArrayList();
    private final List<String> classes = newArrayList();
    private Links linksAndActions = Links.of();
    private Object properties;
    private String title;

    private SirenModelBuilder() {
    }

    /**
     * Creates a new {@link SirenModelBuilder}.
     * 
     * @return the created {@link SirenModelBuilder} instance.
     */
    public static SirenModelBuilder sirenModel() {
        return new SirenModelBuilder();
    }

    /**
     * Adds the given {@literal classes} to the {@link RepresentationModel} to be built.
     * 
     * @param classes must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#class" target="_blank">Siren Entity class</a>
     */
    public SirenModelBuilder classes(@NonNull String... classes) {
        return classes(newArrayList(classes));
    }

    /**
     * Adds the given {@literal classes} to the {@link RepresentationModel} to be built.
     * 
     * @param classes must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#class" target="_blank">Siren Entity class</a>
     */
    public SirenModelBuilder classes(@NonNull Iterable<String> classes) {
        noNullElements(classes);
        classes.forEach(this.classes::add);
        return this;
    }

    /**
     * Adds the given {@literal title} to the {@link RepresentationModel} to be built.
     * 
     * @param title must not be {@literal null}.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#title" target="_blank">Siren Entity title</a>
     */
    public SirenModelBuilder title(@NonNull String title) {
        this.title = title;
        return this;
    }

    /**
     * Adds the given {@literal properties} to the {@link RepresentationModel} to be built.
     * 
     * @param properties must not be {@literal null} and must not be of type {@literal RepresentationModel}.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#properties" target="_blank">Siren Entity properties</a>
     */
    public SirenModelBuilder properties(@NonNull Object properties) {
        assertNotOfTypeRepresentationModel(properties.getClass());
        this.properties = properties;
        return this;
    }

    /**
     * Adds the given {@literal entity} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entity} will be wrapped into an appropriate {@link RepresentationModel} if necessary. The relation to its
     * parent will be evaluated through the configured {@link SirenEntityRelProvider}.
     * 
     * @param entity must not be {@literal null}.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull Object entity) {
        return entities(newArrayList(entity));
    }

    /**
     * Adds the given {@literal entities} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entities} will be wrapped into appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent will be evaluated through the configured {@link SirenEntityRelProvider}.
     * 
     * @param entities must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull Object... entities) {
        return entities(newArrayList(entities));
    }

    /**
     * Adds the given {@literal entities} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entities} will be wrapped into appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent will be evaluated through the configured {@link SirenEntityRelProvider}.
     *
     * @param entities must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull Iterable<?> entities) {
        noNullElements(entities);
        entities.forEach(e -> this.entities.add(new EmbeddedRepresentation(wrap(e))));
        return this;
    }

    /**
     * Adds the given {@literal entity} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entity} will be wrapped into an appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent is defined through the given {@literal rel}.
     *
     * @param rel must not be {@literal null}.
     * @param entity must not be {@literal null}.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull String rel, @NonNull Object entity) {
        return entities(LinkRelation.of(rel), entity);
    }

    /**
     * Adds the given {@literal entities} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entities} will be wrapped into appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent is defined through the given {@literal rel}.
     *
     * @param rel must not be {@literal null}.
     * @param entities must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull String rel, @NonNull Object... entities) {
        return entities(LinkRelation.of(rel), entities);
    }

    /**
     * Adds the given {@literal entities} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entities} will be wrapped into appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent is defined through the given {@literal rel}.
     *
     * @param rel must not be {@literal null}.
     * @param entities must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull String rel, @NonNull Iterable<?> entities) {
        return entities(LinkRelation.of(rel), entities);
    }

    /**
     * Adds the given {@literal entity} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entities} will be wrapped into appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent is defined through the given {@literal rel}.
     *
     * @param rel must not be {@literal null}.
     * @param entity must not be {@literal null}.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull LinkRelation rel, @NonNull Object entity) {
        return entities(rel, newArrayList(entity));
    }

    /**
     * Adds the given {@literal entities} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entities} will be wrapped into appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent is defined through the given {@literal rel}.
     *
     * @param rel must not be {@literal null}.
     * @param entities must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull LinkRelation rel, @NonNull Object... entities) {
        return entities(rel, newArrayList(entities));
    }

    /**
     * Adds the given {@literal entities} to the {@link RepresentationModel} to be built.
     * <p>
     * The {@literal entities} will be wrapped into appropriate {@link RepresentationModel}s if necessary. The relation to its
     * parent is defined through the given {@literal rel}.
     *
     * @param rel must not be {@literal null}.
     * @param entities must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#entities-1" target="_blank">Siren entity entities</a>
     * @see <a href="https://github.com/kevinswiber/siren#embedded-representation" target="_blank">Siren embedded representation</a>
     */
    public SirenModelBuilder entities(@NonNull LinkRelation rel, @NonNull Iterable<?> entities) {
        noNullElements(entities);
        entities.forEach(e -> this.entities.add(new EmbeddedRepresentation(wrap(e), rel)));
        return this;
    }

    /**
     * Adds the given {@literal link} to the {@link RepresentationModel} to be built.
     * 
     * @param link must not be {@literal null}.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#links-1" target="_blank">Siren entity links</a>
     * @see <a href="https://github.com/kevinswiber/siren#actions-1" target="_blank">Siren entity actions</a>
     */
    public SirenModelBuilder linksAndActions(@NonNull Link link) {
        return linksAndActions(of(link));
    }

    /**
     * Adds the given {@literal links} to the {@link RepresentationModel} to be built.
     * 
     * @param links must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#links-1" target="_blank">Siren entity links</a>
     * @see <a href="https://github.com/kevinswiber/siren#actions-1" target="_blank">Siren entity actions</a>
     */
    public SirenModelBuilder linksAndActions(@NonNull Link... links) {
        return linksAndActions(of(links));
    }

    /**
     * Adds the given {@literal links} to the {@link RepresentationModel} to be built.
     * 
     * @param links must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#links-1" target="_blank">Siren entity links</a>
     * @see <a href="https://github.com/kevinswiber/siren#actions-1" target="_blank">Siren entity actions</a>
     */
    public SirenModelBuilder linksAndActions(@NonNull Iterable<Link> links) {
        return linksAndActions(of(links));
    }

    /**
     * Adds the given {@literal links} to the {@link RepresentationModel} to be built.
     * 
     * @param links must not be {@literal null} and must not contain {@literal null} values.
     * @return the current {@link SirenModelBuilder} instance.
     * @see <a href="https://github.com/kevinswiber/siren#links-1" target="_blank">Siren entity links</a>
     * @see <a href="https://github.com/kevinswiber/siren#actions-1" target="_blank">Siren entity actions</a>
     */
    public SirenModelBuilder linksAndActions(@NonNull Links links) {
        noNullElements(links);

        linksAndActions = linksAndActions.merge(REPLACE_BY_REL, links);
        return this;
    }

    /**
     * Builds a {@link RepresentationModel} based on the content hold in this {@link SirenModelBuilder} instance.
     *
     * @return will never be {@literal null}.
     */
    public RepresentationModel<?> build() {
        SirenModel model = new SirenModel(properties, entities, classes, title);
        model.add(linksAndActions);
        return model;
    }

    private static void assertNotOfTypeRepresentationModel(Class<?> type) {
        if (isRepresentationModel(type)) {
            throw new IllegalArgumentException(format("The validated object should not be of type '%s' [but is of type '%s']!",
                RepresentationModel.class.getName(), type.getName()));
        }
    }
}
