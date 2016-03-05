package net.coderodde.util;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 10, time = 700, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 700, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(3)
public class IntQuicksortBenchmark {

    @State(Scope.Benchmark)
    public static class ArrayContainer {

        @Param({ "10000", "100000", "1000000", "10000000" })
        private int length;

        private int[] array;
        private int[] arrayToSort;

        @Setup(Level.Iteration)
        public void setUp() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            array = random.ints(length).toArray();
        }

        @Setup(Level.Invocation)
        public void cloneArray() {
            arrayToSort = array.clone();
        }

    }

    public static void sort(int[] array) {
        sort(array, 0, array.length);
    }

    public static void sort(int[] array, int fromIndex, int toIndex) {
        if (toIndex - fromIndex < 2) {
            return;
        }

        int pivot = array[fromIndex];
        int leftPartitionLength = 0;
        int rightPartitionLength = 0;
        int index = fromIndex;

        while (index < toIndex - rightPartitionLength) {
            int current = array[index];

            if (current > pivot) {
                ++rightPartitionLength;
                int tmp = array[toIndex - rightPartitionLength];
                array[toIndex - rightPartitionLength] = current;
                array[index] = tmp;
            } else if (current < pivot) {
                int tmp = array[fromIndex + leftPartitionLength];
                array[fromIndex + leftPartitionLength] = current;
                array[index] = tmp;

                ++index;
                ++leftPartitionLength;
            } else {
                ++index;
            }
        }

        sort(array, fromIndex, fromIndex + leftPartitionLength);
        sort(array, toIndex - rightPartitionLength, toIndex);
    }

    @Benchmark
    public int[] customSort(ArrayContainer container) {
        sort(container.arrayToSort);
        return container.arrayToSort;
    }

    @Benchmark
    public int[] arraysSort(ArrayContainer container) {
        Arrays.sort(container.arrayToSort);
        return container.arrayToSort;
    }

}
