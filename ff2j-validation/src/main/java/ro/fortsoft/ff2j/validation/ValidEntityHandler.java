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
package ro.fortsoft.ff2j.validation;

import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AnnotationsConfigurer;
import net.sf.oval.configuration.annotation.BeanValidationAnnotationsConfigurer;
import ro.fortsoft.ff2j.EntityHandler;

/**
 * This EntityHandler uses BeanValidation (JSR 303) to validate an entity in handleEntity method.
 *  
 * @author Decebal Suiu
 */
public abstract class ValidEntityHandler<T> implements EntityHandler<T> {

	private Validator validator;
	
	/**
	 * If the entity is valid than method handleValidEntity() is called else 
	 * method handleInvalidEntity() is called.
	 */
	@Override
	public final void handleEntity(T entity) {
		if (validator == null) {
			validator = new Validator(new AnnotationsConfigurer(), new BeanValidationAnnotationsConfigurer());
		}
		// check for validation
		List<ConstraintViolation> violations = validator.validate(entity);
		if (violations.isEmpty()) {				
			handleValidEntity(entity);
		} else {
			handleInvalidEntity(entity, violations);
		}
	}

	public abstract void handleValidEntity(T entity);
	
	public abstract void handleInvalidEntity(T entity, List<ConstraintViolation> violations);
	
}
