package net.coderodde.util;

/**
 * This class contains miscellaneous utility methods.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 5, 2016)
 */
public class Util {

    public static void swap(int[] array, int i, int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public static int median(int a, int b, int c) {
        if (a <= b) {
            if (c <= a) {
                return a;
            }

            return b <= c ? b : c;
        } 

        if (c <= b) {
            return b;
        }

        return a <= c ? a : c;
    } 
}
