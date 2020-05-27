package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.springframework.util.Assert.isAssignable;

import com.fasterxml.jackson.databind.JavaType;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import lombok.NonNull;

public interface EntityModelFactory {

    default EntityModel<?> create(@NonNull JavaType type, @NonNull Iterable<Link> links, @NonNull Object content) {
        Class<?> modelType = type.getRawClass();
        isAssignable(EntityModel.class, modelType);

        // TODO check if type of object matches contained type?

        return (EntityModel<?>) instantiate(modelType, new Class[] { Object.class, Iterable.class },
            new Object[] { content, links });
    }

}
