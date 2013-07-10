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
package ro.fortsoft.ff2j.demo;

import java.io.InputStream;
import java.io.InputStreamReader;

import ro.fortsoft.ff2j.FF2J;

/**
 * @author Decebal Suiu
 */
public class FF2JDemo {

	public static void main(String[] args) {
		String resourceName = "/winstone.log";
		InputStream input = FF2JDemo.class.getResourceAsStream(resourceName);
		try {
			System.out.println("Parsing " + FF2JDemo.class.getResource(resourceName) + "...");
			
			// the important line
			FF2J.Statistics statistics = new FF2J()
				.map(Download.class)
				.addEntityHandler(new DownloadHandler())
				.skipLines(5)
				.parse(new InputStreamReader(input));
			
			// display some statistics
			System.out.println(statistics);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
