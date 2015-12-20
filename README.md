# swagger2html

Converts swagger jsons to html documents which are readable by everybody.

Swagger-Codegen already has a tool to generate html documents. However, it seems ugly and to be in lack of information.  

!(sample/pegstore-by-swagger-cg.png)

This project produces a neat one: 

!(sample/petstore-by-s2h.png)

Check the full html [here](sample/petstore-by-s2h.html). 

# How to run

## Run as a command line tool

````
mvn package 
cp target 
unzip target/swagger2html-some-version-jarset.zip -d /path/to/your/dir

# Go to the direction of extraction and you will see an executable file. Run it like, 

./s2h.sh http://petstore.swagger.io/v2/swagger.json /path/to/your/html/doc/file

````

## Run it inside an web app
Just call class org.swagger2html.Swagger2Html . 

Note: this project has not been deployed to any public maven repository yet. So you have to "mvn install" this project.
 




