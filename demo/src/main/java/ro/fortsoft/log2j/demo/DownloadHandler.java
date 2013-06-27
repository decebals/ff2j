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
package ro.fortsoft.log2j.demo;

import ro.fortsoft.log2j.EntityHandler;

/**
 * @author Decebal Suiu
 */
public class DownloadHandler implements EntityHandler<Download> {

	private int count;
	
	@Override
	public void beforeFirstEntity() {
		count = 0;
	}

	@Override
	public void handleEntity(Download entity) {
		count++;
		// only display the entity 
		System.out.println(entity);		
	}

	@Override
	public void afterLastEntity() {
		System.out.println("Handled " + count + " Download entities");
	}

}
