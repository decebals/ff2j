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
package ro.fortsoft.ff2j;

import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ro.fortsoft.ff2j.converter.Converter;
import ro.fortsoft.ff2j.converter.ConverterRegistry;

/**
 * @author Decebal Suiu
 */
public class FF2J {

    private int skipLines;
	private Mapper mapper;
	private Set<EntityHandler<?>> entityHandlers;
	private Map<Class<?>, EntityHandler<?>> entityHandlersCache;
	
	public FF2J() {
		mapper = new Mapper();
		entityHandlers = new LinkedHashSet<EntityHandler<?>>();
	}
	
    public int getSkipLines() {
		return skipLines;
	}

	public FF2J setSkipLines(int skipLines) {
		this.skipLines = skipLines;
		
		return this;
	}

	public FF2J map(Class<?> entityClass) {
		if (!mapper.isMapped(entityClass)) {
			mapper.addMappedClass(entityClass);
		}
		
		return this;
	}

	public Set<EntityHandler<?>> getEntityHandlers() {
		return entityHandlers;
	}

	public FF2J setEntityHandlers(Set<EntityHandler<?>> entityHandlers) {
		this.entityHandlers = entityHandlers;
		
		return this;
	}
	
	public FF2J addEntityHandler(EntityHandler<?> entityHandler) {
		if (entityHandler != null) {
			entityHandlers.add(entityHandler);
		}
		
		return this;
	}
	
	/**
	 * Register a custome general converter.
	 * 
	 * @param converter
	 * @return
	 */
	public FF2J registerConverter(Converter<?> converter) {
		ConverterRegistry.getInstance().register(converter);
		
		return this;
	}

	/**
     * Process each line of the file and call an entity handler if that line can be mapped to an entity.
     *
     * @param input will not be closed by the reader
     */
    public void parse(Reader input) throws Exception {
    	// pre parse
    	createEntityHandlersCache();
    	for (EntityHandler<?> entityHandler : entityHandlers) {
    		entityHandler.beforeFirstEntity();
    	}
    	
    	// parse
        BufferedReader reader = new BufferedReader(input);
        String lineText = null;
        int lineNumber = 0;
        while ((lineText = reader.readLine()) != null) {
        	lineNumber++;
        	if (lineNumber > skipLines) {
        		onFileLine(lineNumber, lineText);
        	}
        }        
        
        // post parse
    	for (EntityHandler<?> entityHandler : entityHandlers) {
    		entityHandler.afterLastEntity();
    	}
	}
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void onFileLine(int lineNumber, String lineText) throws Exception {
    	Object entity = mapper.mapEntity(lineText);
    	if (entity != null) {
    		EntityHandler entityHandler = entityHandlersCache.get(entity.getClass());
    		if (entityHandler != null) {
    			entityHandler.handleEntity(entity);
    		}
    	}
    }

    private void createEntityHandlersCache() {
		entityHandlersCache = new HashMap<Class<?>, EntityHandler<?>>();
    	for (EntityHandler<?> entityHandler : entityHandlers) {
    		ParameterizedType superclass = (ParameterizedType) entityHandler.getClass().getGenericInterfaces()[0];
			Class<?> entityClass = (Class<?>) superclass.getActualTypeArguments()[0];
	   		entityHandlersCache.put(entityClass, entityHandler);
    	}
    }
    
}
