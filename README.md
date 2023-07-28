# MapLibreGL add-on project for Vaadin

A Vaadin add-on that implements Java API for [MapLibreGL](https://maplibre.org) slippy map component.

## Development instructions

Currently, the add-on is implemented purely with DOM API + JS calls. The required JS & CSS are injected on the fly. The rationaly was to avoid the need for custom front-end bundle -> easier to maintain and take in use in application projects.

### Development

Starting the test/demo server:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080

### Integration test

TODO add integration tests :-)

## Publishing to Vaadin Directory

With commit rights to the repository, issue:

    mvn release:prepare release:cleanup

Configured GH action will build a release and push to Maven Central.
