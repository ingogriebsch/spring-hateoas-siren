# Spring HATEOAS Siren
This module extends [Spring HATEOAS][] with the custom hypermedia type [Siren][]. 
> _Siren: a hypermedia specification for representing entities_

The media type for [Siren][] is defined as `application/vnd.siren+json`.

## Table of Contents
1. [Configuration](#configuration)
1. [Behavior](#behavior)
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
When this module renders a `RepresentationModel`, it will

* map any custom properties of the representation model (if it has some because it is subclassed) to Siren [properties][Siren Entity Properties].
* map links of the representation model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

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
When this module renders an `EntityModel`, it will

* map the value of the content property of the entity model to Siren [properties][Siren Entity Properties].
* map links of the representation model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

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

When this module renders an `EntityModel`, it will not

* map the value of the content property if the value is an instance of one of the available [representation models][Spring HATEOAS Representation Model]!
* map custom properties of the entity model (if it has some because it is subclassed).

### CollectionModel
When this module renders a `CollectionModel`, it will

* map the size of the content property of the collection model to Siren [properties][Siren Entity Properties].
* map the value of the content property of the collection model to Siren [entities][Siren Entities] (regardless if it represents instances of one of the available [representation models][Spring HATEOAS Representation Model] or simple pojos).
* map links of the collection model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

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

When this module renders a `CollectionModel`, it will not

* map custom properties of the collection model (if it has some because it is subclassed).

### PagedModel
When this module renders a `PagedModel`, it will

* map the page metadata of the paged model to Siren [properties][Siren Entity Properties].
* map the value of the content property of the paged model to Siren [entities][Siren Entities] (regardless if it represents instances of one of the available [representation models][Spring HATEOAS Representation Model] or simple pojos).
* map links of the paged model to Siren [links][Siren Entity Link] and [actions][Siren Entity Action] (see below to understand how links are rendered).

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

When this module renders a `PagedModel`, it will not

* map custom properties of the paged model (if it has some because it is subclassed).

### Link
When this module renders a `Link`, it will

* map links having a http method equal to `GET` to Siren [links][Siren Entity Link].
* map affordances corresponding to a link to Siren [actions][Siren Entity Action].

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

When this module renders a `Link`, it will not

* map any links having a http method not equal to `GET`.
* distinguish between templated and not templated links.

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
[Siren Entity Link]: https://github.com/kevinswiber/siren/blob/master/README.md#links-1
[Siren Entity Action]: https://github.com/kevinswiber/siren/blob/master/README.md#actions-1
[JSONPath]: https://github.com/json-path/JsonPath
