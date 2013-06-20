Log2j
=====================
Simple log file to java objects converter using annotation.

Components
-------------------
- **RegexEntity** is an annotation that can be added on any POJO.
- **RegexField** is an annotation that can be added on any field of classes annotated with RegexEntity.

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
// TODO
    
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
