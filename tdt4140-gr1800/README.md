# Example project for the TDT4140 course, Spring 2018

This project is an example for Spring 2018, based on the [template project](../tdt4140-gr18nn/README.md). It is meant to illustrate the expected architecture, as well as demonstrate relevant implementation techniques, including project and build configuration and coding and library usage.

This project is about managing sets of geo-locations, like tracks from hiking og training, similar to what you get from apps like Strava. The project includes two systems:
* a JavaFX app for visualising, analysing and editing sets of geo-locations
* web server with a REST API for managing sets of geo-locations, primarily for collecting the geo-locations from apps, but also as persistence layer for the JavaFX app

## Getting startet

### Prerequisites

We develop using **Eclipse**, but other IDEs should also work. You also need **maven**, at least a standalone install, but you should also install maven support in Eclipse, by means of the m2e plugins, which are available in Eclipse's main update site.

We use Java 8 and JavaFX, so both these should be installed. Both JDK 8 and JDK 9 should work.

### Installing 

The project uses maven as build system and is accordingly organised as a hierarchical project with a top-level module with several sub-modules. Most IDEs support importing it by point to the top-level module folder, so you should only need to clone the repository and import.

Eclipse will allow you to import using the **Import... > Existing Projects into Workspace** wizard. Make sure to import all sub-module projects (check the box for "nested projects" in the wizard). You should also view it in the **Project Explorer** with **Project Presentation** set to **Hierarchical**.

After importing, you should see the following sub-module projects (contained in the root/aggregator project):
* [fx-map-control](https://github.com/ClemensFischer/FX-Map-Control) (in FxMapControl folder): Copy of project from github, since it is not (yet?) release on maven central.
* [tdt4140.gr1800.app.core](app.core/README.md) (in app.core folder): Common domain classes and persistence support.
* [tdt4140.gr1800.app.ui](app.ui/README.md)  (in app.ui folder): JavaFX app.
* [tdt4140.gr1800.web.server](web.server/README.md) (in web.server folder): Web server providing a REST API to domain data.

There is no web client project, yet, since that is outside the scope of the course. We may add this later for completeness, and to illustrate the REST API.

## Running the tests

The sub-modules include both ordinary tests and integration tests, use **mvn test** and **mvn integration-test** for running these. The latter is needed for fully testing the web-server project. Note that both kinds of test will be run with **mvn install**.

## Deployment

We have yet to configure deployment of the JavaFX app and the web server. The former must currently be run from the IDE with **Run as > Java Application**, and the latter using **mvn jetty:run**.

## Built with
* [Jackson](https://github.com/FasterXML/jackson) - [JSON](https://www.json.org) library
* [JPX](https://github.com/jenetics/jpx) - library for importing [GPX](https://en.wikipedia.org/wiki/GPS_Exchange_Format) files
* [geojson-jackson](https://github.com/opendatalab-de/geojson-jackson) - library for [GeoJson](http://geojson.org)
* [HSQLDB](http://www.hsqldb.org) - embedded database with SQL support
* [FX-Map-Control](https://github.com/ClemensFischer/FX-Map-Control)
* [TestFX](https://github.com/TestFX/TestFX) - test framework for JavaFX apps
* [Jetty](https://www.eclipse.org/jetty/) - embedded HTTP server

## Authors

* Hallvard Trætteberg

## License

Not yet decided, probably GPL
