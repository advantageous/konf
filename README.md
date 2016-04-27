# Konf - Typed Java config system 
Java configuration library similar in concept to TypeSafe but uses full 
JavaScript for configuration.

Uses JavaScript/JSON as config for Java. 

You can use full JavaScript for configuration.


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
}

```


The `getX` methods work like you would expect. Given this config file.

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

#### Reading basic types

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

#### Reading a nested config

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
    
#### Reading nested properties with dot notation


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

    @Test
    public void testReadListOfConfig() throws Exception {
        final List<Config> employees = config.getConfigList("employees");
        assertEquals("Geoff", employees.get(0).getString("name"));
        assertEquals("123", employees.get(0).getString("id"));
    }
 ```