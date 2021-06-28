# Reactor training

[![Java CI with Gradle](https://github.com/nurkiewicz/reactor-workshop/actions/workflows/gradle.yml/badge.svg)](https://github.com/nurkiewicz/reactor-workshop/actions/workflows/gradle.yml)

Spring [Reactor](https://projectreactor.io) hands-on training (3 days)


## Day 1: Introduction

- What is reactive programming
- Crash course to `CompletableFuture` and thread pools
- Introducing Reactor
- How to create a stream?
  - `just()`, `generate()`, `create()`, `fromCallable()`, `fromStream()`
- Laziness
  - Hot vs. cold
- Basic operators
  - `map()`, `filter()`, `filterWhen()` `flatMap()`, `handle()`, `take()`, `skip()`
  - `doOn*()` operators
  - `window()`, `buffer()`, `distinct()`
  - `cast()`, `ofType()`, `index()`
  - `timestamp()`, `elapsed()`
  - `zip()`, `merge()`
- Error handling
  - `timeout()`, `retry*()`, `retryBackoff()`
  - `onError*()`
- Blocking and reactive, back and forth
- Concurrency with blocking code and thread pools
  - `subscribeOn()`, `parallel()`
- Unit testing

## Day 2: Reactor advanced
- Concurrency with non-blocking code
- Advanced error handling and retries
- `transform()` vs. `transformDeferred()`
- Advanced operators
  - `groupBy()`, `window()`
  - `reduce()`, `scan()`
  - `expand*()`
- Backpressure
  - `onBackpressure*()`
- `Processor` API
  - `Unicast`, `Emitter`, `Replay`
- Advanced testing with virtual time
- `Context`
- Speculative execution example
- [RxJava](https://github.com/ReactiveX/RxJava) interoperability

## Day 3: Practical
- Comparison to blocking and asynchronous servlets
- Refactoring existing application to Reactor
- [Spring Boot](https://spring.io/projects/spring-boot)
  - Reactive database access
  - Reactive controllers
  - `WebFilter`
  - Global error handling
  - Payload validation
  - Web sockets
- Streaming data in and out
- Troubleshooting and debugging
  - `checkpoint()`, `onOperatorDebug()`, `doOn*()`

## Reference materials

1. [Reactor 3 Reference Guide](https://projectreactor.io/docs/core/release/reference/)
2. [Web on Reactive Stack](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux) in [Spring Framework Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/index.html)
3. [The "Spring WebFlux Framework"](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-webflux) in [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## Troubleshooting

### IntelliJ test runner

In IntelliJ it's much faster to run tests directly, rather than through Gradle.
Go to `Preferences` -> `Build, Execution, Deployment` -> `Build Tools` -> `Gradle` and select `IntelliJ IDEA` from `Run Tests Using` drop-down.

### Error `Can not connect to Ryuk at localhost:...`

Add this environment variable:

```
TESTCONTAINERS_RYUK_DISABLED=true
```

See: [Disabling Ryuk](https://www.testcontainers.org/features/configuration/#disabling-ryuk)
