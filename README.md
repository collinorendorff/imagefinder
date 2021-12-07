# ImageFinder
Java/HTML web crawler for parsing images on user-provided URL


The imagefinder servlet can be found in "src/main/java/com/imagefinder/ImageFinder.java"

The main local landing page for this project can be found in "src/main/webapp/index.html"

**Uses Maven 3.5+ and Java 8 for packaging, building, and running the project, as well as setting up the localhost page with 
a Jetty server**

***SETUP***
To start, open a terminal window and navigate to wherever you unzipped to the root directory `imagefinder`. To build the project, run the command:

>`mvn package`

If all goes well you should see some lines that ends with "BUILD SUCCESS". When you build your project, maven should build it in the `target` directory. To clear this, run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

You should see a line at the bottom that says "Started Jetty Server". Now, if you enter `localhost:8080` into your browser, you should see the `index.html` welcome page! If all has gone well to this point, you're ready to enter URLs for crawling!
