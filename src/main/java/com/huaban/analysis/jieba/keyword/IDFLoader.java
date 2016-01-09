package com.huaban.analysis.jieba.keyword;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
public class IDFLoader {
	private Path path;
	private HashMap<String,Double> idfFreq;
	private double medianIDF;
    public IDFLoader(Path idfPath) {
        init(idfPath);
    }
    
    
    public void init(Path idfPath) {
    	idfFreq = new HashMap<String,Double>();
    	medianIDF = 0.0;
    	setNewPath(idfPath);
    }
    
    public void setNewPath(Path newIDFPath) {
    	ArrayList<Double> toSort = new ArrayList<Double>();
    	if(path == null || !path.toString().equals(newIDFPath.toString())) {
    		path = newIDFPath;
    		try {
                BufferedReader br = Files.newBufferedReader(path);
                long s = System.currentTimeMillis();
                int count = 0;
                while (br.ready()) {
                    String line = br.readLine();
                    String[] arr = line.split(" ");
                    if(arr.length < 2)
                    	continue;
                    String word = arr[0];
                    double idf = Double.parseDouble(arr[1]);
                    idfFreq.put(word,idf);
                    toSort.add(idf);
                    count++;
                }
                System.out.println(String.format(Locale.getDefault(), "idf %s load finished, tot words:%d, time elapsed:%dms", newIDFPath.toString(), count, System.currentTimeMillis() - s));
                br.close();
                
            }
            catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s: load idf failure!", newIDFPath.toString()));
            }
    		Collections.sort(toSort);
    		medianIDF = toSort.get(toSort.size()/2);
    		
    	}
    }
    
    public HashMap<String,Double> idfFreq() {
    	return idfFreq;
    }
    
    public double medianIDF(){
    	return medianIDF;
    }
    
    public static void main(String[] args){
    	
    	Path path = FileSystems.getDefault().getPath(".", "/conf/idf.txt.big");
    	IDFLoader idfld = new IDFLoader(path);
    	
    }
    
}
