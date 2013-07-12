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

/**
 * @author Decebal Suiu
 */
public class BooleanConverter implements Converter<Boolean> {

    public static final BooleanConverter TRUE_FALSE = new BooleanConverter("true", "false", false);
    public static final BooleanConverter YES_NO = new BooleanConverter("yes", "no", false);
    public static final BooleanConverter BINARY = new BooleanConverter("1", "0", true);

    private String positive;
    private String negative;
    private boolean caseSensitive;

    public BooleanConverter() {
        this("true", "false", false);
    }

    public BooleanConverter(String positive, String negative, boolean caseSensitive) {
        this.positive = positive;
        this.negative = negative;
        this.caseSensitive = caseSensitive;
    }
    
	@Override
	public Boolean decode(String text) {
		if (caseSensitive) {
            return positive.equals(text) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return positive.equalsIgnoreCase(text) ? Boolean.TRUE : Boolean.FALSE;
        }
	}

}
