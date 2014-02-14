/*
 * Copyright 2014 Decebal Suiu
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Decebal Suiu
 */
public class RegexTester {

	// change this
	private static final String regex = Download.PATTERN;
	
	// change this
	private static final String line = "[webapp 2013/06/13 21:27:00] - <$> 109.172.60.175, /download/nextreports-setup-6.1.exe, bfec46109eba9245201fd1e03aef7ebc";	
		
	public static void main(String[] args) {
		System.out.println("regex = " + regex);
		System.out.println("line = " + line);
		Matcher matcher = Pattern.compile(regex).matcher(line);
		System.out.println("matches = " + matcher.matches());
		if (matcher.matches()) {
			int groupCount = matcher.groupCount();
			System.out.println("groupCount = " + groupCount);
			for (int i = 1 ; i <= groupCount; i++) {
				System.out.println("group[" + i + "] = " + matcher.group(i));
			}
		}
	}

}
