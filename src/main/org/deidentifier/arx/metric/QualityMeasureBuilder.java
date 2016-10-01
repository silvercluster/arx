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

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXConfiguration.ARXConfigurationInternal;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataHandleInternal;
import org.deidentifier.arx.common.WrappedBoolean;
import org.deidentifier.arx.common.WrappedInteger;
import org.deidentifier.arx.exceptions.ComputationInterruptedException;
import org.deidentifier.arx.framework.check.groupify.HashGroupify;
import org.deidentifier.arx.framework.data.DataManager;

/**
 * A class offering access to the results of various quality models.
 *
 * @author Fabian Prasser
 */
public class QualityMeasureBuilder {

    /** Model */
    protected final WrappedInteger         progress  = new WrappedInteger();
    /** The stop flag. */
    protected volatile WrappedBoolean      interrupt = new WrappedBoolean(false);

    /** Data */
    private final DataDefinition           definition;
    /** Data */
    private final ARXConfiguration         config;
    /** Data */
    private final ARXConfigurationInternal configInternal;
    /** Data */
    private final DataManager              manager;
    /** Data */
    private final boolean                  input;
    
    /**
     * Creates a new instance for input data
     */
    public QualityMeasureBuilder() {
        this.input = true;
        this.definition = null;
        this.manager = null;
        this.config = null;
        this.configInternal = null;
    }
    
    /**
     * Creates a new instance for output data
     * @param handle
     * @param manager
     * @param definition
     * @param config
     * @param configInternal
     */
    public QualityMeasureBuilder(DataHandleInternal handle,
                                 DataManager manager,
                                 DataDefinition definition,
                                 ARXConfiguration config,
                                 ARXConfigurationInternal configInternal) {

        this.input = false;
        this.definition = definition;
        this.manager = manager;
        this.config = config;
        this.configInternal = configInternal;
    }

    /**
     * Returns an interruptible instance of this object.
     * 
     * @return
     */
    public QualityMeasureBuilderInterruptible getInterruptibleInstance() {
        return new QualityMeasureBuilderInterruptible(this);
    }
    
    /**
     * Returns progress in range [0,100]
     * @return
     */
    public int getProgress() {
        return progress.value;
    }

    /**
     * Returns results for the AECS quality measure
     */
    public QualityMeasure getQualityMeasureAECS() {
        progress.value = 0;
        QualityMeasure result = getQuality("AECS", Metric.createAECSMetric());
        progress.value = 100;
        return result;
    }
    
    /**
     * Checks whether an interruption happened.
     */
    private void checkInterrupt() {
        if (interrupt.value) {
            throw new ComputationInterruptedException("Interrupted");
        }
    }
    
    /**
     * Returns the distribution for the given handle
     * @param dataGeneralized
     * @param dataOther
     * @param config
     * @param workDone
     * @param workTodo
     * @return
     */
    private HashGroupify getGroupify(final int[][] dataGeneralized,
                                     final int[][] dataOther,
                                     final ARXConfigurationInternal config,
                                     final double workDone,
                                     final double workTodo) {

        // TODO: We need to handle record suppression correctly...
        
        // Prepare
        int initialSize = (int) (dataGeneralized.length * 0.01d);
        HashGroupify groupify = new HashGroupify(initialSize, config);

        // Compute equivalence classes
        switch (config.getRequirements()) {
        case ARXConfiguration.REQUIREMENT_COUNTER:
            for (int i = 0; i < dataGeneralized.length; i++) {
                groupify.addFromBuffer(dataGeneralized[i], null, i, 1, -1);
                progress.value = i % 100 == 0 ? (int)Math.round(workDone + (double)i/(double)dataGeneralized.length * workTodo) : progress.value;
                checkInterrupt();
            }
            break;
        case ARXConfiguration.REQUIREMENT_COUNTER | ARXConfiguration.REQUIREMENT_SECONDARY_COUNTER:
            for (int i = 0; i < dataGeneralized.length; i++) {
                groupify.addFromBuffer(dataGeneralized[i], null, i, 1, 1);
                progress.value = i % 100 == 0 ? (int)Math.round(workDone + (double)i/(double)dataGeneralized.length * workTodo) : progress.value;
                checkInterrupt();
            }
            break;
        case ARXConfiguration.REQUIREMENT_COUNTER | ARXConfiguration.REQUIREMENT_SECONDARY_COUNTER |
             ARXConfiguration.REQUIREMENT_DISTRIBUTION:
            for (int i = 0; i < dataGeneralized.length; i++) {
                groupify.addFromBuffer(dataGeneralized[i], dataOther[i], i, 1, 1);
                progress.value = i % 100 == 0 ? (int)Math.round(workDone + (double)i/(double)dataGeneralized.length * workTodo) : progress.value;
                checkInterrupt();
            }
            break;
        case ARXConfiguration.REQUIREMENT_COUNTER | ARXConfiguration.REQUIREMENT_DISTRIBUTION:
            for (int i = 0; i < dataGeneralized.length; i++) {
                groupify.addFromBuffer(dataGeneralized[i], dataOther[i], i, 1, -1);
                progress.value = i % 100 == 0 ? (int)Math.round(workDone + (double)i/(double)dataGeneralized.length * workTodo) : progress.value;
                checkInterrupt();
            }
            break;
        case ARXConfiguration.REQUIREMENT_DISTRIBUTION:
            for (int i = 0; i < dataGeneralized.length; i++) {
                groupify.addFromBuffer(dataGeneralized[i], dataOther[i], i, 1, -1);
                progress.value = i % 100 == 0 ? (int)Math.round(workDone + (double)i/(double)dataGeneralized.length * workTodo) : progress.value;
                checkInterrupt();
            }
            break;
        default:
            RuntimeException e = new RuntimeException("Invalid requirements: " + config.getRequirements());
            throw (e);
        }

        // Return
        return groupify;
    }

    /**
     * Returns quality according to a given measure
     * @param name
     * @param model
     * @return
     */
    private QualityMeasure getQuality(String name, Metric<?> model) {
        
        if (input) {
            // Create result
            return new QualityMeasure(name, 0d, 1d, 1d);        
        }

        // Create groupify
        HashGroupify groupify = getGroupify(manager.getDataGeneralized().getArray(), manager.getDataAnalyzed().getArray(), configInternal, 0d, 80d);
        
        // Initialize model
        model.initialize(manager, definition, manager.getDataGeneralized(), manager.getHierarchies(), config);
        
        // Create result
        return new QualityMeasure(name, toDouble(model.createMinInformationLoss()),
                                        toDouble(model.getInformationLoss(null, groupify).getInformationLoss()),
                                        toDouble(model.createMaxInformationLoss()));
    }

    /**
     * Converts information loss to double
     * @param loss
     * @return
     */
    private double toDouble(InformationLoss<?> loss) {
        return Double.valueOf(loss.toString());
    }

    /**
     * Stops all computations. May lead to exceptions being thrown. Use with care.
     */
    void interrupt() {
        this.interrupt.value = true;
    }
}
