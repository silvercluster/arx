/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2016 Fabian Prasser, Florian Kohlmayer and contributors
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

package org.deidentifier.arx.metric;

/**
 * Base class for quality measures
 * 
 * @author Fabian Prasser
 */
public class QualityMeasure {

    /** Minimum */
    private final double minimum;
    /** Value */
    private final double value;
    /** Maximum */
    private final double maximum;
    /** Name */
    private final String name;

    /**
     * Creates a new instance
     * @param minimum
     * @param value
     * @param maximum
     * @param name
     */
    protected QualityMeasure(String name, double minimum, double value, double maximum) {
        this.minimum = minimum;
        this.value = value;
        this.maximum = maximum;
        this.name = name;
    }

    /**
     * @return the maximum
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * @return the minimum
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * Returns the name
     * @return
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }
    
}
