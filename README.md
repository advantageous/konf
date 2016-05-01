[Konf Website](http://advantageous.github.io/konf/)

# Konf - Typed Java Config system 
Java configuration library similar in concept to TypeSafe config,
but uses full JavaScript, YAML or JSON for configuration.

Uses JavaScript/JSON/YAML as config for Java. 

You can use full JavaScript for configuration as long as you define a
variable called `config` that results in a JavaScript object which
equates to a Java map.

## Using Konf on your project

Konf is in the [public maven repo](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.advantageous.konf%22).

### Using konf from maven
```xml
<dependency>
    <groupId>io.advantageous.konf</groupId>
    <artifactId>konf</artifactId>
    <version>1.1.0.RELEASE</version>
</dependency>
```

### Using konf from gradle
```java
compile 'io.advantageous.konf:konf:1.1.0.RELEASE'
```

### Using konf from scala sbt
```java
libraryDependencies += "io.advantageous.konf" % "konf" % "1.0.0.RELEASE"
```

### Using konf from clojure leiningen
```lisp
[io.advantageous.konf/konf "1.0.0.RELEASE"]
```

Here is an example config for JavaScript. 

Konf expects the conf variable to be set to a JavaScript object with 
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


## Java interface for Konf is Config.


The Java interface for Konf is Config.
You can get a sub Config from Config (`getConfig(path)`).
The `path` is always in dot notation (`this.that.foo.bar`).
You can also use:
* `hasPath(path)`
* `getInt(path)` 
* `getLong(path)`
* `getDouble(path)`
* `getBoolean(path)`
* `getString(path)`
* `getStringList(path)` gets a list of strings
* `getConfig(path)` gets a sub-config.
* `getMap(path)` gets a map which is a sub-config.
* `getConfigList(path)` gets a list of configs at the location specified.
* `getIntList(path)` 
* `getLongList(path)`
* `getDoubleList(path)`
* `getBooleanList(path)`


`getMap` works with JavaScript objects. `getStringList` and `getConfigList` works
with JavaScript array of string and a JavaScript array of JavaScript objects. 

Not you get an exception if the `path` requested is not found. 
Use `hasPath(path)` if you think the config path might be missing. 

Here is the full interface.

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

##### 

Konf can reads list of numbers. 

* `getIntList` reads list of ints
* `getLongList` reads list of longs
* `getDoubleList` reads list of doubles
* `getFloatList` reads list of floats

See documentation [list of number configuration](https://github.com/advantageous/konf/wiki/Working-with-lists-of-ints,-longs,-doubles,) 
for more details.


#### Thanks

If you like our configuration project, please try our 
[Reactive Java project](https://github.com/advantageous/reakt)
or our [Actor based microservices lib](https://github.com/advantageous/qbit).

