package com.huaban.analysis.jieba;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordDictionaryTest extends TestCase {

    private static final String TERM="豐碩";
    private static final String FILE_PATH="src/test/resources/dict_big.txt";
    private Path path;

    @Before
    @Override
    protected void setUp() throws Exception {
        path = Paths.get(FILE_PATH);
        //WordDictionary.getInstance().init(new File("conf"));
    }

    @Test
    public void test01_singleThread(){
        WordDictionary.getInstance().loadUserDict(path);
        boolean isContainsWord = WordDictionary.getInstance().containsWord(TERM);
        assertTrue(isContainsWord);
    }

    @Test
    public void test02_multiThread(){
        int threadSize= 10;
        ExecutorService service = Executors.newCachedThreadPool();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        for(int i=0;i<threadSize;i++){
            Future<?> future =service.submit(new Callable<Boolean>(){
                @Override
                public Boolean call() throws Exception {
                    WordDictionary.getInstance().loadUserDict(path);
                    boolean isContainsWord = WordDictionary.getInstance().containsWord(TERM);
                    assertTrue(isContainsWord);
                    return isContainsWord;
                }
            });
            futures.add(future);
        }

        for(Future<?> future: futures){
            try{
                boolean isContains = (Boolean)future.get();
                Assert.assertTrue(isContains);
            }catch (Exception e){
                Assert.assertNotNull(e);
            }
        }
    }
}
