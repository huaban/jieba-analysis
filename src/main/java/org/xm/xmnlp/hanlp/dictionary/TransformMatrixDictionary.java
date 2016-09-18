package org.xm.xmnlp.hanlp.dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

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
            matrix = new int[ordinaryMax][ordinaryMax];
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
            total = new int[ordinaryMax];
            for (int j = 0; j < ordinaryMax; ++j) {
                total[j] = 0;
                for (int i = 0; i < ordinaryMax; ++i) {
                    total[j] += matrix[i][j];
                    total[j] += matrix[j][i];
                }
            }
            for (int j = 0; j < ordinaryMax; ++j) {
                total[j] -= matrix[j][j];
            }
            for (int j = 0; j < ordinaryMax; ++j) {
                totalFrequency += total[j];
            }
            states = ordinaryArray;
            start_probability = new double[ordinaryMax];
            for (int s : states) {
                double frequency = total[s] + 1e-8;
                start_probability[s] = -Math.log(frequency / totalFrequency);
            }
            transition_probability = new double[ordinaryMax][ordinaryMax];
            for (int from : states) {
                for (int to : states) {
                    double frequency = matrix[from][to] + 1e-8;
                    transition_probability[from][to] = -Math.log(frequency / total[from]);
                }
            }


        } catch (Exception e) {
            logger.warning("读取" + path + "失败" + e);
        }

        return true;
    }

    public int getFrequency(String from, String to) {
        return getFrequency(convert(from), convert(to));
    }

    public int getFrequency(E from, E to) {
        return matrix[from.ordinal()][to.ordinal()];
    }

    public int getTotalFrequency(E e) {
        return total[e.ordinal()];
    }

    public int getTotalFrequency() {
        return totalFrequency;
    }

    protected E convert(String label) {
        return Enum.valueOf(enumType, label);
    }

    public void extendSize() {
        ++ordinaryMax;
        double[][] n_transition_probability = new double[ordinaryMax][ordinaryMax];
        for (int i = 0; i < transition_probability.length; i++) {
            System.arraycopy(transition_probability[i], 0, n_transition_probability[i], 0, transition_probability.length);
        }
        transition_probability = n_transition_probability;
        int[] n_total = new int[ordinaryMax];
        System.arraycopy(total, 0, n_total, 0, total.length);
        total = n_total;
        double[] n_start_probability = new double[ordinaryMax];
        System.arraycopy(start_probability, 0, n_start_probability, 0, start_probability.length);
        start_probability = n_start_probability;
        int[][] n_matrix = new int[ordinaryMax][ordinaryMax];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, n_matrix[i], 0, matrix.length);
        }
        matrix = n_matrix;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransfromMatrixDictionary{");
        sb.append("enumType = ").append(enumType);
        sb.append(", ordinaryMax = ").append(ordinaryMax);
        sb.append(", matrix=").append(Arrays.toString(matrix));
        sb.append(", toatl=").append(Arrays.toString(total));
        sb.append(", totalFrequency=").append(totalFrequency);
        sb.append('}');
        return sb.toString();
    }

}
