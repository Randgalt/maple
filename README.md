[![Build Status](https://travis-ci.org/Randgalt/maple.svg?branch=master)](https://travis-ci.org/Randgalt/maple)
[![Maven Central](https://img.shields.io/maven-central/v/io.soabase.maple/maple-parent.svg)](http://search.maven.org/#search%7Cga%7C1%7Cmaple-slf4j)

# Maple

***Type-safe, consistently named and formatted, structured logging wrapper for SLF4J that's ideally suited for your logging aggregator.***

```java
log.info(schema -> schema.id(userId).code(CODE_USER).qty(totalQty));
```

## Quickstart

- Define a logging schema interface
- Wrap an [SLF4J](https://www.slf4j.org) `Logger`
- Begin structured logging

*Define a logging schema interface*

```java
public interface Logging {
    Logging id(String id);
    Logging fullName(String name);
    Logging code(CodeType code);
    Logging qty(int qty);
    // etc
}
```

*Wrap an SLF4J Logger*

```java
MapleLogger<Logging> logger = MapleFactory.getLogger(log, Logging.class);
```

*Begin structured logging*

```java
logger.info(s -> s.id(userId).fullName("Some Person").code(CODE_USER).qty(totalQty));

// translated into SLF4J call:
slf4jLogger.info("id=XYZ123 full_name=\"Some Person\" code=user qty=1234");
```

## Introduction

[Per Thoughtworks](https://www.thoughtworks.com/radar/techniques/structured-logging) we should log in a structured manner...

[Per Splunk](http://dev.splunk.com/view/logging/SP-CAAAFCK): "Use clear key-value pairs. One of the most powerful features of Splunk software is its ability to extract fields from events when you search, creating structure out of unstructured data."

[Per Elasticsearch](https://www.elastic.co/blog/structured-logging-filebeat): "[Logging] works best when the logs are pre-parsed in a structured object, so you can search and aggregate on individual fields." It can already be done in Go or Python so why not Java?

If you export your logs to a centralized indexer, structuring your logging will make the indexer's job much easier and you will be able to get more and better information out of your logs. Manual structured logging is error prone and requires too much discipline. We can do better.

## The Problem

Log files are not individually read by humans. They are aggregated and indexed by systems such as Elasticsearch and Splunk. Free form text messages are not very useful for these systems. Instead, best practices dictate that logging be transformed into fields/values for better indexing, querying and alerting.

Logging libraries have responded to this problem by providing APIs that make creating field/value logging easier. Much like Java's `String.format()` method you can put tokens in your log message to be replaced by runtime values. However, much like the difference between dynamically typed languages and strongly typed languages, token replacement is error prone, i.e.

- It's easy to misspell field names
- It's easy to transpose values in the replacement list
- A field name in one part of the code might be spelled differently in another part of the code
- It's hard to enforce required logging fields (e.g. "event-type")
- No good way to prevent secure values such as passwords, keys, etc. from getting logged
- Spaces, quotes, etc. need to be manually escaped

## Structured Logging Library

- ***Not a new logging library*** - merely a strongly typed wrapper for [SLF4J](https://www.slf4j.org)
- Strongly typed logging model provides consistent naming and field/value mapping
- Automatic escaping/quoting of values
- Very low overhead
- Optional support for:
  - Object/model flattening
  - Required fields
  - "Do Not Log" fields
  - Testing utilities
  - Composed logging
  - Consistent snake-case naming

------

# Documentation and Reference

## Table of Contents

- [Logging Schema](#logging-schema)
- [MapleLogger](#maplelogger)
- [Obtaining a MapleLogger Instance](#obtaining-a-maplelogger-instance)
- [Logging Formatters](#logging-formatters)
- [Additional Features](#additional-features)
- [Examples](#examples)
- [Unstructured Logging, Exceptions](#unstructured-logging-exceptions)
- [Add To Your Project](#add-to-your-project)

## Logging Schema

A "Logging Schema" defines the field/values that you want to log. Depending on your needs, you can have one schema for your 
entire project, a few different schema for different parts of the code, etc. 

Logging Schema are Java interfaces. Schema should contain methods that each return the interface type and take exactly one 
argument. Thus each method describes a field (the method name) and a value (the method argument). Example:

```java
public interface Logging {
    Logging id(String id);
    Logging fullName(String name);
    Logging address(Address address);
    Logging qty(int qty);
}
```

Formatting/processing of schema arguments is controlled by a `MapleFormatter` (see the [Logging Formatters](#logging-formatters) section).

## MapleLogger

At the heart of the library are instances of `MapleLogger`. These instances are parameterized with a [Logging Schema](#logging-schema), internally wrap SLF4J `Logger` instances and provide 
similar methods for logging at various levels. The methods allow for text messages and exceptions like SLF4J but, additionally, provide a [Logging Schema](#logging-schema) instance that can be filled for logging. 

Here's an example of using a MapleLogger instance versus an SLF4J logger instances:

```java
Logger slf4jLogger = ...
MapleLogger<Schema> mapleLogger = ...

// logging only fields/values
slf4jLogger.info("name={} age={}", nameStr, theAge);
mapleLogger.info(s -> s.name(nameStr).age(theAge));

// logging message, exception, fields/values
slf4jLogger.info("Something Happened name={} age={}", nameStr, theAge, exception);
mapleLogger.info("Something Happened", exception, s -> s.name(nameStr).age(theAge));
```

Notes:

- For each logging statement, a new logging schema is allocated 
- The logging schema allocation and execution only occurs if the logging level is enabled
- The formatting of message, exception and logging schema into an actual log message is controlled by the currently configured [Logging Formatter](#logging-formatters)

## Obtaining a MapleLogger Instance

Use methods in `MapleFactory` to obtain instances of `MapleLogger` to use for logging.

__MapleFactory__

| Method | Description |
| ------ | ----------- |
| getLogger(Logger logger, Class&lt;T> schema) | Returns a structured logging instance that wraps the given SLF4J logger and provides an instance of the given schema class | 
| getLogger(Class&lt;?> clazz, Class&lt;T> schema) | Obtains an SLF4J logger via `LoggerFactory.getLogger(clazz)`, returns a structured logging instance that wraps it and provides an instance of the given schema class | 
| getLogger(String name, Class&lt;T> schema) | Obtains an SLF4J logger via `LoggerFactory.getLogger(name)`, returns a structured logging instance that wraps it and provides an instance of the given schema class | 

## Logging Formatters

The formatting of the log message is customizable. Two formatters are provided, `StandardFormatter` and `ModelFormatter`. You change the logging formatter used by calling 
`MapleFactory.setFormatter(...)`.

_StandardFormatter_

The StandardFormatter formats the log in `field=value` pairs and has several options. Values can be quoted and/or escaped and the log main message can appear at the beginning or the end of the log string.

_ModelFormatter_

The ModelFormatter extends _StandardFormatter_ to format all schema arguments as flattened model values. All arguments are passed to a provided Jackson ObjectMapper to serialize to a tree. The tree 
components are flattened into schema values. With this formatter you can use an annotation to keep secret information from being logged.
Annotate any field (or corresponding getter) with `@DoNotLog` and then add `DoNotLogAnnotationIntrospector` to the `ObjectMapper` used for the formatter.
See the [DoNotLog](#donotlog) section for details.

## Additional Features

### Required Values

If you would like to require certain schema values to not be omitted, annotate them with `@Required`. E.g.

```java
public interface MySchema {
    @Required
    MySchema auth(String authValue);
}
```

The Structured Logger will throw `MissingSchemaValueException` if no value is set for required values. Note: if you want to only use this in development or pre-production, you can globally 
disable required value checking by calling `MapleFactory.setProductionMode(true)`.

### Ordering

By default, schema values are output in alphabetical order. Add `@SortOrder` annotations to change this. E.g.

```java
public interface SchemaWithSort {
    SchemaWithSort id(String id);

    SchemaWithSort bool(boolean b);

    @SortOrder(1)
    SchemaWithSort qty(int qty);

    @SortOrder(0)
    SchemaWithSort zero(int z);
}
```
This schema will be output ala: `zero=xxx qty=xxx bool=xxx id=xxx`

### Capture a Partial Value

You can pre-fill some values in the schema if needed. For example, you may want to use a request
ID in all logging in a method. This is done with the `concat()` method. E.g.

```java
MapleLogger<Schema> log = ...

// in some method

Statement<Schema> partial = s -> s.requestId(id);

// later

log.info("message", partial.concat(s -> s.code(c).name(n))); // request ID is also logged
```

### DoNotLog

A Jackson annotation is provided to denote values that you do not want to be logged, `@DoNotLog`. If you use the 
`ModelFormatter` [Logging Formatters](#logging-formatters) (or your own Logging Formatter 
that works with Jackson) use this annotation to mark fields that should not be logged. `DoNotLogAnnotationIntrospector` must 
also be registered with the Jackson mapper. E.g.

Register the annotation with Jackson:

```
ObjectMapper mapper = ...

DoNotLogAnnotationIntrospector.register(mapper);

// this mapper would be used with ModelFormatter or your custom formatter
```

Annotate your models

```
public class Person {
    private final String name;
    
    @DoNotLog
    private final String password;
    
    // etc.
}
```

Create logging schema that use the model

```
public interface Logging {
    Logging person(Person p);
    
    Logging eventType(String type);
    
    ...
}

```

### Unstructured Logging, Exceptions

You can include an unstructured message as well as any exceptions in your log statements. E.g.

```java
MapleLogger<Schema> log = ...

log.info("Any message you need", s -> s.event(e).qty(123));

...

log.info(exception, s -> s.event(e).qty(123));

...

log.info("Any message you need", exception, s -> s.event(e).qty(123));
```

If needed, you can also directly access the SLF4J logger. E.g.

```java
MapleLogger<Schema> log = ...

log.logger().info("Message: {}", message, exception);
```

## Examples

Several Examples are provided as a submodule to the project. See the [Examples Module](maple-examples) for details. 

## Add To Your Project

| GroupID | ArtifactId |
| ------- | ---------- |
| `io.soabase.maple` | `maple-slf4j` |

You must also declare a dependency on SLF4J and an SLF4J compatible logging library. Additionally, if you will be using the 
`ModelFormatter` you must declare a dependency on Jackson.
