package org.xm.xmnlp.hanlp.model.crf;

import org.xm.xmnlp.hanlp.corpus.io.ByteArray;
import org.xm.xmnlp.hanlp.corpus.io.ICacheAble;

import java.io.DataOutputStream;

/**
 * @author xuming
 */
public class FeatureFunction implements ICacheAble {
    /**
     * 环境参数
     */
    char[] o;
    /**
     * 标签参数
     */
//    String s;

    /**
     * 权值，按照index对应于tag的id
     */
    double[] w;

    public FeatureFunction(char[] o, int tagSize) {
        this.o = o;
        w = new double[tagSize];
    }

    public FeatureFunction() {
    }

    @Override
    public void save(DataOutputStream out) throws Exception {
        out.writeInt(o.length);
        for (char c : o) {
            out.writeChar(c);
        }
        out.writeInt(w.length);
        for (double v : w) {
            out.writeDouble(v);
        }
    }

    @Override
    public boolean load(ByteArray byteArray) {
        int size = byteArray.nextInt();
        o = new char[size];
        for (int i = 0; i < size; ++i) {
            o[i] = byteArray.nextChar();
        }
        size = byteArray.nextInt();
        w = new double[size];
        for (int i = 0; i < size; ++i) {
            w[i] = byteArray.nextDouble();
        }
        return true;
    }
}
