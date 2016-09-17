package org.xm.xmnlp.hanlp.dictionary.other;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.io.ObjectInputStream;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class CharTable {
    public static char[] CONVERT;
    static {
        long start = System.currentTimeMillis();
        if(!load(HanLP.Config.CharTablePath)){
            logger.severe("字符正规化表加载失败");
            System.exit(-1);
        }
        logger.info("字符正规化表加载成功：" + (System.currentTimeMillis() - start) + " ms");
    }
    private static boolean load(String path){
        String binPath = path + Predefine.BIN_EXT;
        if(loadBin(binPath)) return true;
        CONVERT = new char[Character.MAX_VALUE +1];
        for(int i =0;i<CONVERT.length;i++){
            CONVERT[i] = (char)i;
        }
        IOUtil.LineIterator iterator = new IOUtil.LineIterator(path);
        while(iterator.hasNext()){
            String line = iterator.next();
            if(line == null)return false;
            if(line .length() !=3)continue;
            CONVERT[line.charAt(0)] = CONVERT[line.charAt(2)];
        }
        logger.info("正在缓存字符正规化表到" + binPath);
        IOUtil.saveObjectTo(CONVERT,binPath);
        return true;
    }
    private static boolean loadBin(String path){
        try{
            ObjectInputStream in = new ObjectInputStream(IOUtil.newInputStream(path));
            CONVERT = (char[] )in.readObject();
            in.close();
        }catch (Exception e){
            logger.warning("字符正规化表缓存加载失败，原因如下：" + e);
            return false;
        }
        return true;
    }
    public static char convert(char c){
        return CONVERT[c];
    }
    public static char[] convert(char[] charArray){
        char[] result = new char[charArray.length];
        for(int i =0;i<charArray.length;i++){
            result[i] = CONVERT[charArray[i]];
        }
        return result;
    }
    public static String convert(String charArray){
        assert charArray !=null;
        char[] result = new char[charArray.length()];
        for(int i = 0;i<charArray.length();i++){
            result[i] = CONVERT[charArray.charAt(i)];
        }
        return new String(result);
    }
    public static void normalization(char[] charArray){
        assert charArray !=null;
        for(int i =0;i<charArray.length;i++){
            charArray[i] = CONVERT[charArray[i]];
        }
    }
}
