# Source code conventions

## Classes and interfaces name conventions

Base package for all classes is *org.cleverbus*.

There are the following name conventions for classes and interfaces:

|     Suffix       |              Meaning                |
| ---------------- | ----------------------------------- |
| ...*Route*       | Route implementation                |
| ...*Converter*   | Converter implementation            |
| ...*Service*     | Service implementation              |
| ...*DAO*         | DAO interfaces                      |
| ...*Exception*   | Exception                           |
| ...*Controller*  | MVC controller                      |
| ...*Factory*     | Class that creates another classes  |
| ...*Test*        | Test class                          |
| ...*Tools*       | Class with useful static methods    |
| ...*Helper*      | Helper class, usually for local use |
| ...*Enum*        | Enumeration                         |


## Method name conventions

There are the following name conventions for methods:

|         Prefix          |                    Meaning                      |           Return value              | 
| ----------------------- | ----------------------------------------------- | ----------------------------------- |
| *set*...                | sets value or reference                         |                                     |
| *get*...                | gets value or reference                         | value/reference or throws exception |
| *add*...                | adds value/reference                            |                                     |
| *update*...             | data/state change                               |                                     |
| *delete*...             | data remova                                     |                                     |
| *create*...             | new object creation                             |                                     |
| *exists*...             | existence check                                 | It returns true or false.           |
| *check*...              | common check                                    | true or false                       |
| *validate*...           | validates input values or internal object state |                                     |
| *findAll*...            | finds all possible values                       | collection or empty collection      |
| *findBy*/*findXyzBy*... | finds value(s) by specified input parameters    | value/reference or null             |
| *test*...               | test method, usually annotated by *@Test*       |                                     |


**Demarcation of transactions** - there is expected readonly transaction for methods which have the following prefixes: *get, exists, check, findAll, find*.

## Syntax rules

Adhere to basic [Java Code Conventions](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html) from Sun/Oracle.

Common syntax rules:

-   Basic unit of indentation is 4 spaces. Tab should be converted to spaces.
-   Line lenght is 120 characters.
-   Maximum size of one class shoudn't exceed 700 lines.
-   Use English language exclusively - for class, interface and method names, in comments, GUI, etc.
-   Use UTF-8 character set in all source code files.

## How to use *TODO*?

*TODO* uses developer for himself, it's not tool for assigning work to anybody else. Never use *TODO* without username.

Use *TODO* in the following format where *TO\_WHOM* is developer's username, for example PJUZA, THANUS.

```
TODO TO_WHOM
```

## JavaDoc

JavaDoc and comments in general are very useful - it's not easy to find balance between many comments without meaningful information and no comments at all. There are few rules which are good to adhere: [How to Write Doc Comments for the Javadoc Tool](http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html)

Use *package-info.java* file for every package.

## @deprecated

We have to adhere backward compatibility and sometimes there is no other possibility then leave old code for deprecation and create new one for next releases.

If any class or method is deprecated then it's necessary to:

-   use *@Deprecated* annotation
-   add *@deprecated* into JavaDoc with information what to use instead

```java
    /**
     * Helper method which creates input route for asynch. messages.
     *
     * @param route               the route where we want to create asynch. input routing
     * @param routeId             the input route ID
     * @param inUri               the from URI of this route
     *
     * @deprecated Use {@link AsynchRouteBuilder} instead.
     */
    @Deprecated
    public static RouteDefinition createAsynchInRoute(RouteBuilder route, String routeId, String inUri)
```

Methods or classes marked as deprecated will be removed in next major version.

## License header

Use the following [Apache license](http://www.apache.org/licenses/LICENSE-2.0.html) header in all Java classes.

```java
/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cleverbus.spi.alerts;
```
