package com.github.ingogriebsch.spring.hateoas.siren;

import static com.google.common.collect.Sets.newHashSet;
import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = PRIVATE)
class RepresentationModelUtils {

    private static final Set<Class<?>> REPRESENTATION_MODEL_TYPES =
        newHashSet(RepresentationModel.class, EntityModel.class, CollectionModel.class, PagedModel.class);

    static boolean isRepresentationModel(@NonNull Class<?> clazz) {
        for (Class<?> resourceType : REPRESENTATION_MODEL_TYPES) {
            if (resourceType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    static boolean isRepresentationModelSubclass(@NonNull Class<?> clazz) {
        return !REPRESENTATION_MODEL_TYPES.contains(clazz) && REPRESENTATION_MODEL_TYPES.contains(clazz.getSuperclass());
    }
}
