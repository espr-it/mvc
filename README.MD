# Simple MVC framework

[![Build Status](https://travis-ci.org/espr-it/mvc.svg?branch=master)](https://travis-ci.org/espr-it/mvc) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.espr/esprit-mvc/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.espr/esprit-mvc) [![codecov.io](http://codecov.io/github/espr-it/mvc/coverage.svg?branch=master)](http://codecov.io/github/espr-it/mvc?branch=master)

The goal of the project is to offer simple, fast, small and testable MVC framework for java applications. IT was designed together with [esprit-injector](https://github.com/espr-it/injector) for running on Google App Engine standard environment - but it's not limited to it. You can run it in any servlet container.

All controllers (we call them routes) can be simple beans with no dependency on the underlying technology - so no servlet requests, responses or template rendering. Which means everything is super testable and you can even import beans as an external jar and run them inside of the container. You just need to define WebConfig with bindings and routes and this library will take care of all conversions, request mapping and response delivery.

The library footprint is intentionally kept minimal and with core dependencies only - it's possible to start a new instance, process request and return response in 3 seconds on the App Engine.

## Examples

##### Register dispatcher in web.xml
```xml
<servlet>
	<servlet-name>dispatcher</servlet-name>
	<servlet-class>it.espr.mvc.Dispatcher</servlet-class>
	<load-on-startup>1</load-on-startup>
	<init-param>
		<param-name>configuration</param-name>
		<param-value>com.project.WebConfig</param-value>
	</init-param>
</servlet>
```

##### Create web config
```java
public class WebConfig extends Configuration {
	@Override
	protected void configureMvc() {
	}
}
```

##### Define your routes

```java
// GET requests to /user/(.*) would be send to method get(String) in UserRoute class
route().get("/users/(:id)").to(UserRoute.class, "get");
```

```java
// You can change regex to match only numbers
route().get("/users/([0-9]+:id)").to(UserRoute.class, "get");
```

```java
// matches GET /users and will call method get(String) with content of request parameter 'ids'
route().get("/users").to(UserRoute.class, "get").with("ids");
```

```java
// matches GET /users/125 and will call get(String,Boolean) with path attribute and 'active' parameter from requests converted to boolean
route().get("/users/([0-9]+:id)").to(UserRoute.class, "get").with('active');
```
The example above should be more explained - the request matcher will try to find a first suitable method in your Route class. So anything with name get and two parameters - method parameter type are then converted into appropriate java values. So the same route could match also get(int userId, String active) too...

## Status

The library is still under heavy development and not everything works as you would expect. There is some chance that some of the API is going to change slightly - but such changes are in major versions only and they should be easy to incorporate in your existing code in couple of minutes.

I use this library on all my project now:

- www.librarist.com - fronted runs on Spring MVC + Velocity + RiotJS, all backend runs on App Engine served this MVC library

## Download and use

There are regular releases on maven now so grab the latest binary and give it a go https://mvnrepository.com/artifact/it.espr/esprit-mvc.
