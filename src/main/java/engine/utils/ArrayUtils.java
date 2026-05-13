package engine.utils;

import java.util.List;

public class ArrayUtils {



    public static int[] convertToIntArray(List<Integer> intList) {
        int[] indArray = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            indArray[i] = intList.get(i);
        }
        return indArray;
    }

    public static float[] convertToFloatArray(List<Float> floatList) {
        float[] floatArray = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            floatArray[i] = floatList.get(i);
        }
        return floatArray;
    }

}
