1. In order to compile the project, just run the below maven command from the root of the project. Make sure to turn off the tests.
   mvn clean install -DskipTests
2. There are some Java8 API;s being used in the application which needs JDK1.8 on the classpath. Also, I tested the application with Tomcat 8.0.12 (since Tomcat 7 is not very friendly with Java8).
3. Deploy the war to any web container (especially Tomcat 8.0.12).

4. The context root of the web application is "rjm".
5. Once deployed the URL of the application for all the functionality will be 
    http://<HOST_NAME>:<PORT_NUM>/rjm/tables/<table|key|search?q=key>
 

Design:
1. The application is designed to persist the data in the underlying files. The location of the db files in *nix based system would be /rjm/byodb.
2. Data for GET,POST,PUT are cached in the system, every request would first query the in memory cache for the data, in case of a cache miss, the underlying file is read and the cache is populated. This would result in faster reads and increase the performance of the system by doing less number of i/o's.
3. The above design is memory intensive, since everything is stored in memory. This might cause problems when the data in the file grows large (> MBS). IN such scenarios,we can stream the data in real time, rather than storing the entire data in memory.

Assumptions :
1. The data passed in the REQUEST body of the POST method should always be in the form of a Json Map. for instance {"key":"value"} . It can be of type array like [{"key":"value"}]



