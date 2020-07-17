# Spring HATEOAS Siren
[![Actions Status](https://github.com/ingogriebsch/spring-hateoas-siren/workflows/verify/badge.svg?branch=master)](https://github.com/ingogriebsch/spring-hateoas-siren/actions)
[![Codecov Status](https://codecov.io/gh/ingogriebsch/spring-hateoas-siren/branch/master/graph/badge.svg)](https://codecov.io/gh/ingogriebsch/spring-hateoas-siren)
[![Codacy Status](https://api.codacy.com/project/badge/Grade/72bf0bf6f85f4e3ba1841cc7e4d0a8d8)](https://app.codacy.com/app/ingo.griebsch/spring-hateoas-siren?utm_source=github.com&utm_medium=referral&utm_content=ingogriebsch/spring-hateoas-siren&utm_campaign=Badge_Grade_Dashboard)
[![DepShield Status](https://depshield.sonatype.org/badges/ingogriebsch/spring-hateoas-siren/depshield.svg)](https://depshield.github.io)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This module extends [Spring HATEOAS][] with the custom hypermedia type [Siren][]. 
> _Siren: a hypermedia specification for representing entities_

The media type for [Siren][] is defined as `application/vnd.siren+json`.

## Table of Contents
1. [Configuration](#configuration)
1. [Behavior](#behavior)
1. [Internationalization](#internationalization)
1. [Restrictions](#restrictions)
1. [Customization](#customization)
1. [Client-side Support](#client-side-support)

## Configuration
To enable the [Siren][] hypermedia type you simply need to add this module as a dependency to your project.

### Maven
```
<dependency>
    <groupId>com.github.ingogriebsch</groupId>
    <artifactId>spring-hateoas-siren</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

### Gradle
```
dependencies {
    compile "com.github.ingogriebsch:spring-hateoas-siren:1.0.0"
}
```

Having this module on the classpath of your project is all you need to do to get the hypermedia type automatically enabled. This way incoming requests asking for the mentioned media type will get an appropriate response. The module is also able to deserialize a Json representation of the media type into the corresponding [representation models][Spring HATEOAS Representation Model].

## Behavior
Using this module will make your application respond to requests that have an `Accept` header of `application/vnd.siren+json`. In general, each [representation model][Spring HATEOAS Representation Model] provided through a `@RestController` method is rendered into a Siren [entity][Siren Entity]. Depending on the respective type of the [representation model][Spring HATEOAS Representation Model] the following rules apply:

### RepresentationModel
If this module serializes a `RepresentationModel`, it maps
* any custom properties of the [representation model][Spring HATEOAS Representation Model] (because it is subclassed) to Siren [properties][Siren Entity Properties].
* the type of the [representation model][Spring HATEOAS Representation Model] to the Siren Entity [title][Siren Entity Title] if the type is mapped through the [Internationalization](#internationalization) mechanism.
* the [links][Spring HATEOAS Links] of the [representation model][Spring HATEOAS Representation Model] to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand they are rendered).

#### Example: Serialize a subclassed representation model having some links
_A sample representation model type_
```
class PersonModel extends RepresentationModel<PersonModel> {
  String firstname, lastname;
}
```

_Use the person representation model_
```
PersonModel model = new PersonModel();
model.firstname = "Dave";
model.lastname = "Matthews";
// add some links (having affordances) to the model...
```

_The Siren representation generated for the representation model_
```
{
  "class": [
    ...
  ],
  "properties": {
    "firstname": "Dave",
    "lastname": "Matthews"
  },
  "links": [
    ...
  ],
  "actions": [
    ...
  ]
}
```

### EntityModel
If this module renders an `EntityModel`, it maps
* the value of the content property of the [entity model][Spring HATEOAS Representation Model] to Siren [properties][Siren Entity Properties] if the value is an instance of a simple pojo.
* the value of the content property of the [entity model][Spring HATEOAS Representation Model] to Siren [entities][Siren Entities] if the value is an instance of one of the available [representation models][Spring HATEOAS Representation Model].
* any custom properties of the [entity model][Spring HATEOAS Representation Model] (because it is subclassed) to Siren [properties][Siren Entity Properties]. If the value of the content property is an instance of a simple pojo the above rule takes precedence (i.e. the custom properties will not be serialized).
* the type of the content property of the [entity model][Spring HATEOAS Representation Model] to the Siren Entity [title][Siren Entity Title] if the value is an instance of a simple pojo and the type is mapped through the [Internationalization](#internationalization) mechanism.
* the type of the [entity model][Spring HATEOAS Representation Model] to the Siren Entity [title][Siren Entity Title] if the [entity model][Spring HATEOAS Representation Model] is subclassed and the type is mapped through the [Internationalization](#internationalization) mechanism.
* the [links][Spring HATEOAS Links] of the [entity model][Spring HATEOAS Representation Model] to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand they are rendered).

#### Example: Serialize an entity model wrapping a pojo and having some links
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_Use an entity model to wrap the person object_
```
Person person = new Person();
person.firstname = "Dave";
person.lastname = "Matthews";

EntityModel<Person> model = new EntityModel<>(person);
// add some links (having affordances) to the model...
```

_The Siren representation generated for the entity model wrapping the person object_
```
{
  "class": [
    ...
  ],
  "properties": {
    "firstname": "Dave",
    "lastname": "Matthews"
  },
  "links": [
    ...
  ],
  "actions": [
    ...
  ]
}
```

#### Example: Serialize an entity model wrapping an entity model wrapping a pojo and having some links
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_Use an entity model to wrap the person object_
```
Person person = new Person();
person.firstname = "Dave";
person.lastname = "Matthews";

EntityModel<Person> personModel = new EntityModel<>(person);
// add some links (having affordances) to the person model...
```

_Use an entity model to wrap the entity model_
```
EntityModel<EntityModel<Person>> model = new EntityModel<>(personModel);
// add some links (having affordances) to the model...
```

_The Siren representation generated for the entity model wrapping the entity model wrapping the person object_
```
{
  "class": [
    ...
  ],
  "entities": [
    "class": [
      ...
    ],
    "rel": [
      ...
    ],
    "properties": {
      "firstname": "Dave",
      "lastname": "Matthews"
    }
  ],
  "links": [
    ...
  ],
  "actions": [
    ...
  ]
}
```

### CollectionModel
If this module renders a `CollectionModel`, it maps
* the value of the content property of the [collection model][Spring HATEOAS Representation Model] to Siren [entities][Siren Entities] regardless if it represents instances of one of the available [representation models][Spring HATEOAS Representation Model] or simple pojos.
* any custom properties of the [collection model][Spring HATEOAS Representation Model] (because it is subclassed) to Siren [properties][Siren Entity Properties].
* the type of the [collection model][Spring HATEOAS Representation Model] to the Siren Entity [title][Siren Entity Title] if the type is mapped through the [Internationalization](#internationalization) mechanism.
* the [links][Spring HATEOAS Links] of the [collection model][Spring HATEOAS Representation Model] to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand they are rendered).

#### Example: Serialize a collection model wrapping an entity model and having some links
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_Use an entity model to wrap the person object_
```
Person person = new Person();
person.firstname = "Dave";
person.lastname = "Matthews";

EntityModel<Person> personModel = new EntityModel<>(person);
// add some links (having affordances) to the model...
```

_Use a collection model to wrap the entity model_
```
Collection<EntityModel<Person>> people = Collections.singleton(personModel);
CollectionModel<EntityModel<Person>> people = new CollectionModel<>(people);
// add some links (having affordances) to the model...
```

_The Siren representation generated for the collection model wrapping the entity model wrapping the person object_
```
{
  "class": [
    ...
  ],
  "entities": [{
    "class": [
      ...
    ],
    "properties": {
      "firstname": "Dave",
      "lastname": "Matthews"
    },
    "links": [
      ...
    ],
    "actions": [
      ...
    ]
  }],
  "links": [
    ...
  ],
  "actions": [
    ...
  ]
}
```

### PagedModel
If this module renders a `PagedModel`, it maps
* the value of the content property of the [paged model][Spring HATEOAS Representation Model] to Siren [entities][Siren Entities] regardless if it represents instances of one of the available [representation models][Spring HATEOAS Representation Model] or simple pojos.
* the page metadata of the [paged model][Spring HATEOAS Representation Model] to Siren [properties][Siren Entity Properties].
* the type of the [paged model][Spring HATEOAS Representation Model] to the Siren Entity [title][Siren Entity Title] if the type is mapped through the [Internationalization](#internationalization) mechanism.
* the [links][Spring HATEOAS Links] of the [paged model][Spring HATEOAS Representation Model] to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand they are rendered).

If this module renders a `PagedModel`, it does not 
* map custom properties of the paged model (because it is subclassed). The page metadata always takes precedence.

#### Example: Serialize a paged model wrapping an entity model wrapping a pojo and having some links
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_Use an entity model to wrap the person object_
```
Person person = new Person();
person.firstname = "Dave";
person.lastname = "Matthews";

EntityModel<Person> personModel = new EntityModel<>(person);
// add some links (having affordances) to the model...
```

_Use a paged model to wrap the entity model_
```
Collection<EntityModel<Person>> people = Collections.singleton(personModel);
PageMetadata metadata = new PageMetadata(20, 0, 1, 1);
PagedModel<EntityModel<Person>> model = new PagedModel<>(people, metadata);
// add some links (having affordances) to the model...
```

_The Siren representation generated for the paged model wrapping the entity model wrapping the person object_
```
{
  "class": [
    ...
  ],
  "properties": {
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  },
  "entities": [{
    "class": [
      ...
    ],
    "properties": {
      "firstname": "Dave",
      "lastname": "Matthews"
    },
    "links": [
      ...
    ],
    "actions": [
      ...
    ]
  }],
  "links": [
    ...
  ],
  "actions": [
    ...
  ]
}
```

### Link
If this module renders a `Link`, it maps
* [links][Spring HATEOAS Links] having a http method equal to `GET` to Siren [links][Siren Entity Link].
* the rel of the [link][Spring HATEOAS Links] to the Siren Link [title][Siren Entity Link Title] if available through the [Internationalization](#internationalization) mechanism.
* [affordances][Spring HATEOAS Affordances] bound to a [link][Spring HATEOAS Links] to Siren [actions][Siren Entity Action].
* the name of an [affordance][Spring HATEOAS Affordances] bound to a [link][Spring HATEOAS Links] to the Siren Action [title][Siren Entity Action Title] if available through the [Internationalization](#internationalization) mechanism.
* the name of an input property which is part of an [affordance][Spring HATEOAS Affordances] bound to a [link][Spring HATEOAS Links] to the Siren Action Field [title][Siren Entity Action Field Title] if available through the [Internationalization](#internationalization) mechanism.

If this module renders a `Link`, it does not
* map any [links][Spring HATEOAS Links] having a http method not equal to `GET`.
* distinguish between templated and not templated [links][Spring HATEOAS Links].

#### Example: Serialize a link having affordances
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_A sample person controller_
```
@RestController
class PersonController {

  @GetMapping("/persons/{id}")
  ResponseEntity<EntityModel<Person>> findOne(Long id) { ... }

  @PutMapping("/persons/{id}")
  ResponseEntity<EntityModel<Person>> update(Long id, Person person) { ... }

  @DeleteMapping("/persons/{id}")
  ResponseEntity<Void> delete(Long id) { ... }
}
```

_A self link having affordances created based on the available person controller methods_
```
@GetMapping("/persons/{id}")
ResponseEntity<EntityModel<Person>> findOne(Long id) {
  Person person = personService.findOne(id);

  Link selfLink = linkTo(methodOn(controllerClass).findOne(id)).withSelfRel() //
    .andAffordance(afford(methodOn(controllerClass).update(id, null))) // 
    .andAffordance(afford(methodOn(controllerClass).delete(id)));
  
  EntityModel<Person> model = new EntityModel<>(person, selfLink);
  return ResponseEntity.ok(model);
} 
```

_The Siren representation generated for the link and it's affordances_
```
{
  ...
  "links": [{
    "rel": [
      "self"
    ],
    "href": "http://localhost:8080/persons/1"
  }],
  "actions": [{
    "name": "update",
    "method": "PUT",
    "href": "http://localhost:8080/persons/1",
    "fields": [{
      "name": "firstname",
      "type": "text"
    },{
      "name": "lastname",
      "type": "text"
    }]
  },{
    "name": "delete",
    "method": "DELETE",
    "href": "http://localhost:8080/persons/1"
  }]
}
```

### Conclusion
[Siren][] defines a resource as an [entity][Siren Entity] which has not only [properties][Siren Entity Properties] and navigational [links][Siren Entity Link] but may also contain [embedded representations][Siren Entity Embedded Representation]. Because such representations retain all the characteristics of an [entity][Siren Entity] you can build quite complex resource structures.
<br/><br/>
To allow to build such structures this module is able to handle subclassed [representation models][Spring HATEOAS Representation Model] of type `EntityModel` and `CollectionModel`. It is in the nature of [representation models][Spring HATEOAS Representation Model] of type `RepresentationModel` to be subclassed. It makes no sense to subclass [Representation models][Spring HATEOAS Representation Model] of type `PagedModel` because they already contain specific properties explaining the nature of this type of resource.
<br/><br/>
The following example explains what is possible to do (we will skip parts of the Siren representation like [class][Siren Entity Class], [links][Siren Entity Link] or [actions][Siren Entity Action] and concentrate on the [properties][Siren Entity Properties] and [embedded representations][Siren Entity Embedded Representation]):

_A sample representation model_
```
class Capital extends RepresentationModel<Capital> {
  String name;
}
```

_A sample entity model_
```
class State extends EntityModel<Capital> {
  String name;
}
```

_A sample collection model_
```
class Country extends CollectionModel<State> {
  String name;
}
```

_Use the different types of representation models_
```
State bali = new State("Bali", new Capital("Denpasar"));
State maluku = new State("Maluku", new Capital("Ambon"));
State riau = new State("Riau", new Capital("Pekanbaru"));

Country indonesia = new Country("Indonesia", List.of(bali, maluka, riau));
```

_The generated Siren representation_
```
{
  "properties": {
    "name": "Indonesia"
  },
  "entities": [{
    "properties": {
      "name": "Bali"
    },
    "entities": [{
      "properties": {
        "name": "Denpasar"
      },
    }]
  }, {
    "properties": {
      "name": "Maluku"
    },
    "entities": [{
      "properties": {
        "name": "Ambon"
      },
    }]
  }, {
    "properties": {
      "name": "Riau"
    },
    "entities": [{
      "properties": {
        "name": "Pekanbaru"
      },
    }]
  }]
}
```

And this is still a relatively simple example of what is possible if using some subclassed [representation models][Spring HATEOAS Representation Model] together. Especially mixing [entity models][Spring HATEOAS Representation Model] with [collection models][Spring HATEOAS Representation Model] and vice versa allows to build quite complex structures. 

## Internationalization
[Siren][] defines a `title` attribute for its [entity][Siren Entity], [link][Siren Entity Link] and [action][Siren Entity Action] (including their fields) objects. These titles can be populated by using Spring’s resource bundle abstraction and a resource bundle named `rest-messages` so that clients can use them in their UIs directly. This bundle will be set up automatically and is used during the serialization process.

### Entity
To define a [title][Siren Entity Title] for an [entity][Siren Entity], use the key template `_entity.$type.title`. Which type is used to build the resulting key depends on which type of [representatio model][Spring HATEOAS Representation Model] is used. To evaluate if a title is available for the specific type, the `fqcn` will be checked first, followed by the `simple name`. Finally, it is checked whether type `default` is available.

### Link
To define a [title][Siren Entity Link Title] for a [link][Siren Entity Link], use the key template `_link.$rel.title`. To evaluate if a [title][Siren Entity Link Title] is available for the [link][Spring HATEOAS Links], the `rel` of the [link][Spring HATEOAS Links] will be checked first. Finally, it is checked whether type `default` is available.

### Action
To define a [title][Siren Entity Action Title] for an [action][Siren Entity Action], use the key template `_action.$name.title`. To evaluate if a [title][Siren Entity Action Title] is available for the [action][Siren Entity Action], the `name` of the [affordance][Spring HATEOAS Affordances] will be checked first. Finally, it is checked whether type `default` is available.
<br/><br/>
To define a [title][Siren Entity Action Field Title] for an [action field][Siren Entity Action], use the key template `_field.$name.title`. To evaluate if a [title][Siren Entity Action Field Title] is available for the [action field][Siren Entity Action], the `name` of the input property which is part of the [affordance][Spring HATEOAS Affordances] will be checked first. Finally, it is checked whether type `default` is available.

## Restrictions
Siren [embedded links][Siren Entity Embedded Link] are currently not implemented through the module itself. If you want them, you need to implement a pojo representing an embedded link and add it as content of either a `CollectionModel` or `PagedModel` instance.
<br/><br/>
Siren [embedded representations][Siren Entity Embedded Representation] are currently only supported if defined as the content of either a `CollectionModel` or a `PagedModel` instance. It is currently not possible to build a hierarchy based on instances of either `RepresentationModel` or `EntityModel`.

## Customization
This module currently uses a really simple approach to map the respective [representation model][Spring HATEOAS Representation Model] to the [class][Siren Entity Class] attribute of the Siren [entity][Siren Entity]. If you want to override/enhance this behavior you need to expose an implementation of the `SirenEntityClassProvider` interface as a Spring bean.
<br/><br/>
This module currently uses a really simple approach to evaluate the relation between a [representation model][Spring HATEOAS Representation Model] and it's contained [representation model][Spring HATEOAS Representation Model] to set the [rel][Siren Entity Rel] attribute of the Siren [entity][Siren Entity]. If you want to override/enhance this behavior you need to expose an implementation of the `SirenEntityRelProvider` interface as a Spring bean.
<br/><br/>
This module currently uses a really simple approach to instantiate the concrete instances of the [representation models][Spring HATEOAS Representation Model] during the deserialization process. If you want to override/enhance this behavior you need to expose an implementation of the `RepresentationModelFactories` interface as a Spring bean.

## Client-side Support

### Deserialization
This module also allows to use/handle the [Siren][] hypermedia type on clients requesting data from servers producing this hypermedia type. Means that adding and enabling this module is sufficient to be able to deserialize responses containing data of the [Siren][] hypermedia type into their respective [representation model][Spring HATEOAS Representation Model] representations.

### Traverson
The hypermedia type `application/vnd.siren+json` is currently not usable with the `Traverson` implementation provided through [Spring HATEOAS][].

### Using LinkDiscoverer Instances
When working with hypermedia enabled representations, a common task is to find a link with a particular relation type in it. [Spring HATEOAS][] provides [JSONPath][]-based implementations of the `LinkDiscoverer` interface for the configured hypermedia types. When using this module, an instance supporting this hypermedia type is exposed as a Spring bean.
<br/><br/>
Alternatively, you can setup and use an instance as follows:
```
String content = "{'_links' :  { 'foo' : { 'href' : '/foo/bar' }}}";

LinkDiscoverer discoverer = new SirenLinkDiscoverer();
Link link = discoverer.findLinkWithRel("foo", content);

assertThat(link.getRel(), is("foo"));
assertThat(link.getHref(), is("/foo/bar"));
```

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).

[Spring HATEOAS]: https://docs.spring.io/spring-hateoas/docs/1.1.0.RELEASE/reference/html/
[Spring HATEOAS Representation Model]: https://docs.spring.io/spring-hateoas/docs/1.1.0.RELEASE/reference/html/#fundamentals.representation-models
[Spring HATEOAS Links]: https://docs.spring.io/spring-hateoas/docs/1.1.0.RELEASE/reference/html/#fundamentals.links
[Spring HATEOAS Affordances]: https://docs.spring.io/spring-hateoas/docs/1.1.0.RELEASE/reference/html/#server.affordances
[Siren]: https://github.com/kevinswiber/siren
[Siren Entity]: https://github.com/kevinswiber/siren/blob/master/README.md#entity
[Siren Entities]: https://github.com/kevinswiber/siren/blob/master/README.md#entities-1
[Siren Entity Embedded Link]: https://github.com/kevinswiber/siren/blob/master/README.md#embedded-link
[Siren Entity Embedded Representation]: https://github.com/kevinswiber/siren/blob/master/README.md#embedded-representation
[Siren Entity Class]: https://github.com/kevinswiber/siren/blob/master/README.md#class
[Siren Entity Rel]: https://github.com/kevinswiber/siren/blob/master/README.md#rel
[Siren Entity Properties]: https://github.com/kevinswiber/siren/blob/master/README.md#properties
[Siren Entity Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title
[Siren Entity Link]: https://github.com/kevinswiber/siren/blob/master/README.md#links-1
[Siren Entity Link Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title-2
[Siren Entity Action]: https://github.com/kevinswiber/siren/blob/master/README.md#actions-1
[Siren Entity Action Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title-3
[Siren Entity Action Field Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title-4
[JSONPath]: https://github.com/json-path/JsonPath
