package org.xm.xmnlp.hanlp.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;
import static sun.text.normalizer.NormalizerImpl.convert;

/**
 * @author xuming
 */
public class TransformMatrixDictionary<E extends Enum<E>> {
    Class<E> enumType;
    private int ordinaryMax;

    public TransformMatrixDictionary(Class<E> enumType) {
        this.enumType = enumType;
    }

    int matrix[][];
    int total[];
    int totalFrequency;
    public int[] states;
    public double[] start_probability;
    public double[][] transition_probability;

    public boolean load(String path) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            String line = br.readLine();
            String[] param = line.split(",");
            String[] labels = new String[param.length - 1];
            System.arraycopy(param, 1, labels, 0, labels.length);
            int[] ordinaryArray = new int[labels.length];
            ordinaryMax = 0;
            for (int i = 0; i < ordinaryArray.length; ++i) {
                ordinaryArray[i] = convert(labels[i]).ordinal();
                ordinaryMax = Math.max(ordinaryMax, ordinaryArray[i]);
            }
            ++ordinaryMax;
            matrix = new int[ordinaryMax][ordinaryArray];
            for (int i = 0; i < ordinaryMax; ++i) {
                for (int j = 0; j < ordinaryMax; ++j) {
                    matrix[i][j] = 0;
                }
            }
            while ((line = br.readLine()) != null) {
                String[] paramArray = line.split(",");
                int currentOrdinary = convert(paramArray[0]).ordinal();
                for (int i = 0; i < ordinaryArray.length; ++i) {
                    matrix[currentOrdinary][ordinaryArray[i]] = Integer.valueOf(paramArray[1 + i]);
                }
            }
            br.close();


        } catch (Exception e) {
            logger.warning("读取" + path + "失败" + e);
        }

        return true;
    }
}
