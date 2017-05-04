package org.deidentifier.arx.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataSubset;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;
import org.deidentifier.arx.test.AbstractTestUtilityMetrics.ARXUtilityMetricsTestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestUtilityMetricNormEuclideanDistance  extends AbstractTestUtilityEstimation {

    
    /**
     * Returns the test cases
     * 
     * @return
     * @throws IOException
     */
    @Parameters(name = "{index}:[{0}]")
    public static Collection<Object[]> cases() throws IOException {
        return Arrays.asList(new Object[][] { { new ARXUtilityMetricsTestCase(ARXConfiguration.create(0.0d,
                                                                                                      Metric.createNormalizedEuclideanDistanceMetric())
                                                                                              .addPrivacyModel(new KAnonymity(5)),
                                                                              "occupation",
                                                                              "./data/adult.csv",
                                                                              "[0, 0, 0, 0, 0, 0, 0, 0]",
                                                                              "3016.0",
                                                                              "[1, 2, 0, 0, 3, 0, 1, 0]",
                                                                              "251.33333333333334",
                                                                              "[1, 2, 0, 1, 0, 2, 2, 1]",
                                                                              "232.0",
                                                                              "[0, 4, 1, 0, 3, 2, 1, 1]",
                                                                              "502.6666666666667",
                                                                              "[1, 4, 1, 2, 2, 2, 2, 1]",
                                                                              "1005.3333333333334",
                                                                              "[1, 4, 1, 1, 3, 2, 2, 1]",
                                                                              "1508.0",
                                                                              "[0, 4, 1, 2, 3, 2, 2, 1]",
                                                                              "1508.0",
                                                                              "[1, 4, 1, 2, 3, 2, 2, 1]",
                                                                              "3016.0") }, });
    }
    
    /**
     * Creates a new instance.
     *
     * @param testCase
     */
    public TestUtilityMetricNormEuclideanDistance(final ARXUtilityMetricsTestCase testCase) {
        super(testCase);
    }
}
