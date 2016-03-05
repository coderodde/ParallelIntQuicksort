package net.coderodde.util;

import static net.coderodde.util.Util.median;
import static net.coderodde.util.Util.swap;

/**
 * This class implements a parallel Quicksort.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 5, 2016)
 */
public class ParallelIntQuicksort {

    private static final int MINIMUM_THREAD_WORKLOAD = 131_072;

    public static void sort(int[] array) {
        sort(array, 0, array.length);
    }

    public static void sort(int[] array, int fromIndex, int toIndex) {
        int rangeLength = toIndex - fromIndex;
        int cores = Math.min(rangeLength / MINIMUM_THREAD_WORKLOAD,
                             Runtime.getRuntime().availableProcessors());
        sortImpl(array, 
                 fromIndex, 
                 toIndex, 
                 cores);
    }

    private ParallelIntQuicksort() {

    }

    private static void sortImpl(int[] array,
                                 int fromIndex, 
                                 int toIndex,
                                 int cores) {
        if (cores <= 1) {
            IntQuicksort.sort(array, fromIndex, toIndex);
            return;
        }

        int rangeLength = toIndex - fromIndex;
        int distance = rangeLength / 4;

        int a = array[fromIndex + distance];
        int b = array[fromIndex + (rangeLength >>> 1)];
        int c = array[toIndex - distance];

        int pivot = median(a, b, c);
        int leftPartitionLength = 0;
        int rightPartitionLength = 0;
        int index = fromIndex;

        while (index < toIndex - rightPartitionLength) {
            int current = array[index];

            if (current > pivot) {
                ++rightPartitionLength;
                swap(array, toIndex - rightPartitionLength, index);
            } else if (current < pivot) {
                swap(array, fromIndex + leftPartitionLength, index);
                ++index;
                ++leftPartitionLength;
            } else {
                ++index;
            }
        }

        ParallelQuicksortThread leftThread = 
                new ParallelQuicksortThread(array,
                                            fromIndex,
                                            fromIndex + leftPartitionLength,
                                            cores / 2);
        ParallelQuicksortThread rightThread =
                new ParallelQuicksortThread(array,
                                            toIndex - rightPartitionLength,
                                            toIndex,
                                            cores - cores / 2);

        leftThread.start();
        rightThread.start();

        try {
            leftThread.join();
            rightThread.join();
        } catch (InterruptedException ex) {
            throw new IllegalStateException(
                    "Parallel quicksort threw an InterruptedException.");
        }
    }

    private static final class ParallelQuicksortThread extends Thread {

        private final int[] array;
        private final int fromIndex;
        private final int toIndex;
        private final int cores;

        ParallelQuicksortThread(int[] array, 
                                int fromIndex, 
                                int toIndex, 
                                int cores) {
            this.array = array;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.cores = cores;
        }

        @Override
        public void run() {
            sortImpl(array, fromIndex, toIndex, cores);
        }
    }
}
