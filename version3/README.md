### Pharse III of Word Count:

Use Apache Maven + Jersey to allow for REST API access, but somehow due to a version conflict problem I cannot get it running on Tomcat, instead I tried to deploy it on Google App Engine, but it didn't run properly, still need to figure out why.

Steps:

1. Assume you have Maven installed as well as Google Cloud SDK. 

Determine whether Maven is installed and which version you have, by invoking the following command:

<code> mvn -v </code>

Install Google Cloud SDK by https://cloud.google.com/sdk/docs/

Install the Cloud SDK app-engine-java component:

<code> gcloud components install app-engine-java </code>

2. Compile and build the project using Maven

	2.1 Change to the main directory for the project

	2.2 Invoke Maven:

	<code> mvn package </code>

3. Running and testing the app

	3.1 During the development phase, you can run and test your app at any time in the development server by invoking the Jetty Maven plugin.

	3.2 Change directory to the top level of the project (for example, to myapp), and run your app by invoking Maven:

	<code> mvn jetty:run </code>

	3.3 Wait for the server to start and use your browser to visit http://localhost:8080/ to access the app.

4. Deploying the app

The app engine plugin is already added to the project, deploy the application by running:

<code> mvn appengine:deploy </code>