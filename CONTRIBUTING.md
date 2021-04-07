# Contributing to Spring HATEOAS Siren

Thanks for being interested in the Spring HATEOAS Siren library! This guide helps to find the most efficient way to report issues, ask questions and contribute to the project.

## Code of conduct

Please review our [code of conduct][code-of-conduct].

## I want to report an issue or have a question

Please review our [support][support] section.

## I have an idea for a new feature

If you think something is missing or certain use cases could be supported better, let us know! You can do so by opening a discussion on the [discussion forum][discussion-forum]. Provide as much relevant context to why and when the feature would be helpful. Providing context is especially important for "Support XYZ" issues since we might not be familiar with what "XYZ" is and why it's useful. If you have an idea of how to implement the feature, include that as well.

## I want to contribute

We welcome Pull Requests but creating and reviewing Pull Requests take considerable time. This section helps you set up for a smooth Pull Request experience.

It's a great idea to discuss the new feature you're considering on the [discussion forum][discussion-forum] before writing any code. There are often different ways you can implement a feature. Getting some discussion about different options helps shape the best solution. 

When starting directly with a Pull Request, there is the risk of having to make considerable changes. Sometimes that is the best approach, though! Showing an idea with code can be very helpful; but be aware that it might be throw-away work.

You will receive feedback from us on your pull request, but please keep in mind that it may take a moment for us to get back to you as this is a side project that is handled entirely in our little spare time.

## Working on the code base

The Spring HATEOAS Siren codebase is Java based. Therefore you can use any IDE that allows you to implement code in Java. We prefer to use the Eclipse based Spring Tool Suite though.

Note that we build on Java 8, so a Java 8 JDK is required. If you don't have a JDK, you can for example use [sdkman][sdkman].

Regarding code conventions you find additional information in the "etc/ide" folder of this project. There are also some Maven plugins integrated into the build process that check some of the conventions during every build.

## Pull Request builds

Pushing to a branch automatically kicks off a build. The build will be linked in the Pull Request, and under
the [CI action][ci-action] on GitHub.

[ci-action]: https://github.com/ingogriebsch/spring-hateoas-siren/actions/workflows/maven.yml
[code-of-conduct]: CODE_OF_CONDUCT.md
[support]: SUPPORT.md
[discussion-forum]: https://github.com/ingogriebsch/spring-hateoas-siren/discussions
[sdkman]: https://sdkman.io/