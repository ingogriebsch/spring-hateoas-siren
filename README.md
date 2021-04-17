# Spring HATEOAS Siren
[![Actions Status](https://github.com/ingogriebsch/spring-hateoas-siren/workflows/build/badge.svg?branch=master)](https://github.com/ingogriebsch/spring-hateoas-siren/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.ingogriebsch.hateoas%3Aspring-hateoas-siren&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.ingogriebsch.hateoas%3Aspring-hateoas-siren)
[![Maven-Central](https://img.shields.io/maven-central/v/de.ingogriebsch.hateoas/spring-hateoas-siren?color=green)](https://search.maven.org/artifact/de.ingogriebsch.hateoas/spring-hateoas-siren)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This library extends [Spring HATEOAS][spring-hateoas] with the custom hypermedia type [Siren][siren]. 
> _Siren: a hypermedia specification for representing entities_

The media type for [Siren][siren] is defined as `application/vnd.siren+json`.

## Version Matrix
If you want to add the library to your app, the first step is to determine the version of the library you should use.
The table below outlines which version of the library maps to which version of [Spring HATEOAS][spring-hateoas], [Spring Boot][spring-boot] and the [Spring Framework][spring-framework].

| Spring HATEOAS Siren | Spring HATEOAS | Spring Boot | Spring Framework
| :---: | :---: | :---: | :---: |
| 1.1.x | 1.2.x | 2.4.x | 5.3.x |
| 1.0.x | 1.1.x | 2.3.x | 5.2.x |

## Integration
Please check section [Setup][spring-hateoas-siren-current-docs-setup] of the documentation to understand how to integrate this library into your project. 
The RELEASE versions are available through [Maven Central][maven-central]. 
The latest SNPASHOT version is available through the [Sonatype Snapshots Repository][sonatype-snapshots-repository].

## Examples
A collection of examples that showcase how to use the features provided by the library can be found [here][spring-hateoas-siren-samples].

## Documentation
Here you find the documentation for the latest releases and the current SNAPSHOT version:

| Version | Reference documentation | API documentation |
| :---: | :---: | :---: |
| SNAPSHOT | [Reference Doc.][spring-hateoas-siren-current-docs] | [API Doc.][spring-hateoas-siren-current-apidocs] |
| 1.1.0 | [Reference Doc.][spring-hateoas-siren-1.1.0-docs] | [API Doc.][spring-hateoas-siren-1.1.0-apidocs] |
| 1.0.3 | [Reference Doc.][spring-hateoas-siren-1.0.3-docs] | [API Doc.][spring-hateoas-siren-1.0.3-apidocs] |

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

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).

[spring-hateoas-siren-current-docs]: https://ingogriebsch.github.io/spring-hateoas-siren/current/index.html
[spring-hateoas-siren-current-docs-setup]: https://ingogriebsch.github.io/spring-hateoas-siren/current/index.html#setup
[spring-hateoas-siren-current-apidocs]: https://ingogriebsch.github.io/spring-hateoas-siren/current/apidocs/index.html
[spring-hateoas-siren-1.1.0-docs]: https://ingogriebsch.github.io/spring-hateoas-siren/1.1.0/index.html
[spring-hateoas-siren-1.1.0-apidocs]: https://ingogriebsch.github.io/spring-hateoas-siren/1.1.0/apidocs/index.html
[spring-hateoas-siren-1.0.3-docs]: https://ingogriebsch.github.io/spring-hateoas-siren/1.0.3/index.html
[spring-hateoas-siren-1.0.3-apidocs]: https://ingogriebsch.github.io/spring-hateoas-siren/1.0.3/apidocs/index.html
[spring-hateoas-siren-samples]: https://github.com/ingogriebsch/spring-hateoas-siren-samples/
[spring-hateoas]: https://spring.io/projects/spring-hateoas
[spring-boot]: https://spring.io/projects/spring-boot
[spring-framework]: https://spring.io/projects/spring-framework
[siren]: https://github.com/kevinswiber/siren
[maven-central]: https://search.maven.org/artifact/de.ingogriebsch.hateoas/spring-hateoas-siren
[sonatype-snapshots-repository]: https://oss.sonatype.org/content/repositories/snapshots/de/ingogriebsch/hateoas/spring-hateoas-siren/
