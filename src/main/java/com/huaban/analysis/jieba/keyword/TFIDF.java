package com.huaban.analysis.jieba.keyword;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Comparator;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.Pair;

/**
 * @author cndn         
 */
public class TFIDF extends KeywordExtractor{
	private JiebaSegmenter tokenizer;
	private IDFLoader idfLoader;
	private HashMap<String,Double> idfFreq;
	private double medianIDF;
	private static final Path DEFAULT_IDF_PATH = FileSystems.getDefault().getPath(".", "/conf/idf.txt");
	
	public TFIDF(Path idfPath) {
	    init(idfPath);		
	}
	public TFIDF() {
		init(DEFAULT_IDF_PATH);
	}
	
	public void init(Path idfPath) {
		tokenizer = new JiebaSegmenter();
		idfLoader = new IDFLoader(idfPath);
	    idfFreq = idfLoader.idfFreq();
	    medianIDF = idfLoader.medianIDF();
	}
	
	public void setIDFPath(Path idfPath) {
		idfLoader.setNewPath(idfPath);
		idfFreq = idfLoader.idfFreq();
	    medianIDF = idfLoader.medianIDF();
	}
	
	/**
     * Extract keywords from sentence using TF-IDF algorithm.
     * Parameters allowPOS and withFlag to be added.
     * 
     * @param sentence
     *        - Sentence to extract
     * @param topK
     *        - Return number of top keywords, -1 for all words
     * 
     * @return tags
     *        - an ArrayList of KeywordWeightsPair (word,weight)
     */
	public List<KeywordWeightPair> extractTagsWithWeights(String sentence, int topK) {
		List<Entry<String,Double>> sortedList = extractTagsSortListHelper(sentence);
		List<KeywordWeightPair> tags = new ArrayList<KeywordWeightPair>();
        if(topK == -1) topK = Integer.MAX_VALUE;
		int count = 0;
		for (Entry<String, Double>entry: sortedList) {
			if(count >= topK)
				break;
			tags.add(new KeywordWeightPair(entry.getKey(),entry.getValue()));
			count++;
		}
		return tags;
	}
	/**
     * Extract keywords from sentence using TF-IDF algorithm.
     * Parameters allowPOS and withFlag to be added.
     * 
     * @param sentence
     *        - Sentence to extract
     * @param topK
     *        - Return number of top keywords, -1 for all words
     * 
     * @return tags
     *        - an ArrayList of String
     */
	public List<String> extractTags(String sentence, int topK) {
		List<Entry<String,Double>> sortedList = extractTagsSortListHelper(sentence);
		List<String> tags = new ArrayList<String>();
        if(topK == -1) topK = Integer.MAX_VALUE;
		int count = 0;
		for (Entry<String, Double>entry: sortedList) {
			if(count >= topK)
				break;
			tags.add(entry.getKey());
			count++;
		}
		return tags;
	}
	
	public List<String> extractTags(String sentence) {
		return extractTags(sentence,-1);
	}
	
	public List<KeywordWeightPair> extractTagsWithWeights(String sentence) {
		return extractTagsWithWeights(sentence,-1);
	}
	/**
     * Extract keywords helper function
     * 
     * @param sentence
     *        - Sentence to extract
     * @return sortList
     *        - an ArrayList of Entry<String,Double> where keywords are sorted by values(tf-idf)
     */
	private List<Entry<String,Double>> extractTagsSortListHelper(String sentence) {
		double total = 0.0;
		double idf;
		List<String> words = tokenizer.sentenceProcess(sentence);
		HashMap<String,Double> freq = new HashMap<String,Double>();
		for(String word:words) {
			if(word.trim().length() < 2 || stopWords.contains(word.toLowerCase())) {
				continue;
			}	
			freq.put(word, freq.containsKey(word)? (freq.get(word)+1.0):1.0);
		}
		for(double val:freq.values())
			total += val;
		for(String k:freq.keySet()) {
			idf = idfFreq.containsKey(k)? idfFreq.get(k):medianIDF;
			freq.put(k, freq.get(k)*idf/total);
		}
		List<Entry<String,Double>> sortList = new ArrayList<Entry<String,Double>>(freq.entrySet());
		Collections.sort(sortList, new Comparator<Map.Entry<String,Double>>() {
			public int compare(Map.Entry<String, Double> map1, Map.Entry<String,Double> map2) {
				double diff = map2.getValue() - map1.getValue();
				if(diff > 0) return 1;
				else return -1;
			}
		});
		return sortList;
	}
	
    
}
