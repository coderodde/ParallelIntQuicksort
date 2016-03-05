package net.coderodde.util;

import java.util.Arrays;
import java.util.Random;
import static net.coderodde.util.Util.median;
import static net.coderodde.util.Util.swap;

public class IntQuicksort {

    private static final int INSERTIONSORT_THRESHOLD = 16;

    private IntQuicksort() {

    }

    public static void sort(int[] array) {
        sort(array, 0, array.length);
    }

    public static void sort(int[] array, int fromIndex, int toIndex) {
        while (true) {
            int rangeLength = toIndex - fromIndex;

            if (rangeLength < 2) {
                return;
            }

            if (rangeLength < INSERTIONSORT_THRESHOLD) {
                insertionsort(array, fromIndex, toIndex);
                return;
            }

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

            if (leftPartitionLength < rightPartitionLength) {
                sort(array, fromIndex, fromIndex + leftPartitionLength);
                fromIndex = toIndex - rightPartitionLength;
            } else {
                sort(array, toIndex - rightPartitionLength, toIndex);
                toIndex = fromIndex + leftPartitionLength;
            }
        }
    }

    private static void insertionsort(int[] array, int fromIndex, int toIndex) {
        for (int i = fromIndex + 1; i < toIndex; ++i) {
            int current = array[i];
            int j = i  - 1;

            while (j >= fromIndex && array[j] > current) {
                array[j + 1] = array[j];
                --j;
            }

            array[j + 1] = current;
        }
    }

    private static final int SIZE = 500_000;
    private static final int FROM = 100;
    private static final int TO = SIZE - 100;

    public static void main(String[] args) {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        int[] array1 = getRandomArray(SIZE, 0, 1_000_000_000, random);
        int[] array2 = array1.clone();
        int[] array3 = array1.clone();

        System.out.println("Seed: " + seed);
        long startTime = System.nanoTime();
        IntQuicksort.sort(array1, FROM, TO);
        long endTime = System.nanoTime();

        System.out.printf("IntQuicksort.sort in %.2f milliseconds.\n",
                          (endTime - startTime) / 1e6);

        startTime = System.nanoTime();
        ParallelIntQuicksort.sort(array2, FROM, TO);
        endTime = System.nanoTime();

        System.out.printf("ParallelIntQuicksort.sort in %.2f milliseconds.\n",
                          (endTime - startTime) / 1e6);

        startTime = System.nanoTime();
        Arrays.sort(array3, FROM, TO);
        endTime = System.nanoTime();

        System.out.printf("Arrays.sort in %.2f milliseconds.\n",
                          (endTime - startTime) / 1e6);

        System.out.println("Arrays are equal: " + 
                           (Arrays.equals(array1, array2) &&
                            Arrays.equals(array2, array3)));
    }

    public static int[] getRandomArray(int size, 
                                       int minimum, 
                                       int maximum, 
                                       Random random) {
        int[] array = new int[size];

        for (int i = 0; i < size; ++i) {
            array[i] = random.nextInt(maximum - minimum + 1) + minimum;
        }

        return array;
    }
}
