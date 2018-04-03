# Example project for the TDT4140 course, Spring 2018

This project is an example for Spring 2018, based on the [template project](../tdt4140-gr18nn/README.md)

## The example project

The example project is meant to illustrate the expected architecture, as well as demonstrate relevant implementation techniques, including project and build configuration and coding and library usage.

This project is about managing sets of geo-locations, like tracks from hiking og training, similar to what you get from apps like Strava. The project includes two systems:
* a JavaFX app for visualising, analysing and editing sets of geo-locations
* web server with a REST API for managing sets of geo-locations, primarily for collecting the geo-locations from apps, but also as persistence layer for the JavaFX app

The project is organized as a hierarchical maven project, with the follow sub-modules (contained in the root/aggregator project):
* [tdt4140.gr1800.app.core](app.core/README.md) (in app.core folder): Common domain classes and persistence support.
* [tdt4140.gr1800.app.ui](app.ui/README.md)  (in app.ui folder): JavaFX app.
* [tdt4140.gr1800.web.server](web.server/README.md) (in web.server folder): Web server providing a REST API to domain data.

There is no web client project, yet, since that is outside the scope of the course. We may add this later for completeness, and to illustrate the REST API.
