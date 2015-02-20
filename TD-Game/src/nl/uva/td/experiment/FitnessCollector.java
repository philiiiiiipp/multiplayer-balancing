package nl.uva.td.experiment;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.collector.AttachPoint;
import org.moeaframework.analysis.collector.Collector;

public class FitnessCollector implements Collector {

    @Override
    public AttachPoint getAttachPoint() {
        return AttachPoint.isClass(NSGAII.class);
    }

    @Override
    public Collector attach(final Object object) {
        System.out.println(object);
        return null;
    }

    @Override
    public void collect(final Accumulator accumulator) {

    }

}
