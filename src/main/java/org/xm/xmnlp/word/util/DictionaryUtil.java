package org.xm.xmnlp.word.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by xuming
 */
public class DictionaryUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryUtil.class);

    public static List<String> loadDictionaryFile(String path) {
        LOGGER.info("dict load start: " + path);
        List<String> lines = new ArrayList<>();
        InputStream is = null;
        try {
            if (path.startsWith("classpath:") || path.startsWith("/")) {
                is = DictionaryUtil.class.getResourceAsStream(path.replace("classpath:", ""));
            } else {
                is = new FileInputStream(path);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            long start = System.currentTimeMillis();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if ("".equals(line) || line.startsWith("#")) {
                    continue;
                }
                if (!lines.contains(line)) {
                    lines.add(line);
                }
            }
            if (null != br) br.close();
            LOGGER.info(String.format(Locale.getDefault(), "%s,dict load finished,spend %d ms. ",
                    path, System.currentTimeMillis() - start));
        } catch (IOException e) {
            LOGGER.info("dict load failure!" + path);
        } finally {
            try {
                if (null != is) is.close();
            } catch (IOException e) {
                LOGGER.info(String.format(Locale.getDefault(), "%s close failure", path));
            }
        }
        return lines;

    }
}
