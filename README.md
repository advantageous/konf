[Konf Website](http://advantageous.github.io/konf/)

# Konf - Typed Java Config system 
Java configuration library similar in concept to TypeSafe config,
but uses full 

* YAML 
* JSON
* JSON Lax
* JavaScript (useful to create Config DSLs, and basic config logic)
* Java Pojos (Pojos, Lists, Maps, basic types)
* TypeSafe Config
* Java properties

You can also mix and match TypeSafe Config.

***Konf*** allows you to easily create your own 
[config DSLs](https://github.com/advantageous/konf/wiki/Config-Logic---creating-your-own-config-DSL) 
something that is not possible with ***TypeSafe Config***.


## Using Konf on your project

Konf is in the [public maven repo](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.advantageous.konf%22).

### Using konf from maven
```xml
<dependency>
    <groupId>io.advantageous.konf</groupId>
    <artifactId>konf</artifactId>
    <version>1.3.0.RELEASE</version>
</dependency>
```

### Using konf from gradle
```java
compile 'io.advantageous.konf:konf:1.3.0.RELEASE'
```

### Using konf from scala sbt
```java
libraryDependencies += "io.advantageous.konf" % "konf" % "1.3.0.RELEASE"
```

### Using konf from clojure leiningen
```lisp
[io.advantageous.konf/konf "1.3.0.RELEASE"]
```

Here is an example config for JavaScript. 

Konf expects the `config` variable to be set to a JavaScript object with 
properties.

#### JavaScript based configuration for Java
```javascript
var config = {

  myUri: uri("http://host:9000/path?foo=bar"),

  someKey: {
    nestedKey: 234,
    other: "this text"
  }

};
```

You can use full JavaScript for configuration as long as you define a
variable called `config` that results in a JavaScript object which
equates to a Java map. 


## Defining your own DSL

You can define you own config DSL for your environment. 
We have a [full example that shows you how to create a custom config DSL](https://github.com/advantageous/konf/wiki/Config-Logic---creating-your-own-config-DSL)
for your internal projects. The example uses Mesosphere and Docker PORT 
look ups and it is from a real project. 

#### Defining your own config DSL
```javascript
var config = {

  platform: {

    statsd: "udp://" + getDockerHost() + ":8125",

    servicePort: mesosPortAt(0, 8080),
    adminPort: mesosPortAt(1, 9090),
    ...
```

See the real world for [example that uses Konf to find ports under
Mesosphere](https://github.com/advantageous/konf/wiki/Config-Logic---creating-your-own-config-DSL) 
(running in stating or prod) or under Docker (running on 
a local developers box). 

## Overview

* implemented in plain Java SDK almost no dependencies (sl4j, and reflekt with no others)
* supports files in : YAML, JSON, JSON LAX, JavaScript, Java properties or any tree of Map/List basic types and POJOs
* allows you to easily create your own config DSL
* merges multiple configs across all formats
* can load from configs, from classpath, http, file or just an Java Object tree
* great support for "nesting" (treat any subtree of the config the same as the whole config)
* users can override the config with Java system properties, java -Dmyapp.foo.bar=10 and sysProp 
* users can override the config with OS environment variables
* supports configuring an app, with its framework and libraries, all from a single file such as application.yaml
* parses duration and size settings, "512k" or "10 seconds"
* converts types, so if you ask for a boolean and the value is the string "yes", or you ask for a float and the value is an int, it will figure it out.
* API based on immutable Config instances, for thread safety and easy reasoning about config transformations
* extensive test coverage

This library limits itself to config. 
If you want to load config from another source, e.g., database or Redis or MongoDB, 
then you would need to write some custom code. The library has nice support for merging 
configurations (Configs with fall-backs) so if you build a custom Config
 from a custom source it's easy to merge it in. Just implement Config and then use 
 `config(config...)` to configure your config into a chain of other configs. 
 This is described at length below see "Loading config files with fallbacks".

##  License

The license is Apache 2.0.

## Release Notes

Please see [Release Notes](https://github.com/advantageous/konf/releases), and
[Release Notes In Progress](https://github.com/advantageous/konf/wiki/Release-Notes-Draft)
for the latest releases.

## Build

The build uses gradle and the tests are written in Java; and, 
the library itself is plain Java.

## Using the Library

```java
import io.advantageous.config.ConfigLoader;

Config conf = ConfigLoader.load("myconfig.js", "reference.js");
int bar1 = conf.getInt("foo.bar");
Config foo = conf.getConfig("foo");
int bar2 = foo.getInt("bar");
```

##  Longer Examples

You can see longer examples in [tests](https://github.com/advantageous/konf/blob/master/src/test/java/io/advantageous/config/JsLoadTest.java)
along with [sample config](https://github.com/advantageous/konf/blob/master/src/test/resources/test-config.js).
You can run these examples by `git cloning` this project and `running gradle test`.

In brief, as shown in the examples:

You create a Config instance provided by your application.
You use `ConfigLoader.load()` and you can define your own config system.
You could setup default `reference.yaml` or `reference.json` but you don't have to.
You could just load a single level of config. Config is as complex or as simple
as you need. 

A `Config` can be created with the parser methods in `ConfigLoader.load`
 or built up from any POJO object tree or tree of Map/List/Pojos basic value.
It is very flexible. Examples are shown below and linked to below that use
  JSON, YAML and allow you to define your own `DSL` like config.
It is very simple and easy to use. 

##  Immutability

Objects are immutable, so methods on Config which transform the 
configuration return a new `Config`. 
There is no complex tree of `Config` objects. Just `Config`. 
It is pretty simple to use and understand.

  
## Java interface for Konf is Config.

The Java interface for Konf is Config.
You can get a sub Config from Config (`getConfig(path)`).
The `path` is always in dot notation (`this.that.foo.bar`).
You can also use:
* `hasPath(path)`
* `getInt(path)` 
* `getLong(path)`
* `getDouble(path)`
* `getBoolean(path)` can be true, false, "yes", "no", "on", "off", yes, no, off, on
* `getString(path)`
* `getStringList(path)` gets a list of strings
* `getConfig(path)` gets a sub-config.
* `getMap(path)` gets a map which is a sub-config.
* `getConfigList(path)` gets a list of configs at the location specified.
* `getIntList(path)` 
* `getLongList(path)`
* `getDoubleList(path)`
* `getBooleanList(path)`
* `getDuration(path)` gets `java.time.Duration` useful for timeouts
* `getDurationList(path)` gets duration list
* `getUri(path)` gets `java.net.URI` useful for connecting to downstream services
* `getUriList(path)` useful for connecting to downstream services


The `getMap` works with JavaScript objects (or Java maps see below for loading config from Java objects, YAML or JSON). 
The `getStringList` and `getConfigList` works
with JavaScript array of string and a JavaScript array of JavaScript objects. 

Note you get an exception if the `path` requested is not found. 
Use `hasPath(path)` if you think the config path might be missing. 

Here is partial glimpse at the `Config` interface.

#### Config interface
```java
public interface Config {

    /** Get string at location. */
    String getString(String path);

    /** Checks to see if config has the path specified. */
    boolean hasPath(String path);

    /** Get int at location. */
    int getInt(String path);

    /** Get float at location. */
    float getFloat(String path);

    /** Get double at location. */
    double getDouble(String path);

    /** Get long at location. */
    long getLong(String path);

    /** Get list of strings at location. */
    List<String> getStringList(String path);

    /** Get map at location. */
    Map<String, Object> getMap(String path);

    /** Get a sub-config at location. */
    Config getConfig(String path);

    /** Get list of sub-configs at location. */
    List<Config> getConfigList(String path);

    /**  Get a single POJO out of config at path. */
    <T> T get(String path, Class<T> type);

    /**  Get a list of POJOs. */
    <T> List<T> getList(String path, Class<T> componentType);
    
    /** Get duration. Good for timeouts */
    Duration getDuration(String path);
    
    /** Get duration list. */
    List<Duration> getDurationList(String path);
    
    /** Get int list. */
    List<Integer> getIntegerList(String path);
    ...
}

```

The `getX` methods work like you would expect. Given this config file.

## JavaScript functions for config

#### JavaScript functions that we support
* `sysProp(propName)` to read a sysProp as in `fooSize : sysProp("my.foo.size")`
* `sysPropOrDefault(propName, defaultValue)` to read a sysProp or a default
* `isWindowsOS()`, `isMacOS()`, `isUnix()`, `isLinux()`, `isSolaris()` 
* `env()` as in `fooSize : env('MY_FOO_SIZE')` or even `fooSize : sysPropOrDefault("my.foo.size", env('MY_FOO_SIZE'))`
* `uri()` which creates a `java.net.URI` as in `fooURI : uri ("http://localhost:8080/")` 
* `java.time.Duration` is imported as `duration` 
*  `java.lang.System` is imported as `system`  
* `seconds(units)`, `minutes(units)`, `hours(units)`, `days(units)`, `millis(units)` and `milliseconds(units`) define a `Duration` which is useful for configuring timeouts and interval jobs
* constants `yes`, `no`, `on`, `off` for boolean config
* `load(resources...)` load a config
* `configs(config...)` chain a group of configs
* `bytes(units)`, `kilobytes(units)`, `megabytes(units)`, `gigabytes(units)` to read sizes

#### Sample config for testing and showing how config works

```javascript
var config = {

  myUri: uri("http://host:9000/path?foo=bar"),

  someKey: {
    nestedKey: 234,
    other: "this text"
  },

  int1: 1,
  float1: 1.0,
  double1: 1.0,
  long1: 1,
  string1: "rick",
  stringList: ['Foo', 'Bar'],
  configInner: {
    int2: 2,
    float2: 2.0
  },
  uri: uri("http://localhost:8080/foo"),
  myClass: "java.lang.Object",
  myURI: "http://localhost:8080/foo",
  employee: {"id": 123, "name": "Geoff"},
  employees: [
    {id: 123, "name": "Geoff"},
    {id: 456, "name": "Rick"},
    {id: 789, 'name': "Paul"}
  ]
};

```


We can do the following operations. 

First we load the config.

#### Loading the config.

```java

    private Config config;

    @Before
    public void setUp() throws Exception {
        config = ConfigLoader.load("test-config.js");
    }
```

Note that `ConfigLoader.load(resources...)` takes a variable length string array.
By default a resource String can contain a valid URI, which 
can have the scheme `classpath`, `file`, or `http`. If you do not specify
a scheme than the path is assumed to be a classpath resource. 

#### Using different resources

```java
        config = ConfigLoader.load(
                      "/io/mycompany/foo-classpath.js",
                      "classpath:test-config.js",
                      "classpath://foo.js",
                      "classpath:/bar.js",
                      "file://opt/app/config.js",
                      "file:///opt/app/config2.js",
                      "file:/opt/app/config.js",
                      "http://my.internal.server:9090/foo.js"
                      );

```

Then we show reading basic types with the `config` object using `getX`.

#### Reading basic types from config

```java
    @Test
    public void testSimple() throws Exception {

        //getInt
        assertEquals(1, config.getInt("int1"));
        
        //getStringList
        assertEquals(asList("Foo", "Bar"), 
               config.getStringList("stringList"));
               
        //getString       
        assertEquals("rick", config.getString("string1"));
        
        //getDouble
        assertEquals(1.0, config.getDouble("double1"), 0.001);
        
        //getLong
        assertEquals(1L, config.getLong("long1"));
        
        //getFloat
        assertEquals(1.0f, config.getFloat("float1"), 0.001);
        
        //Basic JDK value types are supported like class.
        assertEquals(Object.class, config.get("myClass", Class.class));
        
        //Basic JDK value types are supported like URI.
        assertEquals(URI.create("http://localhost:8080/foo"), 
                config.get("myURI", URI.class));
                
        assertEquals(URI.create("http://localhost:8080/foo"), 
                config.get("uri", URI.class));
                
    }

```

You can work with nested properties as well.

#### Reading a nested config from the config

```java
    @Test
    public void testGetConfig() throws Exception {
        //Read nested config.
        final Config configInner = config.getConfig("configInner");
        assertEquals(2, configInner.getInt("int2"));
        assertEquals(2.0f, configInner.getFloat("float2"), 0.001);
    }

    @Test
    public void testGetMap() throws Exception {
        //Read nested config as a Java map.
        final Map<String, Object> map = config.getMap("configInner");
        assertEquals(2, (int) map.get("int2"));
        assertEquals(2.0f, (double) map.get("float2"), 0.001);
    }
```

You can read deeply nested config items as well by specifying the 
property path using dot notation. 
    
#### Reading nested properties with dot notation from config


```java
    @Test
    public void testSimplePath() throws Exception {

        assertTrue(config.hasPath("configInner.int2"));
        assertFalse(config.hasPath("configInner.foo.bar"));
        assertEquals(2, config.getInt("configInner.int2"));
        assertEquals(2.0f, config.getFloat("configInner.float2"), 0.001);
    }
```

You can also read POJOs directly out of the config file.

#### Reading a pojo directly out of the config file
```java

    @Test
    public void testReadClass() throws Exception {
        final Employee employee = config.get("employee", Employee.class);
        assertEquals("Geoff", employee.name);
        assertEquals("123", employee.id);
    }

```

You can read a list of POJOs at once.

#### Reading a pojo list directly out of the config file
```java

    @Test
    public void testReadListOfClass() throws Exception {
        final List<Employee> employees = config.getList("employees", Employee.class);
        assertEquals("Geoff", employees.get(0).name);
        assertEquals("123", employees.get(0).id);
    }
```

You can also read a list of config objects out of the config as well.

#### Reading a config list directly out of the config file
```java

        final List<Config> employees = config.getConfigList("employees");
        assertEquals("Geoff", employees.get(0).getString("name"));
        assertEquals("123", employees.get(0).getString("id"));
```

## Using Config with YAML
 
 First include a YAML to object parser like [YAML Beans](https://github.com/EsotericSoftware/yamlbeans)
 or a library like this.
 
#### Example YAML
 
```yaml
   name: Nathan Sweet
     age: 28
     address: 4011 16th Ave S
     phone numbers:
      - name: Home
        number: 206-555-5138
      - name: Work
        number: 425-555-2306
```
 
#### Using Konf with YAML
 
```java
//Use YamlReader to load YAML file.
YamlReader reader = new YamlReader(new FileReader("contact.yml"));

//Convert object read from YAML into Konf config
Config config = ConfigLoader.loadFromObject(reader.read());

//Now you have strongly typed access to fields
String address = config.getString("address");
```

You can also read Pojos from anywhere in the YAML file as well as 
sub configs. 


## You can also use Konf with JSON using Boon

See [Boon](https://github.com/advantageous/boon) JSON parser project,
and [Boon in five minutes](https://github.com/boonproject/boon/wiki/Boon-JSON-in-five-minutes)

#### Using Konf with JSON

```java
ObjectMapper mapper =  JsonFactory.create();


/* Convert object read from YAML into Konf config.
  'src' can be File, InputStream, Reader, String. */
Config config = ConfigLoader.loadFromObject(mapper.fromJson(src));


//Now you have strongly typed access to fields
String address = config.getString("address");

```

Boon supports LAX JSON (Json with comments, and you do not need to quote
the field).


#### Working with java.time.Duration

* `getDuration(path)` get a duration
* `getDurationList(path)` get a duration list

Konf supports "10 seconds" style config for duration as well as
having built-in functions and support for ISO-8601. See documentation 
for [duration config](https://github.com/advantageous/konf/wiki/Working-with-Durations)
for more details.

##### Konf can reads list of numbers. 

* `getIntList` reads list of ints
* `getLongList` reads list of longs
* `getDoubleList` reads list of doubles
* `getFloatList` reads list of floats

See documentation [list of number configuration](https://github.com/advantageous/konf/wiki/Working-with-lists-of-ints,-longs,-doubles,) 
for more details.

#### Konf can read memory sizes

* `getMemorySize(path)` 
* `getMemorySizeList(path)`



This means we support config like:

#### Sizes supported.
```javascript

  diskSpace : " 10 gigabytes",
  diskVolumes : [" 10 gigabytes", "10GB", "10 gigabytes", 10]
```

We support the following size Strings. 

#### Supported size strings
```java

public enum MemorySizeUnit {

    BYTES(1, "B", "b", "byte", "bytes"),
    KILO_BYTES(1_000, "kB", "kilobyte", "kilobytes"),
    MEGA_BYTES(1_000_000, "MB", "megabyte", "megabytes"),
    GIGA_BYTES(1_000_000_000, "GB", "gigabyte", "gigabytes"),
    TERA_BYTES(1_000_000_000, "TB", "terabyte", "terabytes"),
    PETA_BYTES(1_000_000_000_000L, "PB", "petabyte", "petabytes"),
    EXA_BYTES(1_000_000_000_000_000L, "EB", "exabyte", "exabytes"),
    ZETTA_BYTES(1_000_000_000_000_000_000L, "ZB", "zettabyte", "zettabytes");
    
```

You can also specify the sizes with built-in functions if you don't
want to use strings.

#### Using built-in functions to create sizes.
```javascript
  diskVolumes: [kilobytes(10), megabytes(10), bytes(10), gigabytes(10)]
```

## Loading config files with fallbacks

#### 
```java

import static io.advantageous.config.ConfigLoader.*;
...
    private Config config;
    ...
        config = configs(config("test-config.js"), config("reference.js"));

```

You can load config. The `config` method is an alias for `load(resources...)`.
The `configs(config...)` creates a series of configs where the configs
are search from left to right. The first config that has the object (starting
from the left or 0 index) will return the object. 

Give the following two configs (from the above example).

#### test-config.js
```javascript
var config = {
  abc : "abc",
```


#### reference.js
```javascript
var config = {
  abc : "abcFallback",
  def : "def"
}
```

You could run this test.

#### Testing the reference.js is a fallback for test-config.js.

```java

import static io.advantageous.config.ConfigLoader.*;
...

        config = configs(config("test-config.js"), config("reference.js"));

        final String value = config.getString("abc");
        assertEquals("abc", value);

        final String value1 = config.getString("def");
        assertEquals("def", value1);
```

You can load your config anyway you like. The String `abc` is found
when looking up the key `abc` because it is in the `test-config.js` which
gets read before the value `abcFallback` which is in `reference.js`.
Yet the `def` key yields the `"def"` because it is defined in `reference.js`
but not `test-config.js`. You can implement the same style config reading and
fallback as is in Type Safe Config but with your DSL.


#### Using Konf with Typesafe Config

This allows you to combine TypeSafe `Config` and Konf `Config`.
You can have TypeSafe config be a fallback for Konf or the other way around.


You can load TypeSafe `Config` as a Konf `Config` instance as follows:

#### Loading Typesafe config as a Konf Config object
```java

        Config config = TypeSafeConfig.typeSafeConfig();
        final String abc = config.getString("abc");
        assertEquals("abc", abc);
```

You can also chain TypeSafe config as fallback or Konf `Config` as a fallback
for TypeSafe `Config` as follows:


#### Konf as a fallback for TypeSafe config. 

```java


import static io.advantageous.config.ConfigLoader.config;
import static io.advantageous.config.ConfigLoader.configs;
import static io.advantageous.config.ConfigLoader.load;

...

    Config config;
    ...
        config = configs(TypeSafeConfig.typeSafeConfig(), config("test-config.js"));
```

#### TypeSafe config as a fallback for Konf. 

```java


import static io.advantageous.config.ConfigLoader.config;
import static io.advantageous.config.ConfigLoader.configs;
import static io.advantageous.config.ConfigLoader.load;

...

    Config config;
    ...
        config = configs(config("test-config.js"), TypeSafeConfig.typeSafeConfig());
```

You can convert any TypeSafe `Config` into a Konf `Config` by using
 `TypeSafeConfig.fromTypeSafeConfig(typeSafeConfig)`.
 
Find out more about TypeSafe config support at [Konf TypeSafe config](http://advantageous.github.io/konf-typesafe-config/).

#### Thanks

If you like our configuration project, please try our 
[Reactive Java project](https://github.com/advantageous/reakt)
or our [Actor based microservices lib](https://github.com/advantageous/qbit).

