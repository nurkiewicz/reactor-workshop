# Reactor training

Spring [Reactor](https://projectreactor.io) hands-on training (3 days)


## Day 1: Introduction

- What is reactive programming
- Crash course to `CompletableFuture` and thread pools
- Introducing Reactor
- How to create a stream?
  - `just()`, `generate()`, `create()`, `fromCallable()`, `fromStream()`
- Laziness
  - How vs. cold
- Basic operators
  - `map()`, `filter()`, `flatMap()`, `handle()`, `take()`, `skip()`
  - `using()`, `do*()`
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
- `transform()` vs. `compose()`
- Advanced operators
  - `groupBy()`, `window()`
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
- `ConnectableFlux`
- Refactoring existing application to Reactor
- [Spring Boot](https://spring.io/projects/spring-boot)
  - Reactive database access
  - Reactive controllers
  - `WebFilter`
  - Global error handling
  - Web sockets
- Streaming data in and out
- Troubleshooting and debugging
  - `checkpoint()`, `onOperatorDebug()`, `doOn*()`

## Reference materials

1. [Reactor 3 Reference Guide](https://projectreactor.io/docs/core/release/reference/)
2. [Web on Reactive Stack](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux) in [Spring Framework Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/index.html)
3. [The "Spring WebFlux Framework"](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html#boot-features-webflux) in [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)
