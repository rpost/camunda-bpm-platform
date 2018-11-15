/*
 * Copyright © 2014 - 2018 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.model.xml.validation;

import org.camunda.bpm.model.xml.testmodel.instance.FlyingAnimal;

/**
 * @author Daniel Meyer
 *
 */
public class IsAdultWarner implements ModelElementValidator<FlyingAnimal> {

  @Override
  public Class<FlyingAnimal> getElementType() {
    return FlyingAnimal.class;
  }

  @Override
  public void validate(FlyingAnimal element, ValidationResultCollector validationResultCollector) {

    if(element.getAge() == null || element.getAge() < 18) {
      validationResultCollector.addWarning(10, "Is not an adult");
    }
  }

}
