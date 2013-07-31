Flat File to Java (FF2J)
=====================
Simple plain text flat file to java objects converter using annotations.

Current build status: [![Build Status](https://buildhive.cloudbees.com/job/decebals/job/ff2j/badge/icon)](https://buildhive.cloudbees.com/job/decebals/job/ff2j/)

Features/Benefits
-------------------
With FF2J you can easily convert/transform a plain text flat file in a collection of java objects. 
FF2J is an open source (Apache license) tiny (around 15KB) flat file to POJOs converter, with zero dependencies and a quick learning curve.

No XML, only Java.

FF2J can handles many entity types (POJOs) from a single flat file (for example you can have a log file that can contains entites like Download, Evaluation, License).

Components
-------------------
- **RegexEntity** is an annotation that can be added on any POJO.
- **RegexField** is an annotation that can be added on any field of classes annotated with RegexEntity.
- **Converter** is an interface implemented by all converters, that convert a text (String) in a typed value.
- **EntityHandler** is an interface to be implemented for processing entities. For example you can write a DownloadHandler that writes
all download objects in a database.
- **AbstractEntityHandler** is a simple EntityHandler that does nothing in beforeFirstEntity() and afterLastEntity().
- **NoEntityHandler** is an interface to be implemented for processing no entity line.
- **FF2J** is the main class.
- **FF2J.Statistics** is a holder class for FF2J's statistics.

Artifacts
-------------------
- FF2J `ff2j` (jar)
- FF2J Validation  `ff2j-validation` (jar)
- FF2J Demo `ff2j-demo` (executable jar)

Using Maven
-------------------
In your pom.xml you must define the dependencies to FF2J artifacts with:

```xml
<dependency>
    <groupId>ro.fortsoft.ff2j</groupId>
    <artifactId>ff2j</artifactId>
    <version>${ff2j.version}</version>
</dependency>    
```

where ${ff2j.version} is the last ff2j version.

You may want to check for the latest released version using [Maven Search](http://search.maven.org/#search%7Cga%7C1%7Cff2j)

How to use
-------------------
You can convert a flat file's lines in java objects with a single line:

	// the important line
	FF2J.Statistics statistics = new FF2J()
		.map(Download.class)
		.addEntityHandler(new DownloadHandler())
		.skipLines(5)
		.parse(new InputStreamReader(input));

	// display some statistics
	System.out.println(statistics);

On a big (45MB) log file I retrieves this statistics:

    Parsing file:/stuff/work/ff2j/demo/target/classes/winstone-2-big.log...
    FF2J Statistics:
	    startLineNumber = 1000
	    endLineNumber = 865628
        elapsedTime = 0:0:1.719
	    entitiesCounter = {class ro.fortsoft.ff2j.demo.Download=26402}

where elapsedTime is formated as HH:mm:ss.SSS

You can save endLineNumber in a file or in a database and on next running you can initiate skipLines with that value.

And now the story :)
		
Scenario: I have a `DownloadServlet` that for each download request writes in the container's log a line in the format

	[webapp 2008/10/06 16:12:16] - 192.168.12.124, /download/next-reports-setup-1.7-jre.exe, f13dfc7fe609480297a0b15d611676b4
	
What I want is to see some statistics about downloads.
For this reason I created a simple _POJO_ class with the name `Download` having some properties.

    @RegexEntity(pattern = "PATTERN")
    public class Download {
    
    	// [webapp 2008/10/06 16:12:16] - 192.168.12.124, /download/next-reports-setup-1.7-jre.exe, f13dfc7fe609480297a0b15d611676b4	
   		public static final String PATTERN = "\\[webapp\\s"
			+ "(20[0-1][0-9]/\\d{2}/\\d{2}\\s\\d{2}:\\d{2}:\\d{2})" // date
			+ "\\]\\s-\\s<\\$>\\s"
			+ "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})" // ip
			+ ",\\s/download/"
			+ "([^,]*)" // file 
			+ ".*";

    
    	@RegexField(group = 1, converter = MyDateConverter.class)
    	private Date date;
        
    	@RegexField(group = 3)
    	private String ip;
    
    	@RegexField(group = 4)
    	private String file;
  
    	// getters and setters
    	
    }

I added annotation **@RegexEntity**(pattern = "PATTERN") to class `Download`. The **@RegexEntity** annotation may have a parameter
named pattern which represents the constant name that returns the regex string. The default value is **PATTERN** (in `Download` class 
I added pattern value for transparency).
This annotation is informing FF2J that all flat file's lines that respect the pattern will be transformed in Download objects.

The second step is to specify how FF2J will transform text fragments from flat file's lines in `Download`'s properties.
For this purpose I used **@RegexField** annotation. This annotation takes a mandatory parameter named group that represents the group index from 
the java's regex Matcher object and an optional parameter named converter that will be used by FF2J to convert the text fragment in property's value.

The third step is to create a `DownloadHandler` for handling `Download` objects.
 
    public class DownloadHandler extends AbstractEntityHandler<Download> {

	    @Override
	    public void handleEntity(Download entity) {
		    // only display the entity to System.out
		    System.out.println(entity);		
	    }

    }

where AbstractEntityHandler is a simple EntityHandler that does nothing in beforeFirstEntity() and afterLastEntity().

The signature for the EntityHandler interface is:

    public interface EntityHandler<T> {
	
	    public void beforeFirstEntity();
	
	    public void handleEntity(T entity);

	    public void afterLastEntity();
	
    }

The methods beforeFirstEntity() and afterLastEntity() are callback methods. FF2J will call these methods one time at the start/end of flat file parsing.
For example you can start a database transaction, clear an entity table in beforeFirstEntity() and commit the database transaction in afterLastEntity().

Converters
----------------
A **Converter** is used by FF2J to transform a text fragment into a POJO property's value. If conversion cannot be performed successfully 
than throw a ConversionException (extends RuntimeException).
FF2J comes with builtin converters for all primitive values ( _Boolean_, _Byte_, _Short_, ...) and Date.  
FF2J also allows you to register new general converters (for each field's type) or you can register
a converter only for a particular field.

    public class MyDateConverter extends DateConverter {

	    public MyDateConverter() {
		    super("yyyy/MM/dd hh:mm:ss");
	    }
	
    }

For example `MyDateConverter` is used in `Download` class by FF2J to transform the text fragment in _Date_ object.
If you want to use MyDateConverter for all POJO fields with type Date you can do it with:
    
    new FF2J()
        ...
        registerConverter(new MyDateConverter();

Validations
----------------
It's extremely simple to add validation support on the entity handler. For this purpose you can use ff2j-validation module.   
FF2J Validation come with ValidEntityHandler class and two little dependencies: [Bean Validation](http://beanvalidation.org/1.0/spec/) (JSR 303) and [OVal](http://oval.sourceforge.net/). I choose OVal because it's lightweight (around 300K) and come with no dependencies. 

In your pom.xml you must define the dependency to FF2J Validation artifact with:

```xml
<dependency>
    <groupId>ro.fortsoft.ff2j</groupId>
    <artifactId>ff2j-validation</artifactId>
    <version>${ff2j.version}</version>
</dependency>    
```

where ${ff2j.version} is the last ff2j version.

Validation is supported by constraints in the form of annotations placed on a field, method, or class of a POJO.

    @RegexEntity(pattern = "PATTERN")
    public class Download {

        ...
	
	    @RegexField(group = 1, converter = MyDateConverter.class)
	    @NotNull
	    private Date date;
	
	    @RegexField(group = 2)
	    @NotNull
	    private String ip;
	
	    @RegexField(group = 3)
	    @NotNull
	    private String file;

        ...

    }

In above snippet I want date, ip and file should be not null. For this reason I added @javax.validation.constraints.NotNull on these fields.

To validate entities you can use the ValidEntityHandler class.

    public class DownloadHandler implements ValidEntityHandler<Download> {

   		@Override
		public void handleValidEntity(Download entity) {
            // do something
        }

        @Override
        public void handleInvalidEntity(Download entity, List<ConstraintViolation> violations) {
            // do something
        }

    }

If the entity is valid than method handleValidEntity() is called else method handleInvalidEntity() is called.

That it's all about validations :)

Import scenario
-------------------
A friend of mine tell me that he uses FF2J to import entities from a csv file in his application (spring with hibernate). He decorate him entity with few FF2J annotations and implement a NoEntityHadler to summarize what it is wrong in the csv file. He is happy because he can use the same hibernate entities with validation support to import from csv files.

Demo
-------------------
I have a tiny demo application that parse a log file produced by winstone (http://winstone.sourceforge.net/). The demo application is in demo package.
To run the demo application use:  
 
    mvn
    java -jar demo/target/demo-jar-with-dependencies.jar

Mailing list
--------------
Much of the conversation between developers and users is managed through [mailing list] (http://groups.google.com/group/ff2j).

License
--------------
Copyright 2013 Decebal Suiu
 
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
the License. You may obtain a copy of the License in the LICENSE file, or at:
 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
