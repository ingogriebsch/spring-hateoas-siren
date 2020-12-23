# Spring HATEOAS Siren
[![Actions Status](https://github.com/ingogriebsch/spring-hateoas-siren/workflows/build/badge.svg?branch=master)](https://github.com/ingogriebsch/spring-hateoas-siren/actions)
[![Codecov Status](https://codecov.io/gh/ingogriebsch/spring-hateoas-siren/branch/master/graph/badge.svg)](https://codecov.io/gh/ingogriebsch/spring-hateoas-siren)
[![Codacy Status](https://api.codacy.com/project/badge/Grade/72bf0bf6f85f4e3ba1841cc7e4d0a8d8)](https://app.codacy.com/app/ingo.griebsch/spring-hateoas-siren?utm_source=github.com&utm_medium=referral&utm_content=ingogriebsch/spring-hateoas-siren&utm_campaign=Badge_Grade_Dashboard)
[![DepShield Status](https://depshield.sonatype.org/badges/ingogriebsch/spring-hateoas-siren/depshield.svg)](https://depshield.github.io)
[![Maven-Central](https://img.shields.io/maven-central/v/de.ingogriebsch.hateoas/spring-hateoas-siren?color=green)](https://search.maven.org/artifact/de.ingogriebsch.hateoas/spring-hateoas-siren)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This library extends [Spring HATEOAS][spring-hateoas] with the custom hypermedia type [Siren][siren]. 
> _Siren: a hypermedia specification for representing entities_

The media type for [Siren][siren] is defined as `application/vnd.siren+json`.

## Documentation
Here you find the documentation for the latest releases and the current SNAPSHOT version:

| :---: | :---: | :---: |
| SNAPSHOT | [Reference Doc.][spring-hateoas-siren-current-docs] | [API Doc.][spring-hateoas-siren-current-apidocs] |
| 1.0.0 | [Reference Doc.][spring-hateoas-siren-1.0.0-docs] | [API Doc.][spring-hateoas-siren-1.0.0-apidocs] |

If you are interested in the Reference/API documentation of a specific version which is not listed here, you simply need to apply the following rules.

The url to the Reference documentation has the following pattern:

> `<scheme>://<host>/spring-hateoas-siren/<version>/index.html`

The value `current` is used for the path segment `<version>` if the url points to the Reference documentation of the current SNAPSHOT version.
The Reference documentation of a specific version can be addressed if the path segment `<version>` of the url is replaced with a specifc release version.
For example, the Reference documentation of version 1.0.0 can be addressed if the path segment `<version>` is replaced with `1.0.0`.

The url to the API documentation has the following pattern:

> `<scheme>://<host>/spring-hateoas-siren/<version>/apidocs/index.html`

The value `current` is used for the path segment `<version>` if the url points to the Reference documentation of the current SNAPSHOT version.
The Reference documentation of a specific version can be addressed if the path segment `<version>` of the url is replaced with a specifc release version.
For example, the Reference documentation of version 1.0.0 can be addressed if the path segment `<version>` is replaced with `1.0.0`.

## Integration
Please check section [Setup][spring-hateoas-siren-current-docs-setup] of the documentation to understand how to integrate this library into your project. 
The latest RELEASE version is available through [Maven Central][maven-central]. 
The latest SNPASHOT version is available through the [Sonatype Snapshots Repository][sonatype-snapshots-repository].

## Version Matrix
If you want to add the library to your app, the first step is to determine the version of the library you should use.
The table below outlines which version of the library maps to which version of [Spring HATEOAS][spring-hateoas], [Spring Boot][spring-boot] and the [Spring Framework][spring-framework].

| Spring HATEOAS Siren | Spring HATEOAS | Spring Boot | Spring Framework
| :---: | :---: | :---: | :---: |
| 1.1.x | 1.2.x | 2.4.x | 5.3.x |
| 1.0.x | 1.1.x | 2.3.x | 5.2.x |

## Examples
A collection of examples that showcase how to use the features provided by the library can be found [here][spring-hateoas-siren-samples].

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).

[spring-hateoas-siren-current-docs]: https://ingogriebsch.github.io/spring-hateoas-siren/current/index.html
[spring-hateoas-siren-current-apidocs]: https://ingogriebsch.github.io/spring-hateoas-siren/current/apidocs/index.html
[spring-hateoas-siren-1.0.0-docs]: https://ingogriebsch.github.io/spring-hateoas-siren/1.0.0/index.html
[spring-hateoas-siren-1.0.0-apidocs]: https://ingogriebsch.github.io/spring-hateoas-siren/1.0.0/apidocs/index.html
[spring-hateoas-siren-current-docs-setup]: https://ingogriebsch.github.io/spring-hateoas-siren/current/index.html#setup
[spring-hateoas-siren-samples]: https://github.com/ingogriebsch/spring-hateoas-siren-samples/
[spring-hateoas]: https://spring.io/projects/spring-hateoas
[spring-boot]: https://spring.io/projects/spring-boot
[spring-framework]: https://spring.io/projects/spring-framework
[siren]: https://github.com/kevinswiber/siren
[maven-central]: https://search.maven.org/artifact/de.ingogriebsch.hateoas/spring-hateoas-siren
[sonatype-snapshots-repository]: https://oss.sonatype.org/content/repositories/snapshots/de/ingogriebsch/hateoas/spring-hateoas-siren/
