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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.fortsoft.ff2j.converter.Converter;
import ro.fortsoft.ff2j.converter.ConverterUtils;
import ro.fortsoft.ff2j.converter.DefaultConverter;

/**
 * @author Decebal Suiu
 */
class Mapper {
	
	 /** Set of classes that have been validated for mapping by this mapper */
    private List<Class<?>> mappedClasses;
    
    private Set<EntityMetaData> entitesMetaData;
	private Validator validator;
    
    public Mapper() {
		mappedClasses = new ArrayList<Class<?>>();
    	entitesMetaData = new HashSet<EntityMetaData>();
    	validator = new Validator();
    }
    
    public boolean isMapped(Class<?> entityClass) {
        return mappedClasses.contains(entityClass);
    }

    public void addMappedClass(Class<?> entityClass) {
		try {
			validator.validate(entityClass);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
        mappedClasses.add(entityClass);
    }

    public List<Class<?>> getMappedClasses() {
        return mappedClasses;
    }
                
    public Object mapEntity(String lineText) throws Exception {
    	for (EntityMetaData entityMetaData : entitesMetaData) {
    		Matcher matcher = entityMetaData.getPattern().matcher(lineText);
			if (matcher.matches()) {
//				int groupCount = matcher.groupCount();
//				System.out.println("groupCount = " + groupCount);
				Class<?> entityClass = entityMetaData.getEntityClass();
				Object entity = entityClass.newInstance();
				Set<Field> fields = entityMetaData.getMappedFields();
				for (Field field : fields) {
					EntityMetaData.FieldMetaData fieldMetaData = entityMetaData.getFieldMetaData(field);
					String text = matcher.group(fieldMetaData.getGroup());
					setFieldValue(field, entity, text, fieldMetaData.getConverter());
				}
				
				return entity;
			}
    	}
    	
    	return null;
    }
    
	private void setFieldValue(Field field, Object target, String text, Class<? extends Converter<?>> converter) throws Exception {
		field.setAccessible(true);
		if (String.class.equals(field.getType())) {
		    field.set(target, text);
		} else {
			Object value = null;
			if (DefaultConverter.class.equals(converter)) {
				value = ConverterUtils.convert(text, field.getType());
			} else {
				value = converter.newInstance().decode(text);
			}
			field.set(target, value);
		}
	}
	
	class Validator {
		
	    public void validate(Class<?> entityClass) throws Exception {
	    	String messagePrefix = "In [" + entityClass.getName() + "]: ";

	    	// test for @RegexEntity on entity class
	    	if (!entityClass.isAnnotationPresent(RegexEntity.class)) {
	    		throw new Exception(messagePrefix +  "Class isn't annotated with @RegexEntity");
	    	}
	    	
	    	// test for regex pattern
	    	String pattern = entityClass.getAnnotation(RegexEntity.class).pattern();
	    	Field patternField;
			try {
				patternField = entityClass.getDeclaredField(pattern);
			} catch (NoSuchFieldException e) {
	    		throw new Exception(messagePrefix + "Cannot find the constant " + pattern);
			}
	    	
	    	String regex = (String) patternField.get(String.class);
//	    	System.out.println(entityClass.getName() + " >>> " + regex);

	    	EntityMetaData entityMetaData = new EntityMetaData(entityClass);
	    	entityMetaData.setPattern(Pattern.compile(regex));
			Field[] fields = entityClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(RegexField.class)) {
					RegexField regexField = field.getAnnotation(RegexField.class);
					int group = regexField.group();
					Class<? extends Converter<?>> converter = regexField.converter(); 
					entityMetaData.addFieldMetaData(field, new EntityMetaData.FieldMetaData(group, converter));
				}
			}
//			System.out.println(entityMetaData);
			entitesMetaData.add(entityMetaData);
	    }
	
	}
	
}
