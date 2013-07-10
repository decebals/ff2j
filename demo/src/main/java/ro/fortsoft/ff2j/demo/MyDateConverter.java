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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ro.fortsoft.ff2j.converter.Converter;

/**
 * @author Decebal Suiu
 */
public class MyDateConverter implements Converter<Date> {

	@Override
	public Date convert(String text) {
		try {
			return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
