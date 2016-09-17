package org.xm.xmnlp.hanlp.corpus.io;

import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.utility.TextUtility;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

import static org.xm.xmnlp.hanlp.HanLP.Config.IOAdapter;
import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class IOUtil {
    public static boolean saveObjectTo(Object o, String path) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(IOUtil.newOutputStream(path));
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            logger.warning("在保存对象" + o + "到" + path + "时发生异常" + e);
            return false;
        }
        return true;
    }

    public static Object readObjectFrom(String path) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(IOUtil.newInputStream(path));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            logger.warning("在从" + path + "读取对象时发生异常" + e);
        }
        return null;
    }

    public static String readTxt(String path) {
        if (path == null) return null;
        try {
            InputStream in = IOAdapter == null ? new FileInputStream(path) : IOAdapter.open(path);
            byte[] fileContent = new byte[in.available()];
            readBytesFromOtherInputStream(in, fileContent);
            in.close();
            return new String(fileContent, Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            logger.warning("找不到" + path + e);
            return null;
        } catch (IOException e1) {
            logger.warning("读取" + path + "发生IO异常" + e1);
            return null;
        }
    }

    public static LinkedList<String[]> readCsv(String path) {
        LinkedList<String[]> resultList = new LinkedList<>();
        LinkedList<String> lineList = readLineList(path);
        for (String line : lineList) {
            resultList.add(line.split(","));
        }
        return resultList;
    }

    public static boolean saveTxt(String path, String content) {
        try {
            FileChannel fc = new FileOutputStream(path).getChannel();
            fc.write(ByteBuffer.wrap(content.getBytes()));
            fc.close();
        } catch (Exception e) {
            logger.throwing("IOUtil", "saveTxt", e);
            logger.warning("IOUtil saveTxt 到" + path + "失败" + e.toString());
            return false;
        }
        return true;
    }

    public static boolean saveTxt(String path, StringBuilder content) {
        return saveTxt(path, content.toString());
    }

    public static String readTxt(String file, String charsetName) throws IOException {
        InputStream is = IOAdapter.open(file);
        byte[] targetArray = new byte[is.available()];
        int len;
        int off = 0;
        while ((len = is.read(targetArray, off, targetArray.length - off)) != -1 && off < targetArray.length) {
            off += len;
        }
        is.close();
        return new String(targetArray, charsetName);
    }

    public static <T> boolean saveColloectionToTxt(Collection<T> collection, String path) {
        StringBuilder sb = new StringBuilder();
        for (Object o : collection) {
            sb.append(o);
            sb.append('\n');
        }
        return saveTxt(path, sb.toString());
    }

    public static byte[] readBytes(String path) {
        try {
            if (IOAdapter == null) {
                return readBytesFromFileInputStream(new FileInputStream(path));
            }
            InputStream is = IOAdapter.open(path);
            if (is instanceof FileInputStream) {
                return readBytesFromFileInputStream((FileInputStream) is);
            } else {
                return readBytesFromOtherInputStream(is);
            }

        } catch (Exception e) {
            logger.warning("读取" + path + "时发生异常" + e);
        }
        return null;
    }

    private static byte[] readBytesFromFileInputStream(FileInputStream fis) throws IOException {
        FileChannel channel = fis.getChannel();
        int fileSize = (int) channel.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
        channel.read(byteBuffer);
        byteBuffer.flip();
        byte[] bytes = byteBuffer.array();
        byteBuffer.clear();
        channel.close();
        fis.close();
        return bytes;
    }

    public static byte[] readBytesFromOtherInputStream(InputStream is) throws IOException {
        byte[] targetArray = new byte[is.available()];
        readBytesFromOtherInputStream(is, targetArray);
        is.close();
        return targetArray;
    }

    public static void readBytesFromOtherInputStream(InputStream is, byte[] targetArray) throws IOException {
        int len;
        int off = 0;
        while ((len = is.read(targetArray, off, targetArray.length - off)) != -1 && off < targetArray.length) {
            off += len;
        }
    }

    public static LinkedList<String> readLineList(String path) {
        LinkedList<String> result = new LinkedList<>();
        String txt = readTxt(path);
        if (txt == null) return result;
        StringTokenizer tokenizer = new StringTokenizer(txt, "\n");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }

    public static LinkedList<String> readLineListWithLessMemory(String path) {
        LinkedList<String> result = new LinkedList<>();
        String line = null;
        try {
            BufferedReader bw = newBufferedReader(path);
            while ((line = bw.readLine()) != null) {
                result.add(line);
            }
            bw.close();
        } catch (Exception e) {
            logger.warning("加载" + path + "失败，" + e);
        }
        return result;
    }

    public static InputStream newInputStream(String path) throws IOException {
        if (IOAdapter == null) return new FileInputStream(path);
        return IOAdapter.open(path);
    }

    public static OutputStream newOutputStream(String path) throws IOException {
        if (IOAdapter == null) return new FileOutputStream(path);
        return IOAdapter.create(path);
    }

    public static BufferedReader newBufferedReader(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(newInputStream(path), "UTF-8"));
    }

    public static BufferedWriter newBufferedWriter(String path) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(newOutputStream(path), "UTF-8"));
    }

    public static BufferedWriter newBufferedWriter(String path, boolean append) throws FileNotFoundException, UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), "UTF-8"));
    }

    public static boolean saveMapToTxt(Map<Object, Object> map, String path) {
        return saveMapToTxt(map, path, "=");
    }

    public static boolean saveMapToTxt(Map<Object, Object> map, String path, String separator) {
        map = new TreeMap<>(map);
        return saveEntrySetToTxt(map.entrySet(), path, separator);
    }

    public static boolean saveEntrySetToTxt(Set<Map.Entry<Object, Object>> entries, String path, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : entries) {
            sb.append(entry.getKey());
            sb.append(separator);
            sb.append(entry.getValue());
            sb.append('\n');
        }
        return saveTxt(path, sb.toString());
    }

    public static String dirName(String path) {
        int index = path.lastIndexOf('/');
        if (index == -1) return path;
        return path.substring(0, index + 1);
    }

    public static LineIterator readLine(String path) {
        return new LineIterator(path);
    }

    public static class LineIterator implements Iterator<String> {
        BufferedReader bw;
        String line;

        public LineIterator(String path) {
            try {
                bw = newBufferedReader(path);
                line = bw.readLine();
            } catch (FileNotFoundException e) {
                logger.warning("文件" + path + "不存在，接下来的调用会返回null" + TextUtility.exceptionToString(e));
                bw = null;
            } catch (IOException e) {
                logger.warning("在读取过程中发生错误" + TextUtility.exceptionToString(e));
                bw = null;
            }
        }

        public void close() {
            if (bw == null) return;
            try {
                bw.close();
                bw = null;
            } catch (IOException e) {
                logger.warning("关闭文件失败" + TextUtility.exceptionToString(e));
            }
            return;
        }

        @Override
        public boolean hasNext() {
            if (bw == null) return false;
            if (line == null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    logger.warning("关闭文件失败" + TextUtility.exceptionToString(e));
                }
                return false;
            }
            return true;
        }

        @Override
        public String next() {
            String preLine = line;
            try {
                if (bw != null) {
                    line = bw.readLine();
                    if (line == null && bw != null) {
                        try {
                            bw.close();
                            bw = null;
                        } catch (IOException e) {
                            logger.warning("关闭文件失败" + TextUtility.exceptionToString(e));
                        }
                    }
                } else {
                    line = null;
                }
            } catch (IOException e) {
                logger.warning("在读取过程中发生错误" + TextUtility.exceptionToString(e));
            }
            return preLine;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("只读，不可写！");
        }

    }

    /**
     * 获取最后一个分隔符的后缀
     *
     * @param name
     * @param delimiter
     * @return
     */
    public static String getSuffix(String name, String delimiter) {
        return name.substring(name.lastIndexOf(delimiter) + 1);
    }

    public static void writeLine(BufferedWriter bw, String... params) throws IOException {
        for (int i = 0; i < params.length - 1; i++) {
            bw.write(params[i]);
            bw.write('\t');
        }
        bw.write(params[params.length - 1]);
    }

    public static TreeMap<String, CoreDictionary.Attribute> loadDictionary(String... pathArray) throws IOException {
        TreeMap<String, CoreDictionary.Attribute> map = new TreeMap<>();
        for (String path : pathArray) {
            BufferedReader br = newBufferedReader(path);
            loadDictionary(br, map);
        }
        return map;
    }

    public static void loadDictionary(BufferedReader br, TreeMap<String, CoreDictionary.Attribute> storage) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            String param[] = line.split("\\s");
            int natureCount = (param.length - 1) / 2;
            CoreDictionary.Attribute attribute = new CoreDictionary.Attribute(natureCount);
            for (int i = 0; i < natureCount; ++i) {
                attribute.nature[i] = Enum.valueOf(Nature.class, param[1 + 2 * i]);
                attribute.frequency[i] = Integer.parseInt(param[2 + 2 * i]);
                attribute.totalFrequency += attribute.frequency[i];
            }
            storage.put(param[0], attribute);
        }
        br.close();
    }

    public static void writeCustomNature(DataOutputStream out, LinkedHashSet<Nature> customNatureCollector) throws IOException {
        if (customNatureCollector.size() == 0) return;
        out.writeInt(-customNatureCollector.size());
        for (Nature nature : customNatureCollector) {
            TextUtility.writeString(nature.toString(), out);
        }
    }
}
