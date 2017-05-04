package org.deidentifier.arx.metric.v2;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.certificate.elements.ElementData;
import org.deidentifier.arx.framework.check.groupify.HashGroupify;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;
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
public class MetricNormalizedEuclideanDistance extends AbstractMetricSingleDimensional {

    private static final long serialVersionUID = 2772702607115713132L;

    protected MetricNormalizedEuclideanDistance() {
        super(true, true, true);
    }
    
    protected MetricNormalizedEuclideanDistance(boolean monotonicWithGeneralization,
                                          boolean monotonicWithSuppression,
                                          boolean independent) {
        super(monotonicWithGeneralization, monotonicWithSuppression, independent);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ElementData render(ARXConfiguration config) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected InformationLossWithBound<ILSingleDimensional>
              getInformationLossInternal(Transformation node, HashGroupify groupify) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected InformationLossWithBound<ILSingleDimensional>
              getInformationLossInternal(Transformation node, HashGroupifyEntry entry) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ILSingleDimensional getLowerBoundInternal(Transformation node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ILSingleDimensional getLowerBoundInternal(Transformation node,
                                                        HashGroupify groupify) {
        // TODO Auto-generated method stub
        return null;
    }
}
