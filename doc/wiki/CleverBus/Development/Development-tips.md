# Development tips

## Restrict Spring bean inicialization 

If there are many Spring beans with route definitions then startup time for loading ESB application can be quite long. This functionality enables to **include** only those Spring beans which should be initialized or **exclude** those Spring beans which we don't want to initialize.

It can be handy during development because it's possible via system properties to restrict set of Spring beans (=Camel routes) which will be initialized.

More info in [Configuration](../User-guide/Configuration).

## Calling services between Spring contexts

Since version 0.4

Common scenario for solving: [there is root Spring context root and two child contexts for admin GUI (web MVC) and for web services (spring WS)](../Architecture/Maven-and-Spring). And we need to call services in WS context from admin GUI controllers.

There is package of handful classes for solving this problem, see *com.cleverlance.cleverbus.core.common.contextcall.ContextCall* as main interface for use.

## Routes initialization test 

Since version 0.4

If you want to test that all routes will be successfully initialized in Camel then use the following test. This test mainly checks that all routes have unique ID and URI.

``` java
/**
 * Test that verifies if all Camel routes are correctly initialized - if there are unique route IDs and unique URIs.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class RoutesInitTest extends AbstractModulesDbTest {
    @BeforeClass
    public static void setInitAllRoutes() {
        setInitAllRoutes(true);
    }
    @Test
    public void testInit() {
        // nothing to do - if all routes are successfully initialized then test is OK
        System.out.println("All routes were successfully initialized");
    }
}
```
