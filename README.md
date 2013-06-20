Log2j
=====================
Simple log file to java objects converter using annotation.

Components
-------------------
- **RegexEntity** is an annotation that can be added on any POJO.
- **RegexField** is an annotation that can be added on any field of classes annotated with RegexEntity.
- **Converter** is an interface implemented by all converters, that convert a text (String) in a typed value.
- **EntityHandler** is an interface to be implemented for processing entities.

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
    		+ "(20[0-1][0-9]/\\d{2}/\\d{2})" // date
    		+ "\\s"
    		+ "(\\d{2}:\\d{2}:\\d{2})" // time
    		+ "\\]\\s-\\s<\\$>\\s"
    		+ "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})" // ip
    		+ ",\\s/download/"
    		+ "([^,]*)" // file 
    		+ ".*";
    
    	@RegexField(group = 1, converter = MyDateConverter.class)
    	private Date date;
    
    	@RegexField(group = 2)
    	private String time;
    
    	@RegexField(group = 3)
    	private String ip;
    
    	@RegexField(group = 4)
    	private String file;
    
    	// other properties
    	private String session;
    	private String country;
    	private String city;
    	private String agent;
    	private String referer;
    
    	// getters and setters
    	
    }

I added annotation **@RegexEntity**(pattern = "PATTERN") to class `Download`. The **@Regex** annotation may have a parameter
named pattern which represents the constant name that returns the regex string. The default value is **PATTERN** (in `Download` class 
I added pattern value for transparency).
This annotation is informing Log2j that all log's lines that respect the pattern will be transformed in Download objects.

The second step is to specify how Log2j will transform text fragments from log's lines in `Download`'s properties.
For this purpose I used **@RegexField** annotation. This annotation takes a mandatory parameter named group that represents the group index from 
the java's regex Matcher object and an optional parameter named converter that will be used by Log2j to convert the text fragment in property's value.

The third step is to create a `DownloadHandler` for handling `Download` objects.
 
    public class DownloadHandler implements EntityHandler<Download> {
    
    	@Override
    	public void handleEntity(Download entity) {
    		// only display the entity 
    		System.out.println(entity);
    	}
    
    }

In the example above my `DownloadHandler` only prints all download objects to _System.out_.

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

Demo
-------------------
I have a tiny demo application that parse a log file produced by winstone (http://winstone.sourceforge.net/). The demo application is in demo package.
To run the demo application use:  
 
    mvn
    java -jar demo/target/demo-jar-with-dependencies.jar


License
--------------
Copyright 2013 Decebal Suiu
 
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
the License. You may obtain a copy of the License in the LICENSE file, or at:
 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
