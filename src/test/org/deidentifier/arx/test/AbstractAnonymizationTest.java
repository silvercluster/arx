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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXLattice.Anonymity;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.gui.resources.Resources;
import org.deidentifier.arx.test.ConfigurationUtil.Dataset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Test for data transformations.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public abstract class AbstractAnonymizationTest extends AbstractTest {
    
    /**
     * Represents a test case.
     *
     * @author Fabian Prasser
     * @author Florian Kohlmayer
     */
    public static class ARXAnonymizationTestCase {
        
        /** TODO */
        private static int      counter;
                                
        /** TODO */
        public final int        id = counter++;
                                   
        /** TODO */
        public ARXConfiguration config;
                                
        /** TODO */
        public Dataset          dataset;
                                
        /** TODO */
        public String           sensitiveAttribute;
                                
        /** TODO */
        public String           optimalInformationLoss;
                                
        /** TODO */
        public int[]            optimalTransformation;
                                
        /** TODO */
        public boolean          practical;
                                
        /** TODO */
        public int[]            statistics;
                                
        /**
         * Creates a new instance.
         *
         * @param config
         * @param dataset
         * @param optimalInformationLoss
         * @param optimalTransformation
         * @param practical
         */
        public ARXAnonymizationTestCase(final ARXConfiguration config,
                                        final Dataset dataset,
                                        final double optimalInformationLoss,
                                        final int[] optimalTransformation,
                                        final boolean practical) {
            this(config, "", dataset, optimalInformationLoss, optimalTransformation, practical, null);
        }
        
        /**
         * Creates a new instance.
         *
         * @param config
         * @param dataset
         * @param optimalInformationLoss
         * @param optimalTransformation
         * @param practical
         * @param statistics
         */
        public ARXAnonymizationTestCase(final ARXConfiguration config,
                                        final Dataset dataset,
                                        final double optimalInformationLoss,
                                        final int[] optimalTransformation,
                                        final boolean practical,
                                        int[] statistics) {
            this(config, "", dataset, optimalInformationLoss, optimalTransformation, practical, statistics);
        }
        
        /**
         * Creates a new instance.
         * @param config
         * @param dataset
         * @param datasetSubset
         * @param sensitiveAttribute
         * @param optimalInformationLoss
         * @param optimalTransformation
         * @param practical
         * @param statistics
         */
        public ARXAnonymizationTestCase(ARXConfiguration config, Dataset dataset, String sensitiveAttribute, String optimalInformationLoss, int[] optimalTransformation, boolean practical, int[] statistics) {
            this.config = config;
            this.dataset = dataset;
            this.sensitiveAttribute = sensitiveAttribute;
            this.optimalInformationLoss = optimalInformationLoss;
            this.optimalTransformation = optimalTransformation;
            this.practical = practical;
            this.statistics = statistics;
        }
        
        /**
         * Creates a new instance.
         *
         * @param config
         * @param sensitiveAttribute
         * @param dataset
         * @param optimalInformationLoss
         * @param optimalTransformation
         * @param practical
         */
        public ARXAnonymizationTestCase(final ARXConfiguration config,
                                        final String sensitiveAttribute,
                                        final Dataset dataset,
                                        final double optimalInformationLoss,
                                        final int[] optimalTransformation,
                                        final boolean practical) {
            this(config, sensitiveAttribute, dataset, optimalInformationLoss, optimalTransformation, practical, null);
        }
        
        /**
         * Creates a new instance.
         *
         * @param config
         * @param sensitiveAttribute
         * @param dataset
         * @param optimalInformationLoss
         * @param optimalTransformation
         * @param practical
         * @param statistics
         */
        public ARXAnonymizationTestCase(final ARXConfiguration config,
                                        final String sensitiveAttribute,
                                        final Dataset dataset,
                                        final double optimalInformationLoss,
                                        final int[] optimalTransformation,
                                        final boolean practical,
                                        int[] statistics) {
            this(config, dataset, sensitiveAttribute, String.valueOf(optimalInformationLoss), optimalTransformation, practical, statistics);
        }
        
        /**
         * Creates a new instance.
         *
         * @param config
         * @param sensitiveAttribute
         * @param dataset
         * @param optimalInformationLoss
         * @param optimalTransformation
         * @param practical
         */
        public ARXAnonymizationTestCase(final ARXConfiguration config,
                                        final String sensitiveAttribute,
                                        final Dataset dataset,
                                        final String optimalInformationLoss,
                                        final int[] optimalTransformation,
                                        final boolean practical) {
            this(config, sensitiveAttribute, dataset, optimalInformationLoss, optimalTransformation, practical, null);
        }
        
        /**
         * Creates a new instance.
         *
         * @param config
         * @param sensitiveAttribute
         * @param dataset
         * @param optimalInformationLoss
         * @param optimalTransformation
         * @param practical
         * @param statistics
         */
        public ARXAnonymizationTestCase(final ARXConfiguration config,
                                        final String sensitiveAttribute,
                                        final Dataset dataset,
                                        final String optimalInformationLoss,
                                        final int[] optimalTransformation,
                                        final boolean practical,
                                        int[] statistics) {
            this(config, dataset, sensitiveAttribute, optimalInformationLoss, optimalTransformation, practical, statistics);
        }
        
        @Override
        public String toString() {
            return this.config.getCriteria() + "-" + this.config.getMaxOutliers() + "-" + this.config.getMetric() + "-" + this.dataset + "-PM:" +
                   this.config.isPracticalMonotonicity();
        }
    }
    
    private static final String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
    
    /**
     * Transforms it into a string representation.
     *
     * @param classification
     * @return
     */
    public static String getClassification(int[] classification) {
        StringBuilder builder = new StringBuilder();
        builder.append("Classification {\n");
        builder.append(" Transformations: ").append(classification[0]).append("\n");
        builder.append(" Checked: ").append(classification[1]).append("\n");
        builder.append(" Anonymous: ").append(classification[2]).append("\n");
        builder.append(" Non-anonymous: ").append(classification[3]).append("\n");
        builder.append(" Probably anonymous: ").append(classification[4]).append("\n");
        builder.append(" Probably non-anonymous: ").append(classification[5]).append("\n");
        builder.append(" Utility available: ").append(classification[6]).append("\n");
        builder.append("}");
        return builder.toString();
    }
    
    /** The test case. */
    protected final ARXAnonymizationTestCase testCase;
                                             
    /** To access the test name */
    @Rule
    public TestName                          name = new TestName();
                                                  
    /**
     * Creates a new instance.
     *
     * @param testCase
     */
    public AbstractAnonymizationTest(final ARXAnonymizationTestCase testCase) {
        this.testCase = testCase;
    }
    
    @Override
    @Before
    public void setUp() {
        // Empty by design
        // We also intentionally don't call super.setUp()
    }
    
    /**
     * 
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        
        boolean benchmark = false;
        List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String argument : arguments) {
            if (argument.startsWith("-DBenchmark")) {
                benchmark = true;
                break;
            }
        }
        
        final Data data = ConfigurationUtil.getDataObject(this.testCase);
        
        // Create an instance of the anonymizer
        final ARXAnonymizer anonymizer = new ARXAnonymizer();
        this.testCase.config.setPracticalMonotonicity(this.testCase.practical);
        
        // Test or warmup
        ARXResult result = anonymizer.anonymize(data, this.testCase.config);
        
        // Benchmark
        if (benchmark) {
            
            String version = System.getProperty("Version");
            String path = System.getProperty("Benchmark");
            if ((path == null) || (path.length() == 0)) {
                path = ".";
            }
            String testClass = this.getClass().getSimpleName();
            
            final int REPETITIONS = 5;
            long time = System.currentTimeMillis();
            long time2 = 0;
            for (int i = 0; i < REPETITIONS; i++) {
                data.getHandle().release();
                result = anonymizer.anonymize(data, this.testCase.config);
                time2 += result.getTime();
            }
            time = (System.currentTimeMillis() - time) / REPETITIONS;
            time2 /= REPETITIONS;
            
            StringBuilder line = new StringBuilder();
            line.append(Resources.getVersion());
            line.append(";");
            line.append(version);
            line.append(";");
            line.append(testClass);
            line.append(";");
            line.append(this.testCase.id);
            line.append(";");
            line.append(time);
            line.append(";");
            line.append(time2);
            output(line.toString(), path + "/benchmark_" + version + "_" + timestamp + "_" + testClass + ".csv");
        }
        
        // check if no solution
        if (this.testCase.optimalTransformation == null) {
            assertTrue(result.getGlobalOptimum() == null);
        } else {
            
            String lossActual = result.getGlobalOptimum().getMaximumInformationLoss().toString();
            String lossExpected = this.testCase.optimalInformationLoss;
            
            assertEquals(this.testCase.dataset + "-should: " + lossExpected + " is: " +
                         lossActual + "(" + result.getGlobalOptimum().getMinimumInformationLoss().toString() + ")",
                         lossExpected,
                         lossActual);
                         
            if (!Arrays.equals(result.getGlobalOptimum().getTransformation(), this.testCase.optimalTransformation)) {
                System.err.println("Note: Information loss equals, but the optimum differs:");
                System.err.println("Should: " + Arrays.toString(this.testCase.optimalTransformation) + " is: " +
                                   Arrays.toString(result.getGlobalOptimum().getTransformation()));
                System.err.println("Test case: " + this.testCase.toString());
            }
        }
        
        if (this.testCase.statistics != null) {
            
            // Collect statistics
            int[] statistics = new int[7];
            for (ARXNode[] level : result.getLattice().getLevels()) {
                for (ARXNode arxNode : level) {
                    statistics[0]++;
                    if (arxNode.isChecked()) {
                        statistics[1]++;
                    }
                    if (arxNode.getAnonymity() == Anonymity.ANONYMOUS) {
                        statistics[2]++;
                    }
                    if (arxNode.getAnonymity() == Anonymity.NOT_ANONYMOUS) {
                        statistics[3]++;
                    }
                    if (arxNode.getAnonymity() == Anonymity.PROBABLY_ANONYMOUS) {
                        statistics[4]++;
                    }
                    if (arxNode.getAnonymity() == Anonymity.PROBABLY_NOT_ANONYMOUS) {
                        statistics[5]++;
                    }
                    if (arxNode.getMaximumInformationLoss() == arxNode.getMinimumInformationLoss()) {
                        statistics[6]++;
                    }
                }
            }
            
            // Compare
            String algorithmConfiguration = getAlgorithmConfiguration(this.testCase.config);
            assertEquals(algorithmConfiguration + ". Mismatch: number of transformations", this.testCase.statistics[0], statistics[0]);
            assertEquals(algorithmConfiguration + ". Mismatch: number of checks", this.testCase.statistics[1], statistics[1]);
            assertEquals(algorithmConfiguration + ". Mismatch: number of anonymous transformations", this.testCase.statistics[2], statistics[2]);
            assertEquals(algorithmConfiguration + ". Mismatch: number of non-anonymous transformations", this.testCase.statistics[3], statistics[3]);
            assertEquals(algorithmConfiguration + ". Mismatch: number of probably anonymous transformations", this.testCase.statistics[4], statistics[4]);
            assertEquals(algorithmConfiguration + ". Mismatch: number of probably non-anonymous transformations", this.testCase.statistics[5], statistics[5]);
            assertEquals(algorithmConfiguration + ". Mismatch: number of transformations with utility available", this.testCase.statistics[6], statistics[6]);
        }
    }
    
    /**
     * Writes a header to the given file
     * @param file
     */
    private void createHeader(String file) {
        File f = new File(file);
        if (!f.exists()) {
            Writer writer = null;
            try {
                writer = new FileWriter(f);
                writer = new BufferedWriter(writer);
                
                StringBuilder line = new StringBuilder();
                line.append("");
                line.append(";");
                line.append("");
                line.append(";");
                line.append("");
                line.append(";");
                line.append("");
                line.append(";");
                line.append("Execution Time");
                line.append(";");
                line.append("Internal Execution Time");
                writer.write(line.toString());
                writer.write(System.lineSeparator());
                
                line = new StringBuilder();
                line.append("Version");
                line.append(";");
                line.append("Git Commit");
                line.append(";");
                line.append("Test");
                line.append(";");
                line.append("Testid");
                line.append(";");
                line.append("Arithmetic Mean");
                line.append(";");
                line.append("Arithmetic Mean");
                writer.write(line.toString());
                writer.write(System.lineSeparator());
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        }
    }
    
    /**
     * Returns the configuration of FLASH.
     *
     * @param config
     * @return
     */
    private String getAlgorithmConfiguration(ARXConfiguration config) {
        return config.getMonotonicityOfPrivacy() + " monotonicity of privacy with " + config.getMonotonicityOfUtility() + " monotonicity of utility";
    }
    
    /**
     * Appends the given value to the file
     * @param value
     * @param file
     */
    private void output(String value, String file) {
        Writer writer = null;
        try {
            createHeader(file);
            writer = new FileWriter(file, true);
            writer = new BufferedWriter(writer);
            writer.write(value);
            writer.write(System.lineSeparator());
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
