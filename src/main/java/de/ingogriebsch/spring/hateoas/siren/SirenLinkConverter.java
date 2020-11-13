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

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import static com.google.common.collect.Lists.newArrayList;
import static de.ingogriebsch.spring.hateoas.siren.MediaTypes.SIREN_JSON;
import static de.ingogriebsch.spring.hateoas.siren.SirenActionFieldType.TEXT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import java.util.List;

import de.ingogriebsch.spring.hateoas.siren.SirenAction.Field;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.http.MediaType;

/**
 * Converter which is able to either convert {@link Link}s (and their {@link Affordance}s) into a {@link SirenNavigables}, or the
 * other way around.
 * 
 * @author Ingo Griebsch
 * @since 1.0.0
 * @see SirenLink
 */
@RequiredArgsConstructor
class SirenLinkConverter {

    private final MessageResolver messageResolver;
    private final SirenActionFieldTypeConverter sirenActionFieldTypeConverter;

    SirenNavigables to(Iterable<Link> links) {
        return SirenNavigables.merge(stream(links.spliterator(), false).map(this::convert).collect(toList()));
    }

    List<Link> from(SirenNavigables navigables) {
        return slice(navigables).stream().map(this::convert).collect(toList());
    }

    SirenNavigables convert(Link link) {
        return SirenNavigables.of(links(link), actions(link));
    }

    private Link convert(SirenNavigables navigables) {
        SirenLink link = navigables.getLinks().iterator().next();
        String rel = link.getRels().stream().findFirst().orElse(null);

        return Link.of(link.getHref(), rel) //
            .withTitle(link.getTitle()) //
            .withType(link.getType());
    }

    private List<SirenLink> links(Link link) {
        SirenLink sirenLink = SirenLink.builder() //
            .rel(link.getRel().value()) //
            .href(link.getHref()) //
            .title(title(link)) //
            .type(link.getType()) //
            .build();

        return newArrayList(sirenLink);
    }

    private List<SirenAction> actions(Link link) {
        List<SirenAction> result = newArrayList();
        for (SirenAffordanceModel model : affordanceModels(link)) {
            if (!GET.equals(model.getHttpMethod())) {
                result.add(action(model));
            }
        }
        return result;
    }

    private SirenAction action(SirenAffordanceModel model) {
        MediaType type = actionType(model);
        List<Field> fields = fields(model, type);

        return SirenAction.builder() //
            .name(model.getName()) //
            .method(model.getHttpMethod()) //
            .href(model.getLink().getHref()) //
            .title(actionTitle(model.getName())) //
            .fields(fields) //
            .build();
    }

    private List<Field> fields(SirenAffordanceModel model, MediaType actionType) {
        InputPayloadMetadata input = model.getInput();
        if (input == null) {
            return newArrayList();
        }
        return input.stream().map(pm -> field(pm, actionType)).collect(toList());
    }

    private Field field(PropertyMetadata propertyMetadata, MediaType actionType) {
        return Field.builder() //
            .name(propertyMetadata.getName()) //
            .type(fieldType(propertyMetadata, actionType))//
            .title(fieldTitle(propertyMetadata.getName())) //
            .build();
    }

    private String title(Link link) {
        String title = link.getTitle();
        if (title != null) {
            return title;
        }

        LinkRelation rel = link.getRel();
        if (rel != null) {
            return title(SirenLink.TitleResolvable.of(link.getRel()));
        }
        return null;
    }

    private String actionTitle(String name) {
        return name != null ? title(SirenAction.TitleResolvable.of(name)) : null;
    }

    private String fieldTitle(String name) {
        return name != null ? title(SirenAction.Field.TitleResolvable.of(name)) : null;
    }

    private String title(MessageSourceResolvable resolvable) {
        return messageResolver.resolve(resolvable);
    }

    private String fieldType(PropertyMetadata propertyMetadata, MediaType actionType) {
        return ofNullable(sirenActionFieldTypeConverter.convert(propertyMetadata, actionType))
            .map(SirenActionFieldType::getKeyword).orElse(TEXT.getKeyword());
    }

    private static MediaType actionType(SirenAffordanceModel model) {
        return APPLICATION_FORM_URLENCODED;
    }

    private static List<SirenNavigables> slice(SirenNavigables navigables) {
        return navigables.getLinks().stream().map(l -> slice(l, navigables.getActions())).collect(toList());
    }

    private static SirenNavigables slice(SirenLink link, List<SirenAction> actions) {
        return SirenNavigables.of(newArrayList(link), actions);
    }

    private static List<SirenAffordanceModel> affordanceModels(Link link) {
        return link.getAffordances().stream().map(a -> a.getAffordanceModel(SIREN_JSON)).map(SirenAffordanceModel.class::cast)
            .collect(toList());
    }
}
