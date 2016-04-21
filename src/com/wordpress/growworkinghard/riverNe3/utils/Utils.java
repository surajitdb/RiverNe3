package com.wordpress.growworkinghard.riverNe3.utils;

public class Utils {

    public static double[] sumDoubleArrays(final double[] firstArray, final double[] secondArray) {
        for (int i = 0; i < firstArray.length; i++)
            firstArray[i] = firstArray[i] + secondArray[i];

        return firstArray;

    }

}
