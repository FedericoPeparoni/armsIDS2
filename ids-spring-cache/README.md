# IDS Spring Cache Library Extension

IDS Spring Cache Library Extension offers additional functionality on top of 
Cache Abstraction in the Spring Framework. The main additional functionality 
revolves around handling caches on exceptions and updating cache stores at 
regular intervals.

&copy; 2018 IDS North America

## Installation

You can download the library as Maven dependency from the ids repository, 
http://maven.idscorporation.ca/archiva/repository/ids:

```
<dependency>
    <groupId>ca.ids</groupId>
    <artifactId>ids-spring-cache</artifactId>
    <version>x.x.x</version>
</dependency>
```

This project uses [SemVer][3] for versioning. For the versions available, see 
the [tags on this repository][4].

## Usage

In order to use these featuers, you must enable Spring Caching and configure 
necessary apsect and manager classes. See the [Configuration][1] section of the 
wiki for more information.

### Cacheable On Exception

Once you have a proper configuration setup within your project, you can enable 
exception caching with the following annotation examples. See 
~~[Cacheable On Exception][2]~~ section of the wiki for more details.

```java
...

// Spring Framework's cache config, see spring.io
@CacheConfig(cacheNames = "MyService")
// Define exception(s) to pull from cache when thrown.
@CacheOnExceptionConfig(exceptions = { JDBCConnectionException.class })
public class MyService() {

...

// Define exception(s) to exclude from cache when thrown (inverse).
@CacheOnExceptionConfig(exceptions = { JDBCConnectionException.class }, exclude = true)
public class MyService() {

...

// Defines methods to cache when successful. When configured
// exception(s) thrown, attempts to pull form previous cached result.
@CacheableOnException
public MyObject find(int id) {

...

// Defines methods to retry when configured exception(s) thrown.
// If return value expected, throws CacheRetryException.
@CacheableOnException(retry = true)
public void save(MyObject value) {

...

// Defines method to exclude exceptions (inverse) when 
```

### Cacheable Exception Metadata

Within a `@CacheableOnException(retry = true)` annotated method, you can throw a custom exception
`new CacheableRuntimeException(Object[] metadata, Throwable cause)` to provide
more information regarding actionable and dynamic information such as SQL statements the method is
trying to run.

For example, the following code snippet will catch any `DataAccessException` thrown and pass the 
SQL statement as a string.


```java
@CacheableOnException(retry = true)
public void save(MyObject value) {
    
    ...
    
    } catch (SomeException ex) {
        throw new CacheableRuntimeException( new String[] { 
            String.format("INSERT INTO my_mock_table (my_mock_column) VALUES (%s)", value.myMockProperty)
        }, ex);
    }
}
```

## Flowcharts

Below are the flowchart diagrams depicting the workflow for cacheable exceptions.

![CacheableOnException()][flowchart_read]

![CacheableOnException(retry = true)][flowchart_write]

[1]: https://git.idscorporation.ca/ids/ids-spring-cache/wikis/configuration
[2]: #usage
[3]: http://semver.org/
[4]: https://git.idscorporation.ca/ids/ids-spring-cache/tags
[flowchart_read]: img/CacheableOnException_Read.png
[flowchart_write]: img/CacheableOnException_Write.png
