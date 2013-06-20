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
package ro.fortsoft.log2j.converter;

import java.lang.reflect.ParameterizedType;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author Decebal Suiu
 */
public class ConverterRegistry {

	private static ConverterRegistry instance = new ConverterRegistry();

    private Map<Class<?>, Converter<?>> converters = new Hashtable<Class<?>, Converter<?>>();
    
    private ConverterRegistry() {
    	registerDefaultConverters();
    }
    
    public static ConverterRegistry getInstance() {
        return instance;
    }

    protected void registerDefaultConverters() {
    	register(new BooleanConverter());
    	register(new ByteConverter());
    	register(new ShortConverter());
        register(new IntegerConverter());
        register(new LongConverter());
        register(new FloatConverter());
        register(new DoubleConverter());
    }

    /**
     * Register a converter.
     */
    public void register(Converter<?> converter) {
        if (converter != null) {
    		ParameterizedType superclass = (ParameterizedType) converter.getClass().getGenericInterfaces()[0];
			Class<?> targetClass = (Class<?>) superclass.getActualTypeArguments()[0];
            converters.put(targetClass, converter);
        }
    }

    /**
     * Lookup a converter for this target class.
     */
    public Converter<?> lookup(Class<?> target) {
        return converters.get(target);
    }

    /**
     * Unregister a registerd converter.
     */
    public void unregister(Converter<?> converter) {
    	if (converter != null) {
	    	Set<Class<?>> keys = converters.keySet();
	    	for (Class<?> key : keys) {
	    		if (converters.get(key).equals(converter)) {
	    			converters.remove(key);
	    		}
	    	}
    	}
    }

}
