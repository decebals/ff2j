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
package ro.fortsoft.log2j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import ro.fortsoft.log2j.converter.Converter;

/**
 * @author Decebal Suiu
 */
class EntityMetaData {

	private Class<?> entityClass;
	private Map<Field, FieldMetaData> fields;
	private Pattern pattern;
	
	public EntityMetaData(Class<?> entityClass) {
		this.entityClass = entityClass;
		
		fields = new HashMap<Field, FieldMetaData>();
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public Set<Field> getMappedFields() {
		return fields.keySet();
	}

	public void addFieldMetaData(Field field, FieldMetaData fieldMetaData) {
		fields.put(field, fieldMetaData);
	}
	
	public FieldMetaData getFieldMetaData(Field field) {
		return fields.get(field);
	}
	
	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("entityClass = " + entityClass);
		sb.append(",");
		sb.append("pattern = " + pattern);
		sb.append(",");
		sb.append("fields = " + fields);
		
		return sb.toString();
	}

	static class FieldMetaData {
	
		private int group;
		private Class<? extends Converter<?>> converter;
		
		public FieldMetaData(int group, Class<? extends Converter<?>> converter) {
			this.group = group;
			this.converter = converter;
		}

		public int getGroup() {
			return group;
		}

		public Class<? extends Converter<?>> getConverter() {
			return converter;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("group = " + group);
			sb.append(",");
			sb.append("converter = " + converter);
			
			return sb.toString();
		}

	}
	
}
