package org.deeplearning4j.spark.impl.common;

import lombok.AllArgsConstructor;
import org.apache.spark.api.java.function.Function2;

import java.util.*;

/**
 * SplitPartitionsFunction is used to split a RDD (using {@link org.apache.spark.api.java.JavaRDD#mapPartitionsWithIndex(Function2, boolean)}
 * via filtering.<br>
 * It is similar in design to {@link org.apache.spark.api.java.JavaRDD#randomSplit(double[])} however it is less prone to
 * producing imbalanced splits that that method. Specifically, {@link org.apache.spark.api.java.JavaRDD#randomSplit(double[])}
 * splts each element individually (i.e., randomly determine a new split for each element at random), whereas this method
 * chooses one out of every numSplits objects per output split. Exactly <i>which</i> of these is done randomly.
 *
 * @author Alex Black
 */
@AllArgsConstructor
public class SplitPartitionsFunction<T> implements Function2<Integer, Iterator<T>, Iterator<T>> {
    private final int splitIndex;
    private final int numSplits;
    private final long baseRngSeed;

    @Override
    public Iterator<T> call(Integer v1, Iterator<T> iter) throws Exception {
        long thisRngSeed = baseRngSeed + v1;

        Random r = new Random(thisRngSeed);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < numSplits; i++) {
            list.add(i);
        }

        List<T> outputList = new ArrayList<>();
        int i = 0;
        while (iter.hasNext()) {
            if (i % numSplits == 0)
                Collections.shuffle(list, r);

            T next = iter.next();
            if (list.get(i % numSplits) == splitIndex)
                outputList.add(next);
            i++;
        }

        return outputList.iterator();
    }
}
