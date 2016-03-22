/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2015 Florian Kohlmayer, Fabian Prasser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deidentifier.arx.test;

import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.Data.DefaultData;

/**
 * Provides data for test cases.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class DataProvider {
    
    /** TODO */
    protected DefaultData          data;
                                   
    /** TODO */
    protected DefaultHierarchy     age;
                                   
    /** TODO */
    private final DefaultHierarchy ageWrong;
                                   
    /** TODO */
    private final DefaultHierarchy gender;
                                   
    /** TODO */
    private final DefaultHierarchy zipcode;
                                   
    /** TODO */
    private final DefaultHierarchy ageOne;
                                   
    /** TODO */
    private final DefaultHierarchy genderOne;
                                   
    /** TODO */
    private final DefaultHierarchy zipcodeOne;
                                   
    /** TODO */
    private final DefaultHierarchy ageMissing;
                                   
    /**
     * Init.
     */
    public DataProvider() {
        
        // Define data
        this.data = Data.create();
        this.data.add("age", "gender", "zipcode");
        this.data.add("34", "male", "81667");
        this.data.add("45", "female", "81675");
        this.data.add("66", "male", "81925");
        this.data.add("70", "female", "81931");
        this.data.add("34", "female", "81931");
        this.data.add("70", "male", "81931");
        this.data.add("45", "male", "81931");
        
        // Define hierarchies
        this.age = Hierarchy.create();
        this.age.add("34", "<50", "*");
        this.age.add("45", "<50", "*");
        this.age.add("66", ">=50", "*");
        this.age.add("70", ">=50", "*");
        
        // Define hierarchies
        this.ageWrong = Hierarchy.create();
        this.ageWrong.add("34", "30-40", "30-69", "*");
        this.ageWrong.add("45", "40-50", "30-69", "*");
        this.ageWrong.add("66", "70-80", "30-69", "*");
        this.ageWrong.add("70", "70-80", "70+", "*");
        
        this.gender = Hierarchy.create();
        this.gender.add("male", "*");
        this.gender.add("female", "*");
        
        // Only excerpts for readability
        this.zipcode = Hierarchy.create();
        this.zipcode.add("81667", "8166*", "816**", "81***", "8****", "*****");
        this.zipcode.add("81675", "8167*", "816**", "81***", "8****", "*****");
        this.zipcode.add("81925", "8192*", "819**", "81***", "8****", "*****");
        this.zipcode.add("81931", "8193*", "819**", "81***", "8****", "*****");
        
        // Define hierarchies with height one
        this.ageOne = Hierarchy.create();
        this.ageOne.add("34");
        this.ageOne.add("45");
        this.ageOne.add("66");
        this.ageOne.add("70");
        
        this.genderOne = Hierarchy.create();
        this.genderOne.add("male");
        this.genderOne.add("female");
        
        this.zipcodeOne = Hierarchy.create();
        this.zipcodeOne.add("81667");
        this.zipcodeOne.add("81675");
        this.zipcodeOne.add("81925");
        this.zipcodeOne.add("81931");
        
        // Define hierarchies
        this.ageMissing = Hierarchy.create();
        this.ageMissing.add("34", "<50", "*");
        this.ageMissing.add("45", "<50", "*");
        this.ageMissing.add("70", ">=50", "*");
    }
    
    /**
     * Returns a standard data definition.
     */
    public void createDataDefinition() {
        // Create a standard definition
        this.data.getDefinition().setAttributeType("age", this.age);
        this.data.getDefinition().setAttributeType("gender", this.gender);
        this.data.getDefinition().setAttributeType("zipcode", this.zipcode);
    }
    
    /**
     * Returns a standard data definition.
     */
    public void createDataDefinitionMissing() {
        // Create a standard definition
        this.data.getDefinition().setAttributeType("age", this.ageMissing);
        this.data.getDefinition().setAttributeType("gender", this.gender);
        this.data.getDefinition().setAttributeType("zipcode", this.zipcode);
    }
    
    /**
     * Returns a standard data definition.
     */
    public void createDataDefinitionWithHeightOne() {
        // Create a standard definition
        this.data.getDefinition().setAttributeType("age", this.ageOne);
        this.data.getDefinition().setAttributeType("gender", this.genderOne);
        this.data.getDefinition().setAttributeType("zipcode", this.zipcodeOne);
    }
    
    /**
     * Returns a standard data definition.
     */
    public void createWrongDataDefinition() {
        // Create a standard definition
        this.data.getDefinition().setAttributeType("age", this.ageWrong);
        this.data.getDefinition().setAttributeType("gender", this.gender);
        this.data.getDefinition().setAttributeType("zipcode", this.zipcode);
    }
    
    /**
     * @return the age
     */
    public DefaultHierarchy getAge() {
        return this.age;
    }
    
    /**
     * @return the data
     */
    public DefaultData getData() {
        return this.data;
    }
    
    /**
     * @return the gender
     */
    public DefaultHierarchy getGender() {
        return this.gender;
    }
    
    /**
     * @return the zipcode
     */
    public DefaultHierarchy getZipcode() {
        return this.zipcode;
    }
}
