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
The reference documentation can be found [here][spring-hateoas-siren]. The Javadoc API documentation can be found [here][spring-hateoas-siren-javadoc].

## Integration
Please check section [Setup][spring-hateoas-siren-setup] of the documentation to understand how to integrate this library into your project.

The latest RELEASE version is available through [Maven Central][maven-central].

The latest SNPASHOT version is available through the [Sonatype Snapshots Repository][sonatype-snapshots-repository].

## Examples
A collection of examples that showcase how to use the features provided by the library can be found [here][spring-hateoas-siren-samples].

## License
This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).

[spring-hateoas-siren]: https://ingogriebsch.github.io/spring-hateoas-siren/
[spring-hateoas-siren-setup]: https://ingogriebsch.github.io/spring-hateoas-siren/#setup
[spring-hateoas-siren-javadoc]: https://ingogriebsch.github.io/spring-hateoas-siren/apidocs/
[spring-hateoas-siren-samples]: https://github.com/ingogriebsch/spring-hateoas-siren-samples/
[spring-hateoas]: https://spring.io/projects/spring-hateoas
[siren]: https://github.com/kevinswiber/siren
[maven-central]: https://search.maven.org/artifact/de.ingogriebsch.hateoas/spring-hateoas-siren
[sonatype-snapshots-repository]: https://oss.sonatype.org/content/repositories/snapshots/de/ingogriebsch/hateoas/spring-hateoas-siren/
