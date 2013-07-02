Log2j
=====================
Simple log file to java objects converter using annotation.

Features/Benefits
-------------------
With Log2j you can easily convert/transform a log file in a collection of java objects. 
Log2j is an open source (Apache license) tiny (around 15KB) log file to POJOs converter, with zero dependencies and a quick learning curve.

No XML, only Java.

Components
-------------------
- **RegexEntity** is an annotation that can be added on any POJO.
- **RegexField** is an annotation that can be added on any field of classes annotated with RegexEntity.
- **Converter** is an interface implemented by all converters, that convert a text (String) in a typed value.
- **EntityHandler** is an interface to be implemented for processing entities. For example you can write a DownloadHandler that writes
all download objects in a database.

Artifacts
-------------------
- Log2j `log2j` (jar)
- Log2j Demo `log2j-demo` (executable jar)

Using Maven
-------------------
In your pom.xml you must define the dependencies to Log2j artifacts with:

```xml
<dependency>
    <groupId>ro.fortsoft.log2j</groupId>
    <artifactId>log2j</artifactId>
    <version>${log2j.version}</version>
</dependency>    
```

where ${log2j.version} is the last log2j version.

You may want to check for the latest released version using [Maven Search](http://search.maven.org/#search%7Cga%7C1%7Clog2j)

How to use
-------------------
You can convert a log's lines in java objects with a single line:

    new Log2j()
		.map(Download.class)
		.addEntityHandler(new DownloadHandler())
		.parse(new InputStreamReader(input));

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
This annotation is informing Log2j that all log's lines that respect the pattern will be transformed in Download objects.

The second step is to specify how Log2j will transform text fragments from log's lines in `Download`'s properties.
For this purpose I used **@RegexField** annotation. This annotation takes a mandatory parameter named group that represents the group index from 
the java's regex Matcher object and an optional parameter named converter that will be used by Log2j to convert the text fragment in property's value.

The third step is to create a `DownloadHandler` for handling `Download` objects.
 
    public class DownloadHandler implements EntityHandler<Download> {
    
	    private int count;
	
	    @Override
	    public void beforeFirstEntity() {
		    count = 0;
	    }

	    @Override
	    public void handleEntity(Download entity) {
		    count++;
		    // only display the entity 
		    System.out.println(entity);		
	    }

	    @Override
	    public void afterLastEntity() {
		    System.out.println("Handled " + count + " Download entities");
	    }
    
    }

The methods beforeFirstEntity() and afterLastEntity() are callback methods. Log2j will call these methods one time at the start/end of log parsing.
For example you can start a database transaction, clear an entity table in beforeFirstEntity() and commit the database transaction in afterLastEntity().

In the example above my `DownloadHandler` init count variable to zero in beforeFirstEntity(), prints all download objects to _System.out_ in handleEntity() and prints a count with download entities in afterLastEntity().

Converters
----------------

A **Converter** is used by Log2j to transform a text fragment into a POJO property's value.  
Log2j comes with builtin converters for all primitive values ( _Boolean_, _Byte_, _Short_, ...).  
Log2j also allows you to register new general converters (for each field's type) or you can register
a converter only for a particular field.

    public class MyDateConverter implements Converter<Date> {
    
    	@Override
    	public Date convert(String text) {
    		try {
    			return new SimpleDateFormat("yyyy/MM/dd").parse(text);
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    
    		return null;
    	}
    
    }

For example `MyDateConverter` is used in `Download` class by Log2j to transform the text fragment in _Date_ object.
If you want to use MyDateConverter for all POJO fields with type Date you can do it with:
    
    new Log2j()
        ...
        registerConverter(new MyDateConverter();

Validations
----------------

You can easily add validation in your EntityHandler using java [Bean Validation](http://beanvalidation.org/1.0/spec/) (JSR 303).
With some annotation you can have an validated object in handleEntity().

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

To validate entities I modified a little bit the handleEntity method of DownloadHandler class.

    public class DownloadHandler implements EntityHandler<Download> {

        private Validator validator;

        public DownloadHandler() {
            validator = new Validator(new AnnotationsConfigurer(), new BeanValidationAnnotationsConfigurer());
        }

   		@Override
		public void handleEntity(Download entity) {
			// check for validation
			List<ConstraintViolation> violations = validator.validate(entity);
			if (violations.size() > 0) {
				System.out.println("Entity \"" + entity.getClass().getSimpleName() + " - " + entity + "\" is invalid");
				System.out.println("Violations: " + violations);
				return;
			}

            ...
        }
			
    }
 
For downloads validation I used [OVal](http://oval.sourceforge.net/) as implementation of Bean Validation specifications. I choose OVal because it is lightweight (around 300K) and come with no dependencies (for my requirements). 

Demo
-------------------
I have a tiny demo application that parse a log file produced by winstone (http://winstone.sourceforge.net/). The demo application is in demo package.
To run the demo application use:  
 
    mvn
    java -jar demo/target/demo-jar-with-dependencies.jar

Mailing list
--------------

Much of the conversation between developers and users is managed through [mailing list] (http://groups.google.com/group/log2j).

License
--------------
Copyright 2013 Decebal Suiu
 
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
the License. You may obtain a copy of the License in the LICENSE file, or at:
 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
