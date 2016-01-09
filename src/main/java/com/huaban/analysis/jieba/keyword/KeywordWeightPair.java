package com.huaban.analysis.jieba.keyword;

public class KeywordWeightPair {
	public String key = "";
    public Double weight = 0.0;

    public KeywordWeightPair(String key, double weight) {
	this.key = key;
	this.weight = weight;
    }

    @Override
    public String toString() {
	return "(" + key + "," + weight +")";
    }
}
