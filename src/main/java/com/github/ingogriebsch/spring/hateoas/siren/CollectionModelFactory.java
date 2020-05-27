package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.applyProperties;
import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.springframework.util.Assert.isAssignable;

import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import lombok.NonNull;

public interface CollectionModelFactory {

    default CollectionModel<?> create(@NonNull JavaType type, @NonNull Iterable<Link> links, @NonNull Iterable<Object> content,
        Map<String, Object> properties) {
        Class<?> modelType = type.getRawClass();
        isAssignable(CollectionModel.class, modelType);

        CollectionModel<?> model = (CollectionModel<?>) instantiate(modelType, new Class[] { Iterable.class, Iterable.class },
            new Object[] { content, links });

        if (properties != null) {
            applyProperties(model, properties);
        }
        return model;
    }
}
