package org.xm.xmnlp.word.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        List<String> lines = new ArrayList<>();
        Class currentClass = new Object() {
        }.getClass().getEnclosingClass();
        InputStream is = currentClass.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        try {
            long start = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!lines.contains(line)) {
                    lines.add(line);
                }
            }
            LOGGER.info(String.format(Locale.getDefault(), "%s,dict load finished,spend %d ms. ",
                    path, System.currentTimeMillis() - start));
        } catch (IOException e) {
            LOGGER.info("dict load failure!" + path);
        } finally {
            try {
                if (null != br) br.close();
                if (null != is) is.close();
            } catch (IOException e) {
                LOGGER.info(String.format(Locale.getDefault(), "%s close failure", path));
            }
        }
        return lines;

    }
}
