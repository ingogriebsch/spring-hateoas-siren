package com.github.ingogriebsch.spring.hateoas.siren;

import static com.github.ingogriebsch.spring.hateoas.siren.BeanUtils.instantiate;
import static org.springframework.util.Assert.isAssignable;

import java.util.Collection;

import com.fasterxml.jackson.databind.JavaType;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;

import lombok.NonNull;

public interface PagedModelFactory {

    default PagedModel<?> create(@NonNull JavaType type, @NonNull Iterable<Link> links, @NonNull Iterable<Object> content,
        @NonNull PageMetadata metadata) {
        Class<?> modelType = type.getRawClass();
        isAssignable(PagedModel.class, modelType);

        return (PagedModel<?>) instantiate(modelType, new Class[] { Collection.class, PageMetadata.class, Iterable.class },
            new Object[] { content, metadata, links });
    }
}
