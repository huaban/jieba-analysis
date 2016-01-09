package com.huaban.analysis.jieba.keyword;
import java.util.HashSet;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;

public class KeywordExtractor {
	protected HashSet<String> stopWords;
	private static final String[] STOP_WORDS = {
            "the", "of", "is", "and", "to", "in", "that", "we", "for", "an", "are",
            "by", "be", "as", "on", "with", "can", "if", "from", "which", "you", "it",
            "this", "then", "at", "have", "all", "not", "one", "has", "or", "that"};
	
	public KeywordExtractor() {
		stopWords = new HashSet<String>();
	}
	
    public void loadStopWords(Path stopWordsPath,Charset charset) {
    	try {
            BufferedReader br = Files.newBufferedReader(stopWordsPath, charset);
            long s = System.currentTimeMillis();
            int count = 0;
            while (br.ready()) {
                String line = br.readLine();
                String word = line;
                stopWords.add(word);
                count++;
            }
            System.out.println(String.format(Locale.getDefault(), "Stop words %s load finished, tot words:%d, time elapsed:%dms", stopWordsPath.toString(), count, System.currentTimeMillis() - s));
            br.close();
        }
        catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s: load user dict failure!", stopWordsPath.toString()));
        }
    }
    
    public void loadStopWords(Path stopWordsPath) {
    	Charset charset = Charset.forName("UTF-8");
    	loadStopWords(stopWordsPath,charset);
    }
    
    public void loadStopWords() {
    	Path path = FileSystems.getDefault().getPath(".", "/conf/stop_words.dict");
    	Charset charset = Charset.forName("UTF-8");
    	loadStopWords(path,charset);
    }
    
}
