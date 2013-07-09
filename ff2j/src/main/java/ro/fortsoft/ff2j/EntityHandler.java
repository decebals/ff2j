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

/**
 * @author Decebal Suiu
 */
public interface EntityHandler<T> {
	
	/**
	 * FF2J will call this method at the start of file parsing.
	 */
	public void beforeFirstEntity();
	
	public void handleEntity(T entity);

	/**
	 * FF2J will call this method at the end of file parsing.
	 */
	public void afterLastEntity();
	
}
