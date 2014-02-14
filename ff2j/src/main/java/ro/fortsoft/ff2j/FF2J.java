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
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ro.fortsoft.ff2j.converter.Converter;
import ro.fortsoft.ff2j.converter.ConverterRegistry;

/**
 * @author Decebal Suiu
 */
public class FF2J {

    private long skipLines;
	private Mapper mapper;
	private Set<EntityHandler<?>> entityHandlers;
	private NoEntityHandler noEntityHandler;
	private ProgressListener progressListener;
	private Map<Class<?>, EntityHandler<?>> entityHandlersCache;
	private Statistics statistics;
	private Exception error;
	
	public FF2J() {
		mapper = new Mapper();
		entityHandlers = new LinkedHashSet<EntityHandler<?>>();
		statistics = new Statistics();
	}
	
    /**
     * Skips several lines in the file.
     * 
     * @param skipLines Specifies the number of lines to skip.
     * @return
     */
	public FF2J skipLines(long skipLines) {
		this.skipLines = skipLines;
		statistics.startLineNumber = skipLines;
		
		return this;
	}

	/**
	 * Add a class that this instance can map to RegexEntities. This method will validate the class, and all mapped
     * RegexEntity implementations referenced from this class.
     * 
	 * @param entityClass
	 * @return
	 */
	public FF2J map(Class<?> entityClass) {
		if (!mapper.isMapped(entityClass)) {
			mapper.addMappedClass(entityClass);
		}
		
		return this;
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
	
	public FF2J setNoEntityHandler(NoEntityHandler noEntityHandler) {
		this.noEntityHandler = noEntityHandler;
		
		return this;
	}

	public FF2J setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
		
		return this;
	}

	/**
	 * Register a custom general converter.
	 * 
	 * @param converter
	 * @return
	 */
	public FF2J registerConverter(Converter<?> converter) {
		ConverterRegistry.getInstance().register(converter);
		
		return this;
	}

	public Exception getError() {
		return error;
	}

	/**
     * Process each line of the file and call an entity handler if that line can be mapped to an entity.
     *
     * @param input will not be closed by the reader
     */
    public Statistics parse(Reader input) {    	
    	// pre parse    	
    	createEntityHandlersCache();
    	for (EntityHandler<?> entityHandler : entityHandlers) {
    		entityHandler.beforeFirstEntity();
    	}    	
    	if (progressListener != null) {
    		progressListener.started();
    	}

    	statistics.startTime = System.currentTimeMillis();
    			
    	// parse
    	boolean success = true;
        BufferedReader reader = new BufferedReader(input);
        String lineText = null;
        long lineNumber = 0;
        try {
	        while ((lineText = reader.readLine()) != null) {
	        	lineNumber++;
	        	if (lineNumber > skipLines) {
	        		boolean goNext = true;
	        		if (progressListener != null) {
	        			goNext = progressListener.inProgress(lineNumber);
	        		}
	        		if (goNext) {
	        			goNext = onFileLine(lineNumber, lineText);
	        		}
	        		
	        		if (!goNext) {
	        			success = false;
	        			break;
	        		}
	        	}
	        }
        } catch (Exception e) {
        	success = false;
        	error = e;
        }
        
        // post parse
        statistics.endLineNumber = lineNumber;
        statistics.endTime = System.currentTimeMillis();
    	for (EntityHandler<?> entityHandler : entityHandlers) {
    		entityHandler.afterLastEntity();
    	}
    	if (progressListener != null) {
    		progressListener.ended(success);
    	}
    	
    	return statistics;
	}
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean onFileLine(long lineNumber, String lineText) throws Exception {
    	Object entity = mapper.mapEntity(lineText);
    	if (entity != null) {
    		EntityHandler entityHandler = entityHandlersCache.get(entity.getClass());
    		if (entityHandler != null) {
    			entityHandler.handleEntity(entity);
    			statistics.incrementCounter(entity.getClass());
    		}
    	} else if (noEntityHandler != null) {
    		return noEntityHandler.handleNoEntity(lineNumber, lineText);
    	}
    	
    	return true;
    }

    private void createEntityHandlersCache() {
		entityHandlersCache = new HashMap<Class<?>, EntityHandler<?>>();
    	for (EntityHandler<?> entityHandler : entityHandlers) {
    		ParameterizedType type = null;
    		Type[] interfaces = entityHandler.getClass().getGenericInterfaces();
    		if (interfaces.length > 0) {
    			type = (ParameterizedType) interfaces[0];
    		} else {
    			type = (ParameterizedType) entityHandler.getClass().getGenericSuperclass();
    		}
			Class<?> entityClass = (Class<?>) type.getActualTypeArguments()[0];
	   		entityHandlersCache.put(entityClass, entityHandler);
    	}
    }
    
    /**
     * A holder class for FF2J's statistics.
     */
    public class Statistics implements Serializable {
    	
    	private static final long serialVersionUID = 1L;
    	
		long startLineNumber;
    	long endLineNumber;
    	long startTime;
    	long endTime;
    	Map<Class<?>, Long> entitiesCounter;
    	
    	public Statistics() {
    		entitiesCounter = new LinkedHashMap<Class<?>, Long>();
    	}
    	
		public long getStartLineNumber() {
			return startLineNumber;
		}
		
		public long getEndLineNumber() {
			return endLineNumber;
		}
    	
		/**
		 *  Returns elapsed miliseconds.
		 *  
		 * @return
		 */
		public long getElapsedTime() {
			return endTime - startTime;
		}

		public Map<Class<?>, Long> getEntitiesCounter() {
			return entitiesCounter;
		}

		/**
		 * Returns elapsed milliseconds as sting in format HH:mm:ss.SSS.
		 * 
		 * @return
		 */
		public String getElapsedTimeString() {
			long timeInMillis = getElapsedTime();
			
			// format milliseconds			
			long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
			timeInMillis -= TimeUnit.HOURS.toMillis(hours);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
			timeInMillis -= TimeUnit.MINUTES.toMillis(minutes);
			long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis);
			timeInMillis -= TimeUnit.SECONDS.toMillis(seconds);
			 
			// HH:mm:ss.SSS
			return String.format("%d:%d:%d.%d", hours, minutes, seconds, timeInMillis);	
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer("FF2J Statistics:\n");
			sb.append("\t" + "startLineNumber = " + getStartLineNumber() + "\n");
			sb.append("\t" + "endLineNumber = " + getEndLineNumber() + "\n");
			sb.append("\t" + "elapsedTime = " + getElapsedTimeString() + "\n");
			sb.append("\t" + "entitiesCounter = " + getEntitiesCounter());
			
			return sb.toString();
		}		
		
		void incrementCounter(Class<?> enityClass) {
			Long counter = entitiesCounter.get(enityClass);
			if (counter == null) {
				counter = 1L;
			} else {
				counter = Long.valueOf(counter.longValue() + 1);
			}
			entitiesCounter.put(enityClass, counter);
		}
		
    }
    
}
