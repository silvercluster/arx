package org.deidentifier.arx.metric.v2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.DataType.ARXDecimal;
import org.deidentifier.arx.DataType.ARXInteger;
import org.deidentifier.arx.certificate.elements.ElementData;
import org.deidentifier.arx.framework.check.groupify.HashGroupify;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;
import org.deidentifier.arx.framework.data.Data;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.data.GeneralizationHierarchy;
import org.deidentifier.arx.framework.lattice.Transformation;
import org.deidentifier.arx.metric.InformationLossWithBound;

/**
 * Normalized Euclidean Distance.
 * 
 * Implementation based on "Sánchez, D., Martínez, S., & Domingo-Ferrer, J.
 * (2016). Comment on “Unique in the shopping mall: On the reidentifiability of
 * credit card metadata”. Science, 351(6279), 1274-1274."
 * 
 * @author Martin Waltl
 */
public class MetricSDNormalizedEuclideanDistance extends AbstractMetricSingleDimensional {

    private static final long serialVersionUID = 2772702607115713132L;
    
    /** Typical dev*/
    private double[] typicalDev;
    /** Max*/
    private double max[];
    /** Min*/
    private double min[];
    /** Num attr*/
    private int numAttr;
    /** Max euclidean distance*/
    private double maxEuclideanDistance;
    /** Input data obtained during initialization */
    private Data input;
    /** Generalization hierarchies obtained during initialization */
    private GeneralizationHierarchy[] hierarchies;

    
    /** Indices of the relevant attributes */
    private List<Integer> relevantAttributes;

    protected MetricSDNormalizedEuclideanDistance() {
        super(true, true, true);
    }
    
    protected MetricSDNormalizedEuclideanDistance(boolean monotonicWithGeneralization,
                                          boolean monotonicWithSuppression,
                                          boolean independent) {
        super(monotonicWithGeneralization, monotonicWithSuppression, independent);
    }

    @Override
    public ElementData render(ARXConfiguration config) {
        ElementData result = new ElementData("Normalized Euclidean Distance");
        result.addProperty("Monotonic", this.isMonotonic(config.getMaxOutliers()));
        result.addProperty("Generalization factor", this.getGeneralizationFactor());
        result.addProperty("Suppression factor", this.getSuppressionFactor());
        return null;
    }

    @Override
    protected InformationLossWithBound<ILSingleDimensional> getInformationLossInternal(Transformation node, HashGroupify groupify) {
        double distance = 0.0;
        
        HashGroupifyEntry entry = groupify.getFirstEquivalenceClass();
        while (entry != null) {
            distance += computeEuclideanDistance(node, entry);
            entry = entry.nextOrdered;
        }
        
        distance /= (double)this.relevantAttributes.size();
        distance /= (double)this.input.getDataLength();
        distance /= this.maxEuclideanDistance;          // normalize

        return (new InformationLossWithBound<ILSingleDimensional>(new ILSingleDimensional(distance)));
    }

    @Override
    protected InformationLossWithBound<ILSingleDimensional> getInformationLossInternal(Transformation node, HashGroupifyEntry entry) {
        double distance = computeEuclideanDistance(node, entry);
        distance /= (double)entry.count;
        distance /= (double)this.relevantAttributes.size();
        distance /= this.maxEuclideanDistance;          // normalize
        
        return (new InformationLossWithBound<ILSingleDimensional>(new ILSingleDimensional(distance)));
    }
        
    protected double computeEuclideanDistance(Transformation node, HashGroupifyEntry entry) {
        // TODO dummy, the computation must be done here
        final int[][] inputData = input.getData();
        final String[][] dict = input.getDictionary().getMapping();

        double distance = 0.0;

        if(entry.isNotOutlier) {
            for (int key : entry.key) {
                
            }
        }
        else {
            // TODO entry/equivalence class (all entries) suppressed
            
        }

        return distance;
    }
    
    
    
    
    private String[] getGeneralizedRecord(Transformation node, int row) {
        int[] generalization = node.getGeneralization();
        double infoLoss = 1d;
    }
    
    private String[] getInputRecord(int row) {
        String[] record = new String[input.getHeader().length];
        
        for(int attr = 0; attr < input.getHeader().length; attr++) {
            record[attr] = input.getDictionary().getMapping()[attr][input.getData()[0][attr]];
        }
        
        return record;
    }
    
