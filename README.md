# swagger2html

Converts swagger jsons to html documents which are readable by everybody.
------

__Swagger-Codegen already has a tool to generate html documents. However, it seems ugly and to be in lack of information.__

![petstore-by-swagger-cg](sample/petstore-by-swagger-cg.png)

------

__This project produces a neat one:__

![petstore-by-s2h](sample/petstore-by-s2h.png)

Check the full html [here](https://rawgit.com/chenjianjx/swagger2html/master/sample/petstore-by-s2h.html). 


# How to run

## Run as a command line tool

````
mvn package 
cd target 
unzip target/swagger2html-some-version-jarset.zip -d /path/to/your/dir

# Go to the direction of extraction and you will see an executable file. Run it like, 

./s2h.sh http://petstore.swagger.io/v2/swagger.json /path/to/your/html/doc/file

````

## Run it inside your application

In your pom.xml, add the following: 

````
	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
		...
	</repositories>


	<dependencies>
		<dependency>
			<groupId>com.github.chenjianjx</groupId>
			<artifactId>swagger2html</artifactId>
			<version>1.0.6</version>
		</dependency>
		...
	</dependencies>	

````



```` 
	org.swagger2html.Swagger2Html.toHtml(url, output); 
````  




