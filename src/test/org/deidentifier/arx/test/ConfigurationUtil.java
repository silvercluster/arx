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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataSubset;
import org.deidentifier.arx.criteria.LDiversity;
import org.deidentifier.arx.criteria.TCloseness;
import org.deidentifier.arx.io.CSVHierarchyInput;
import org.deidentifier.arx.test.AbstractAnonymizationTest.ARXAnonymizationTestCase;

/**
 * 
 * Helper class.
 * @author Florian Kohlmayer
 *         
 */
public class ConfigurationUtil {
    
    /**
     * The Datasets.
     * 
     * @author Florian Kohlmayer
     *         
     */
    public static enum Dataset {
                                ADULT("adult.csv", "adult_subset.csv"),
                                CUP("cup.csv", "cup_subset.csv"),
                                FARS("fars.csv", "fars_subset.csv"),
                                ATUS("atus.csv", "atus_subset.csv"),
                                IHIS("ihis.csv", "ihis_subset.csv");
                                
        private String datafilename;
        private String subsetfilename;
                       
        private Dataset(String filename, String subsetfilename) {
            this.datafilename = filename;
            this.subsetfilename = subsetfilename;
        }
        
        public String getDataPath() {
            return prependDirectory(this.datafilename);
        }
        
        public String getSubsetPath() {
            return prependDirectory(this.subsetfilename);
        }
    }
    
    /** The test data directory */
    private static String       testDataDir;
    /** The default data directory */
    private static final String DEFAULT_DIR = "./data/";
                                            
    static {
        // Order: 1) system property, 3) default
        
        testDataDir = System.getProperty("test.data.dir", DEFAULT_DIR);
        
    }
    
    /**
     * Returns the data object for the test case.
     *
     * @param testCase
     * @return
     * @throws IOException
     */
    public static Data getDataObject(final ARXAnonymizationTestCase testCase) throws IOException {
        
        final Data data = Data.create(testCase.dataset.getDataPath(), ';');
        
        // Read generalization hierachies
        final FilenameFilter hierarchyFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                if (name.matches(testCase.dataset.getDataPath().substring(testCase.dataset.getDataPath().lastIndexOf("/") + 1, testCase.dataset.getDataPath().length() - 4) +
                                 "_hierarchy_(.)+.csv")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        
        final File testDir = new File(testCase.dataset.getDataPath().substring(0, testCase.dataset.getDataPath().lastIndexOf("/")));
        final File[] genHierFiles = testDir.listFiles(hierarchyFilter);
        final Pattern pattern = Pattern.compile("_hierarchy_(.*?).csv");
        
        for (final File file : genHierFiles) {
            final Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find()) {
                
                final CSVHierarchyInput hier = new CSVHierarchyInput(file, ';');
                final String attributeName = matcher.group(1);
                
                if (!attributeName.equalsIgnoreCase(testCase.sensitiveAttribute)) {
                    data.getDefinition().setAttributeType(attributeName, Hierarchy.create(hier.getHierarchy()));
                } else { // sensitive attribute
                    if (testCase.config.containsCriterion(LDiversity.class) || testCase.config.containsCriterion(TCloseness.class)) {
                        data.getDefinition().setAttributeType(attributeName, AttributeType.SENSITIVE_ATTRIBUTE);
                    }
                }
                
            }
        }
        
        return data;
    }
    
    /**
     * Returns the datasubset.
     * 
     * @param testCase
     * @return
     */
    public static DataSubset getSubset(final Dataset dataset) {
        try {
            return DataSubset.create(Data.create(dataset.getDataPath(), ';'), Data.create(dataset.getSubsetPath(), ';'));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Method to prepend the configured directory.
     * 
     * @param filename
     * @return
     */
    public static String prependDirectory(String filename) {
        return testDataDir + filename;
    }
    
    // ConfigurationUtil.getSubset(Dataset.ATUS)
    // ConfigurationUtil.getSubset(Dataset.ATUS)
    
}
