package org.xm.xmnlp.word.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mingzai on 2016/9/11.
 */
public class WordConfTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordConfTools.class);

    private static final Map<String, String> conf = new HashMap<>();

    public static void set(String key, String value) {
        conf.put(key, value);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = conf.get(key) == null ? Boolean.valueOf(defaultValue).toString() : conf.get(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("get conf:" + key + " = " + value);
        }
        return value.contains("true");
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static int getInt(String key, int defaultValue) {
        int value = conf.get(key) == null ? defaultValue : Integer.parseInt(conf.get(key).trim());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("get conf:" + key + " = " + value);
        }
        return value;
    }

    public static int getInt(String key) {
        return getInt(key, -1);
    }

    public static String get(String key, String defaultValue) {
        String value = conf.get(key) == null ? defaultValue : conf.get(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("获取配置项：" + key + "=" + value);
        }
        return value;
    }

    public static String get(String key) {
        String value = conf.get(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("获取配置项：" + key + "=" + value);
        }
        return value;
    }

    static {
        reload();
    }

    public static void reload() {
        conf.clear();
        LOGGER.info("start load conf file:");
        long s = System.currentTimeMillis();
        loadConf("word.conf");
        //loadConf("word.local.conf");
        checkSystemProperties();
        long cost = System.currentTimeMillis() - s;
        LOGGER.info("load conf file finished. spend time:" + cost + " ms, conf num:" + conf.size());
        LOGGER.info("conf info:");
        AtomicInteger i = new AtomicInteger();
        conf.keySet()
                .stream()
                .sorted()
                .forEach(
                        key -> LOGGER.debug(i.incrementAndGet() + ". " + key + "=" + conf.get(key))
                );
    }

    private static void loadConf(String confFile) {
        InputStream is = WordConfTools.class.getClassLoader().getResourceAsStream(confFile);
        if (is == null) {
            LOGGER.info("not found conf file:" + confFile);
            return;
        }
        LOGGER.info("load conf file:" + confFile);
        loadConf(is);
    }

    private static void loadConf(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if ("".equals(line) || line.startsWith("#")) {
                    continue;
                }
                int index = line.indexOf("=");
                if (index == -1) {
                    LOGGER.error("conf error:" + line);
                    continue;
                }
                if (index > 0 && line.length() > index + 1) {//has K V
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1, line.length()).trim();
                    conf.put(key, value);
                } else if (index > 0 && line.length() == index + 1) {//has K no V
                    String key = line.substring(0, index).trim();
                    conf.put(key, "");
                } else {
                    LOGGER.error("conf error:" + line);
                }
            }
        } catch (IOException e) {
            System.err.println("conf load error:" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) is.close();
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }


    private static void checkSystemProperties() {
        for (String key : conf.keySet()) {
            String value = System.getProperty(key);
            if (value != null) {
                conf.put(key, value);
                LOGGER.info("cover the default conf:" + key + "=" + value);
            }
        }
    }
}