    private double computeDistanceBetweenTwoRecords(String[] inputRecord, String[] outputRecord) {
        double dis, partial, partial1, cn1, cn2;
        double min, max;
        
        dis = 0;
        partial = 0;
        for(int attr : this.relevantAttributes){
            
            cn1 = Double.parseDouble(inputRecord[attr]);
            double[] minmax;
            try {
                minmax = parse(outputRecord[attr], attr);
            } catch (ParseException e) {
                throw new IllegalStateException("Parsing failed: " + e.getMessage());
            }
            min = minmax[0];
            max = minmax[1];
            if((max - cn1) > (cn1 - min)){
                cn2 = max;
            }
            else{
                cn2 = min;
            }
            if(typicalDev[attr] == 0.0){
                partial1 = 0.0;
            }
            else{
                partial1 = (cn1-cn2) / typicalDev[attr];
            }
            partial += (partial1 * partial1);
        }
        dis = Math.sqrt(partial);
        return dis;
    }
    
    /**
     * Parses different forms of transformed values
     * @param value
     * @return
     * @throws ParseException 
     */
    private double[] parse(String value, int column) throws ParseException {

        double min, max;
        
        // Suppressed
        if (value.equals("*")) {
            min = this.min[column];
            max = this.max[column];
            
        // Masked
        } else if (value.contains("*")) {
            min = Double.valueOf(value.replace('*', '0'));
            max = Double.valueOf(value.replace('*', '9'));
            
        // Interval from ARX
        } else if (value.startsWith("[") && value.endsWith("[")) {
            min = Double.valueOf(value.substring(1, value.indexOf(",")).trim());
            max = Double.valueOf(value.substring(value.indexOf(",") + 1, value.length() - 1).trim()) - 1d;

        // Interval from Crises
        } else if (value.startsWith("[") && value.endsWith("]")) {
            min = Double.valueOf(value.substring(1, value.indexOf(";")).trim());
            max = Double.valueOf(value.substring(value.indexOf(";") + 1, value.length() - 1).trim());

        // ARX upper boundary (greater than)
        } else if (value.startsWith(">") && !value.startsWith(">=")) {
            min = this.min[column];
            max = Double.valueOf(value.substring(1, value.length()));
            max += 1d;          // TODO only valid for integer values   
            
        // ARX upper boundary (greater than or equal)
        } else if (value.startsWith(">=")) {        
            min = this.min[column];
            max = Double.valueOf(value.substring(2, value.length()));
            
        // ARX lower boundary (lower than)
        } else if (value.startsWith("<") && !value.startsWith("<=")) {          
            min = Double.valueOf(value.substring(1, value.length()));
            min -= 1d;      // TODO only valid for integer values   
            max = this.max[column];
            
        // ARX lower boundary (lower than or equals)
        } else if (value.startsWith("<=")) {            
            min = Double.valueOf(value.substring(2, value.length()));
            max = this.max[column];
            
        // Set from ARX - TODO: ARX should not use sets! 
        } else if (value.startsWith("{") && value.endsWith("}")) {
            
            min = Double.MAX_VALUE;
            max = -Double.MAX_VALUE;
            value = value.replace('{', ' ').replace('}', ' ');
            for (String part : value.split(",")) {
                part = part.trim();
                if (!part.equals("")) {
                    min = Math.min(min, Double.valueOf(part));
                    max = Math.max(max, Double.valueOf(part));
                }
            }
            
        // Ungeneralized
        } else if (containsOnlyDigits(value)){
            min = Double.valueOf(value);
            max = Double.valueOf(value);
        
        // Check
        } else {
            throw new ParseException("Cannot parse: " + value, 0);
        }
        
        // Return
        return new double[]{min, max};
    }
    
