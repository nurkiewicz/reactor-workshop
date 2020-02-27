# WebFlux Exercises

1. `OpmlReader.allFeeds()` should return `Flux<Outline>` of blogs
2. `FeedReader.get()` should return `Mono<String>` and use `WebClient instead of `HttpURLConnection`
3. Use `WebClient` instead of `HttpURLConnection`.
4. Handle errors and 301 redirect
5. Publish SSE stream of new articles
6. Polling for new articles periodically from each `Outline`
7. Store articles in the database (use e.g. `ReactiveMongoRepository`) so that after restart you don't publish the same articles
    1. Check out e.g. `ArticlesSources`
8. Create endpoints for browsing (e.g. most recent, about something, by author...). See `ArticlesController`
    1. Most recent
    2. From one blog only
    3. By keyword
    4. Fetch article content
9. Make a simple front-end for SSE
10. Use Redis to store how many times each article's content was fetched
