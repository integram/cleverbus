# How to build CleverBus?

Building CleverBus is very easy:

1.  download sources from [GitHub](https://github.com/integram/cleverbus) or clone [Git repository](https://github.com/integram/cleverbus.git)
2.  call *mvn package*
3.  deploy *cleverbus.war* (module *web-admin*) to application server and start it

Must be called <i>mvn package</i> and not <i>mvn compile</i> because there are dependencies in resources between Maven modules which use <a href='http://maven.apache.org/plugins/maven-dependency-plugin'><i>maven-dependency-plugin</i></a> with <i>unpack-dependencies</i> goal.

You can continue with [starting new integration project](How-to-start-new-project) or with [writing new integration routes](How-to-write-routes).