    /**
     * Returns whether the string contains only digits
     * @param value
     * @return
     */
    private boolean containsOnlyDigits(String value) {
        char[] chars = value.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected ILSingleDimensional getLowerBoundInternal(Transformation node) {
        return null;
    }

    @Override
    protected ILSingleDimensional getLowerBoundInternal(Transformation node, HashGroupify groupify) {
        return null;
    }
    
    @Override
    protected void initializeInternal(DataManager manager,
                                      DataDefinition definition,
                                      Data input,
                                      GeneralizationHierarchy[] hierarchies,
                                      ARXConfiguration config) {
        super.initializeInternal(manager, definition, input, hierarchies, config);
        
        if(input.getDataLength() < 1) {
            throw new IllegalStateException("Input data must not be empty.");
        }
        
        // TODO Currently, the quasi-identifiers are used. Check if it is useful/necessary to include other attributes as well

        // prepare statistics for input data
        this.input = input;
        this.hierarchies = hierarchies;
        numAttr = input.getHeader().length;
        this.relevantAttributes = new ArrayList<Integer>();
        for(int attr = 0; attr < numAttr; attr++) {
            DataType<?> attrDataType = definition.getDataType(input.getHeader()[attr]);
            System.out.println("NED: input attribute " + input.getHeader()[attr]);
            if (attrDataType instanceof ARXInteger || attrDataType instanceof ARXDecimal) {
                System.out.println("Is Numeric -> use");
                this.relevantAttributes.add(attr);
            }
            else {
                System.out.println("Is Not Numeric -> skip");
            }
        }
        
        if (this.relevantAttributes.size() == 0) {
            throw new IllegalStateException("NED cannot be computed (no numeric attributes specified).");
        }
        
        int[][] inputData = input.getData();

        typicalDev = new double[numAttr];
        max = new double[numAttr];
        min = new double[numAttr];
        
        final String[][] dict = input.getDictionary().getMapping();
        
        // Compute min, max and typical deviation for each attribute
        for (int attr : relevantAttributes) {
            double[] values = new double[input.getDataLength()];

            max[attr] = Double.parseDouble(dict[attr][inputData[0][attr]]);
            min[attr] = Double.parseDouble(dict[attr][inputData[0][attr]]);
            
            for (int row = 1; row < input.getDataLength(); row++) {
                values[row] = Double.parseDouble(dict[attr][inputData[row][attr]]);

                if (values[row] > max[attr]) {
                    max[attr] = values[row];
                }
                if (values[row] < min[attr]) {
                    min[attr] = values[row];
                }
            }

            typicalDev[attr] = getTypcialDeviation(values);
        }
        
        // Compute maximum euclidean distance
        this.maxEuclideanDistance = 0d;
        for (int row = 1; row < input.getDataLength(); row++) {
            double partial = 0d;
            
            for(int attr : relevantAttributes){
                double cn1 = Double.parseDouble(dict[attr][inputData[row][attr]]);
                double cn2 = 0d;
                double min = this.min[attr];
                double max = this.max[attr];
                if((max - cn1) > (cn1 - min)){
                    cn2 = max;
                }
                else{
                    cn2 = min;
                }
                double partial1;
                if(typicalDev[attr] == 0.0){
                    partial1 = 0.0;
                }
                else{
                    partial1 = (cn1-cn2) / typicalDev[attr];
                }
                partial += (partial1 * partial1);
            }
            this.maxEuclideanDistance += Math.sqrt(partial);
        }
        this.maxEuclideanDistance /= this.relevantAttributes.size();
        this.maxEuclideanDistance /= input.getDataLength();
        System.out.println("NED initialization complete");
    }
    
    /**
     * This function calculates the typical deviation of an attribute
     * 
     * @author Sergio Martínez (Universitat Rovira i Virgili)
     */
    private double getTypcialDeviation(double values[]){
        // TODO Make it smarter, currently it is copied from Soria Comas
        double typicalDev, medianVar, partial;
        
        medianVar = 0;
        for(int i=0; i<values.length; i++){
            medianVar += values[i];
        }
        medianVar /= values.length;
        
        typicalDev = 0;
        for(int i=0; i<values.length; i++){
            partial = values[i] - medianVar;
            partial = partial * partial;
            typicalDev += partial;
        }
        typicalDev /= (values.length - 1);
        typicalDev = Math.sqrt(typicalDev);
        
        return typicalDev;
    }
    
    @Override
    public String toString() {
        return "NormalizedEuclideanDistance";    
    }
    
    @Override
    public String getName() {
        return "Normalized Euclidean Distance";
    }
    
    /**
     * Only for test purposes (JUnit). Get maximum value for each attribute.
     * @return
     */
    public double[] getMax() {
        return max;
    }
    
    /**
     * Only for test purposes (JUnit). Get minimum value for each attribute.
     * @return
     */
    public double[] getMin() {
        return max;
    }

    /**
     * Only for test purposes (JUnit). Get maximum euclidean distance.
     * @return
     */
    public double getMaxEuclideanDistance() {
        return maxEuclideanDistance;
    }
    
}
