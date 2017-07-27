package org.deeplearning4j.datasets.iterator;

import lombok.NonNull;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

/**
 * This iterator detaches/migrates DataSets coming out from backed DataSetIterator, thus providing "safe" DataSets.
 *
 * @author raver119@gmail.com
 */
public class WorkspacesShieldDataSetIterator implements DataSetIterator {
    protected DataSetIterator iterator;

    public WorkspacesShieldDataSetIterator(@NonNull DataSetIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public DataSet next(int num) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int totalExamples() {
        return iterator.totalExamples();
    }

    @Override
    public int inputColumns() {
        return iterator.inputColumns();
    }

    @Override
    public int totalOutcomes() {
        return iterator.totalOutcomes();
    }

    @Override
    public boolean resetSupported() {
        return iterator.resetSupported();
    }

    @Override
    public boolean asyncSupported() {
        return iterator.asyncSupported();
    }

    @Override
    public void reset() {
        iterator.reset();
    }

    @Override
    public int batch() {
        return iterator.batch();
    }

    @Override
    public int cursor() {
        return iterator.cursor();
    }

    @Override
    public int numExamples() {
        return iterator.numExamples();
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        iterator.setPreProcessor(preProcessor);
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        return iterator.getPreProcessor();
    }

    @Override
    public List<String> getLabels() {
        return iterator.getLabels();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public DataSet next() {
        DataSet ds = iterator.next();

        if (ds.getFeatures().isAttached()) {
            if (Nd4j.getMemoryManager().getCurrentWorkspace() == null) {
                ds.detach();
            } else {
                ds.migrate();
            }
        }

        return ds;
    }

    @Override
    public void remove() {
        // no-op
    }
}
