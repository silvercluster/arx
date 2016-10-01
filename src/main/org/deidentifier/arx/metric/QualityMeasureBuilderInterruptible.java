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
 * A class offering access to the results of various quality models.
 * 
 * @author Fabian Prasser
 *         
 */
public class QualityMeasureBuilderInterruptible {
    
    /** The wrapped instance */
    private final QualityMeasureBuilder parent;
    
    /**
     * Creates a new instance
     * 
     * @param builder
     */
    QualityMeasureBuilderInterruptible(QualityMeasureBuilder parent) {
        this.parent = parent;
    }
    
    /**
     * If supported by the according builder, this method will report a progress
     * value in [0,100]. Otherwise, it will always return 0
     * 
     * @return
     */
    public int getProgress() {
        return parent.getProgress();
    }
    
    /**
     * Interrupts all computations. Raises an InterruptedException.
     */
    public void interrupt() {
        parent.interrupt();
    }
}
