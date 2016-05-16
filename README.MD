
Every agent invokes the following requests to the "remote" resource:

-   Obtain **visited** destinations for a user. (User identification is
    propagated via request header and basically everything about a user
    maximally simplified since it's out of scope of this example)

-   Obtain **recommended** destinations for a user. Requests obtaining
    **visited** and **recommended** destinations are independent and can
    be run in parallel.

-   Obtain weather **forecasts** for recommended destinations. New
    client request is invoked for every recommended destination.
    Obtaining weather **forecasts** depend on actual
    **recommended** destinations.

-   Obtain trip **calculations** (prices) for recommended destinations.
    New client request is invoked for every recommended destination.
    Obtaining trip **calculations** depend on actual
    **recommended** destinations. **forecast** and **calculation**
    requests are independent on each other and can be invoked
    in parallel.

Contents
--------

The mapping of the URI path space is presented in the following table:

URI path                                           | Resource class                  | HTTP methods   | Allowed values
-------------------------------------------------- | ------------------------------- | -------------- | ------------------------------------------------------------------------------
**_/rx/agent/sync_**                               | SyncAgentResource               | GET            | returns JSON
**_/rx/agent/async_**                              | AsyncAgentResource              | GET            | returns JSON
**_/rx/agent/observable_**                         | ObservableAgentResource         | GET            | returns JSON
**_/rx/agent/completion_**                         | CompletionStageAgentResource    | GET            | returns JSON
**_/rx/agent/listenable_**                         | ListenableFutureAgentResource   | GET            | returns JSON
**_/rx/remote/destination/visited_**               | DestinationResource             | GET            | returns JSON
**_/rx/remote/destination/recommended_**           | DestinationResource             | GET            | returns JSON
**_/rx/remote/forecast/{destination}_**            | ForecastResource                | GET            | destination - name of a country; returns XML (random value)
**_/rx/remote/calculation/from/{from}/to/{to}_**   | CalculationResource             | GET            | from - name of a country, to - name of a country; returns XML (random value)

Application is Servlet 3 based, web.xml-less. Everything needed (resources/providers) is registered in `RxApplication`.

Sample Response
---------------

Agent responses look similar to the following one:

```javascript
{
  "visited" : [ {
    "destination" : "Antigua & Barbuda"
  }, {
    "destination" : "Guinea"
  }, {
    "destination" : "Malta"
  }, {
    "destination" : "Denmark"
  }, {
    "destination" : "Tajikistan"
  } ],
  "recommended" : [ {
    "destination" : "Bolivia",
    "forecast" : "Showers",
    "price" : 1359
  }, {
    "destination" : "Yemen",
    "forecast" : "Haze",
    "price" : 8032
  }, {
    "destination" : "Dominican Republic",
    "forecast" : "Cloudy",
    "price" : 1141
  }, {
    "destination" : "Korea South",
    "forecast" : "Mostly Sunny",
    "price" : 9853
  }, {
    "destination" : "Saudi Arabia",
    "forecast" : "Fog",
    "price" : 9063
  } ],
  "processingTime" : 877
}
```

As can be seen the response entity contains 3 main elements: **visited**
(list of visited destinations), **recommended** (list of recommended
destinations + weather forecast and price calculation) and
**processingTime** (describing how long it took to obtain previous two
elements).

Running the Example
-------------------

Run the example as follows:

>     mvn clean package jetty:run

This deploys current example using Jetty. You can access the application at:

-   <http://localhost:8080/rx/agent/sync>
-   <http://localhost:8080/rx/agent/async>
-   <http://localhost:8080/rx/agent/listenable>
-   <http://localhost:8080/rx/agent/observable>
-   <http://localhost:8080/rx/agent/completion>

