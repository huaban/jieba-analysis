package org.xm.xmnlp.hanlp.corpus.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;

import static org.xm.xmnlp.hanlp.HanLP.Config.IOAdapter;
import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class IOUtil {
    public static boolean saveObjectTo(Object o ,String path){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(IOUtil.newOutputStream(path));
            oos.writeObject(o);
            oos.close();
        }catch (IOException e){
            logger.warning("在保存对象" + o + "到" + path + "时发生异常" + e);
            return false;
        }
        return true;
    }
    public static Object readObjectFrom(String path){
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(IOUtil.newInputStream(paht));
            Object o = ois.readObject();
            ois.close();
            return o;
        }catch (Exception e){
            logger.warning("在从" + path + "读取对象时发生异常" + e);
        }
        return null;
    }
    public static String readTxt(String path){
        if(path == null) return null;
        try{
            InputStream in = IOAdapter ==null?new FileInputStream(path):IOAdapter.open(path);
            byte[] fileContent = new byte[in.available()];
            readBytesFromOtherInputStream(in,fileContent);
            in.close();
            return new String(fileContent, Charset.forName("UTF-8"));
        }catch (FileNotFoundException e){
            logger.warning("找不到" + path + e);
            return null;
        }catch (IOException e1){
            logger.warning("读取" + path + "发生IO异常" + e1);
            return null;
        }
    }
    public static LinkedList<String[]> readCsv(String path){
        LinkedList<String[]> resultList = new LinkedList<>();
        LinkedList<String> lineList = readLineList(path);
        for(String line: lineList){
            resultList.add(line.split(","));
        }
        return resultList;
    }
    public static boolean saveTxt(String path,String content){
        try{
            FileChannel fc = new FileOutputStream(path).getChannel();
            fc.write(ByteBuffer.wrap(content.getBytes()));
            fc.close();
        }catch (Exception e){
            logger.throwing("IOUtil","saveTxt",e);
            logger.warning("IOUtil saveTxt 到" + path + "失败" + e.toString());
            return false;
        }
        return true;
    }
    public static boolean saveTxt(String path,StringBuilder content){
        return saveTxt(path,content.toString());
    }

    public static <T> boolean saveColloectionToTxt(Collection<T> collection,String path){
        StringBuilder sb = new StringBuilder();
        for(Object o : collection){
            sb.append(o);
            sb.append('\n');
        }
        return saveTxt(path,sb.toString());
    }
    public static byte[] readBytes(String path){
        try{
            if(IOAdapter ==null){
                return readBytesFromFileInputStream(new FileInputStream(path));
            }
            InputStream is = IOAdapter.open(path);
            if(is instanceof FileInputStream){
                return readBytesFromFileInputStream((FileInputStream) is);
            }else {
                return readBytesFromOtherInputStream(is);
            }

        }catch (Exception e){
            logger.warning("读取" + path + "时发生异常" + e);
        }
        return null;
    }
    private static byte[] readBytesFromFileInputStream(FileInputStream fis) throws IOException{
        FileChannel channel = fis .getChannel();
        int fileSize = (int)channel.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
        channel.read(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = byteBuffer.array();
        byteBuffer.clear();
        channel.close();
        fis.close();
        return bytes;
    }
    public static byte[] readBytesFromOtherInputStream(InputStream is)throws IOException{
        byte[] targetArray = new byte[is.available()];
        readBytesFromOtherInputStream(is,targetArray);
        is.close();
        return targetArray;
    }
    public  static void readBytesFromOtherInputStream(InputStream is,byte[] targetArray)throws  IOException{
        int len ;
        int off = 0;
        while((len = is.read(targetArray,off,targetArray.length - off))!= -1 && off< targetArray.length){
            off += len;
        }
    }

    public static String readTxt(String file ,String charsetName) throws IOException{
        InputStream is = IOAdapter.open(file);
        byte[] targetArray = new byte[is.available()];
        int len;
        int off = 0;
        while((len = is.read(targetArray,off,targetArray.length - off)) != -1 && off<targetArray.length){
            off +=len;
        }
        is.close();
        return new String(targetArray,charsetName);
    }
}
