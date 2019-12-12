# Spring HATEOAS Siren
[![Actions Status](https://github.com/ingogriebsch/spring-hateoas-siren/workflows/verify/badge.svg)](https://github.com/ingogriebsch/spring-hateoas-siren/actions)
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

Having this module on the classpath of your project is all you need to do to get the hypermedia type automatically enabled. This way incoming requests asking for the mentioned media type will get an appropriate response.

## Behavior
Using this module will make your application respond to requests that have an `Accept` header of `application/vnd.siren+json` in the following way:

In general each [representation model][Spring HATEOAS Representation Model] is rendered into a Siren [entity][Siren Entity]. Depending on the respective type of the [representation model][Spring HATEOAS Representation Model] the following rules apply:

### RepresentationModel
If this module renders a `RepresentationModel`, it will
* map any custom properties of the model (if it has some because it is subclassed) to Siren [properties][Siren Entity Properties].
* map the type of the model to the Siren Entity [title][Siren Entity Title] if available through the [Internationalization](#internationalization) mechanism.
* map links of the model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

#### Example: Serialize a subclassed RepresentationModel having some links
_A sample representation model type_
```
class PersonModel extends RepresentationModel<PersonModel> {
  String firstname, lastname;
}
```

_Using the person representation model_
```
PersonModel model = new PersonModel();
model.firstname = "Dave";
model.lastname = "Matthews";
// add some links (having affordances) to the model...
```

_The Siren representation generated for the person representation model_
```
{
  "class": [
    "representation"
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
If this module renders an `EntityModel`, it will
* map the value of the content property of the model to Siren [properties][Siren Entity Properties].
* map the type of the content property of the model to the Siren Entity [title][Siren Entity Title] if available through the [Internationalization](#internationalization) mechanism.
* map links of the model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

If this module renders an `EntityModel`, it will not
* map the value of the content property of the model if the value is an instance of one of the available [representation models][Spring HATEOAS Representation Model]!
* map custom properties of the model (if it has some because it is subclassed).

#### Example: Serialize an EntityModel wrapping a pojo and having some links
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_Using an entity model to wrap the person object_
```
Person person = new Person();
person.firstname = "Dave";
person.lastname = "Matthews";

EntityModel<Person> model = new EntityModel<>(person);
// add some links (having affordances) to the model...
```

_The Siren representation generated for the entity model wrapped person_
```
{
  "class": [
    "entity"
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

### CollectionModel
If this module renders a `CollectionModel`, it will
* map the size of the content property of the model to Siren [properties][Siren Entity Properties].
* map the value of the content property of the model to Siren [entities][Siren Entities] (regardless if it represents instances of one of the available [representation models][Spring HATEOAS Representation Model] or simple pojos).
* map the type of the model to the Siren Entity [title][Siren Entity Title] if available through the [Internationalization](#internationalization) mechanism.
* map links of the model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

If this module renders a `CollectionModel`, it will not
* map custom properties of the collection model (if it has some because it is subclassed).

#### Example: Serialize a CollectionModel wrapping a pojo and having some links
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_Using a collection model to wrap a collection of person objects_
```
Person person = new Person();
person.firstname = "Dave";
person.lastname = "Matthews";

Collection<Person> people = Collections.singleton(person);
CollectionModel<Person> model = new CollectionModel<>(people);
// add some links (having affordances) to the model...
```

_The Siren representation generated for the collection model wrapped persons_
```
{
  "class": [
    "collection"
  ],
  "properties": {
    "size": 1
  },
  "entities": [{
    "class": [
      "entity"
    ],
    "properties": {
      "firstname": "Dave",
      "lastname": "Matthews"
    }
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
If this module renders a `PagedModel`, it will
* map the page metadata of the model to Siren [properties][Siren Entity Properties].
* map the value of the content property of the model to Siren [entities][Siren Entities] (regardless if it represents instances of one of the available [representation models][Spring HATEOAS Representation Model] or simple pojos).
* map the type of the model to the Siren Entity [title][Siren Entity Title] if available through the [Internationalization](#internationalization) mechanism.
* map links of the model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

If this module renders a `PagedModel`, it will not
* map custom properties of the paged model (if it has some because it is subclassed).

#### Example: Serialize a PagedModel wrapping a pojo and having some links
_A sample person object_
```
class Person {
  String firstname, lastname;
}
```

_Using a paged model to wrap a page of person objects_
```
Person person = new Person();
person.firstname = "Dave";
person.lastname = "Matthews";

Collection<Person> people = Collections.singleton(person);
PageMetadata metadata = new PageMetadata(20, 0, 1, 1);
PagedModel<Person> model = new PagedModel<>(people, metadata);
// add some links (having affordances) to the model...
```

_The Siren representation generated for the paged model wrapped persons_
```
{
  "class": [
    "paged"
  ],
  "properties": {
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "number": 0
  },
  "entities": [{
    "class": [
      "entity"
    ],
    "properties": {
      "firstname": "Dave",
      "lastname": "Matthews"
    }
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
If this module renders a `Link`, it will
* map links having a http method equal to `GET` to Siren [links][Siren Entity Link].
* map the rel of the link to the Siren Link [title][Siren Link Title] if available through the [Internationalization](#internationalization) mechanism.
* map affordances corresponding to a link to Siren [actions][Siren Entity Action].
* map the name of an affordance bound to the link to the Siren Action [title][Siren Action Title] if available through the [Internationalization](#internationalization) mechanism.
* map the name of an input property which is part of an affordance bound to the link to the Siren Action Field [title][Siren Action Field Title] if available through the [Internationalization](#internationalization) mechanism.

If this module renders a `Link`, it will not
* map any links having a http method not equal to `GET`.
* distinguish between templated and not templated links.

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

## Internationalization
Siren defines a `title` attribute for its entity, link and action (including their fields) objects. These titles can be populated by using Spring’s resource bundle abstraction and a resource bundle named `rest-messages` so that clients can use them in their UIs directly. This bundle will be set up automatically and is used during Siren serialization.

### Entity
To define a title for an entity, use the key template `_entity.$type.title`. Which type is used to build the resulting key depends on which type of model is used. To evaluate if a title is available for the specific type, the `fqcn` will be checked first, followed by the `simple name`. Finally, it is checked whether type `default` is available.

### Link
To define a title for a link, use the key template `_link.$rel.title`. To evaluate if a title is available for the link, the `rel` of the link will be checked first. Finally, it is checked whether type `default` is available.

### Action
To define a title for an action, use the key template `_action.$name.title`. To evaluate if a title is available for the action, the `name` of the affordance will be checked first. Finally, it is checked whether type `default` is available.
To define a title for an action-field, use the key template `_field.$name.title`. To evaluate if a title is available for the action-field, the `name` of the input property which is part of the affordance will be checked first. Finally, it is checked whether type `default` is available.

## Restrictions
* Siren [embedded links][Siren Entity Embedded Link] are currently not implemented through the module itself. If you want them, you need to implement a pojo representing an embedded link and add it as content of either a `CollectionModel` or `PagedModel` instance.
* Siren [embedded representations][Siren Entity Embedded Representation] are currently only supported if defined as the content of either a `CollectionModel` or a `PagedModel` instance. It is currently not possible to build a hierarchy based on instances of either `RepresentationModel` or `EntityModel`.

## Customization
This module currently uses a really simple approach to map the respective [representation model][Spring HATEOAS Representation Model] to the [class][Siren Entity Class] attribute of the Siren [entity][Siren Entity]. If you want to override/enhance this behavior you need to expose an implementation of the `SirenEntityClassProvider` interface as a Spring bean.

## Client-side Support

### Deserialization
This module also allows to use/handle the [Siren][] hypermedia type on clients requesting data from servers producing this hypermedia type. Means that adding and enabling this module is sufficient to be able to deserialize responses containing data of the [Siren][] hypermedia type.

### Traverson
The hypermedia type `application/vnd.siren+json` is currently not usable with the `Traverson` implementation provided through [Spring HATEOAS][].

### Using LinkDiscoverer Instances
When working with hypermedia enabled representations, a common task is to find a link with a particular relation type in it. [Spring HATEOAS][] provides [JSONPath][]-based implementations of the `LinkDiscoverer` interface for the configured hypermedia types. When using this module, an instance supporting this hypermedia type is exposed as a Spring bean. 
<br/><br/>Alternatively, you can setup and use an instance as follows:

```
String content = "{'_links' :  { 'foo' : { 'href' : '/foo/bar' }}}";

LinkDiscoverer discoverer = new SirenLinkDiscoverer();
Link link = discoverer.findLinkWithRel("foo", content);

assertThat(link.getRel(), is("foo"));
assertThat(link.getHref(), is("/foo/bar"));
```

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).

[Spring HATEOAS]: https://docs.spring.io/spring-hateoas/docs/current/reference/html/
[Spring HATEOAS Representation Model]: https://docs.spring.io/spring-hateoas/docs/current/reference/html/#fundamentals.representation-models
[Siren]: https://github.com/kevinswiber/siren
[Siren Entity]: https://github.com/kevinswiber/siren/blob/master/README.md#entity
[Siren Entities]: https://github.com/kevinswiber/siren/blob/master/README.md#entities-1
[Siren Entity Embedded Link]: https://github.com/kevinswiber/siren/blob/master/README.md#embedded-link
[Siren Entity Embedded Representation]: https://github.com/kevinswiber/siren/blob/master/README.md#embedded-representation
[Siren Entity Class]: https://github.com/kevinswiber/siren/blob/master/README.md#class
[Siren Entity Properties]: https://github.com/kevinswiber/siren/blob/master/README.md#properties
[Siren Entity Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title
[Siren Entity Link]: https://github.com/kevinswiber/siren/blob/master/README.md#links-1
[Siren Entity Link Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title-2
[Siren Entity Action]: https://github.com/kevinswiber/siren/blob/master/README.md#actions-1
[Siren Entity Action Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title-3
[Siren Entity Action Field Title]: https://github.com/kevinswiber/siren/blob/master/README.md#title-4
[JSONPath]: https://github.com/json-path/JsonPath
