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
package ro.fortsoft.ff2j.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Decebal Suiu
 */
public class DateConverter implements Converter<Date> {

	public static final String DEFAULT_PATTERN = "dd/MM/yyyy";
	
    private SimpleDateFormat dateFormat;

    public DateConverter() {
        this(DEFAULT_PATTERN);
	}

    public DateConverter(String pattern) {
        this.dateFormat = new SimpleDateFormat(pattern);
    }

	@Override
	public Date decode(String text) {
		try {
			return dateFormat.parse(text);
		} catch (ParseException e) {
			throw new ConversionException(e);
		}
	}

}
