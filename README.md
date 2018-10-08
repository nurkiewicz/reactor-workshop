# Reactor training

Spring [Reactor](https://projectreactor.io) hands-on training (3 days)


## Day 1: Introduction

- What is reactive programming
- Crash course to `CompletableFuture` and thread pools
- Introducing Reactor
- How to create a stream?
  - `just()`, `generate()`, `create()`, `fromCallable()`
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
  - `timeout()`
  - `onError*()`, `retry*()`
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
  - `exapnd*()`
- Backpressure
  - `onBackpressure*()`
- `Processor` API
  - `Unicast`, `Emitter`, `Replay`
- Advanced testing with virtual time
- `Context`
- [RxJava](https://github.com/ReactiveX/RxJava) interoperability

## Day 3: Practical
- `ConnectableFlux`
- Refactoring existing application to Reactor
- [Spring Boot](https://spring.io/projects/spring-boot)
  - Reactive database access
  - Reactive controllers
- Streaming data in and out
- Troubleshooting and debugging
  - `checkpoint()`, `onOperatorDebug()`, `doOn*()`
