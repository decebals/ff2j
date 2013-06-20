/*
 * Copyright 2013 Decebal Suiu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
 * the License. You may obtain a copy of the License in the LICENSE file, or at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ro.fortsoft.log2j.demo;

import java.io.InputStream;
import java.io.InputStreamReader;

import ro.fortsoft.log2j.Log2j;

/**
 * @author Decebal Suiu
 */
public class Log2jDemo {

	public static void main(String[] args) {
		String file = "winstone.log";
		
		InputStream input = Log2jDemo.class.getResourceAsStream("/winstone.log");
		try {
			// store de start time
			long time = System.currentTimeMillis();
			System.out.println("Parsing file " + file + "...");
			
			// the important line
			new Log2j().map(Download.class).addEntityHandler(new DownloadHandler()).parse(new InputStreamReader(input));
			
			// display of parse time
			time = System.currentTimeMillis() - time;
			System.out.println("Parse in " + time + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
