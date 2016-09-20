package org.xm.xmnlp.hanlp.model;

import org.xm.xmnlp.hanlp.collection.trie.ITrie;
import org.xm.xmnlp.hanlp.corpus.io.ByteArray;
import org.xm.xmnlp.hanlp.model.crf.CRFModel;
import org.xm.xmnlp.hanlp.model.crf.FeatureFunction;
import org.xm.xmnlp.hanlp.model.crf.Table;

import java.util.LinkedList;

/**
 * @author xuming
 */
public class CRFSegmentModel extends CRFModel {
    private int idM;
    private int idE;
    private int idS;

    /**
     * 不允许构造空白实例
     */
    private CRFSegmentModel() {
    }

    /**
     * 以指定的trie树结构储存内部特征函数
     *
     * @param featureFunctionTrie
     */
    public CRFSegmentModel(ITrie<FeatureFunction> featureFunctionTrie) {
        super(featureFunctionTrie);
    }

    /**
     * 初始化几个常量
     */
    private void initTagSet() {
        idM = this.getTagId("M");
        idE = this.getTagId("E");
        idS = this.getTagId("S");
    }

    @Override
    public boolean load(ByteArray byteArray) {
        boolean result = super.load(byteArray);
        if (result) {
            initTagSet();
        }

        return result;
    }

    @Override
    protected void onLoadTxtFinished() {
        super.onLoadTxtFinished();
        initTagSet();
    }

    @Override
    public void tag(Table table) {
        int size = table.size();
        if (size == 1) {
            table.setLast(0, "S");
            return;
        }
        double[][] net = new double[size][4];
        for (int i = 0; i < size; ++i) {
            LinkedList<double[]> scoreList = computeScoreList(table, i);
            for (int tag = 0; tag < 4; ++tag) {
                net[i][tag] = computeScore(scoreList, tag);
            }
        }
        net[0][idM] = -1000.0;  // 第一个字不可能是M或E
        net[0][idE] = -1000.0;
        int[][] from = new int[size][4];
        for (int i = 1; i < size; ++i) {
            for (int now = 0; now < 4; ++now) {
                double maxScore = -1e10;
                for (int pre = 0; pre < 4; ++pre) {
                    double score = net[i - 1][pre] + matrix[pre][now] + net[i][now];
                    if (score > maxScore) {
                        maxScore = score;
                        from[i][now] = pre;
                    }
                }
                net[i][now] = maxScore;
            }
        }
        // 反向回溯最佳路径
        int maxTag = net[size - 1][idS] > net[size - 1][idE] ? idS : idE;
        table.setLast(size - 1, id2tag[maxTag]);
        maxTag = from[size - 1][maxTag];
        for (int i = size - 2; i > 0; --i) {
            table.setLast(i, id2tag[maxTag]);
            maxTag = from[i][maxTag];
        }
        table.setLast(0, id2tag[maxTag]);
    }
}
