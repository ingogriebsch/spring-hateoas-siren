package com.github.ingogriebsch.spring.hateoas.siren;

public interface RepresentationModelFactories {

    default RepresentationModelFactory forRepresentationModel() {
        return new RepresentationModelFactory() {
        };
    }

    default EntityModelFactory forEntityModel() {
        return new EntityModelFactory() {
        };
    }

    default CollectionModelFactory forCollectioModel() {
        return new CollectionModelFactory() {
        };
    }

    default PagedModelFactory forPagedModel() {
        return new PagedModelFactory() {
        };
    }

}
