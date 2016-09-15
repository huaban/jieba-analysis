package org.xm.xmnlp.hanlp.seg;

/**
 * @author xuming
 */
public class Config {
    public boolean indexMode = false;
    public boolean nameRecognize = true;
    public boolean translatedNameRecognize = true;
    public boolean japaneseNameRecognize = false;
    public boolean placeRecognize = false;
    public boolean organizationRecognize = false;
    public boolean useCustomDictionary = true;
    public boolean speechTagging = false;
    public boolean ner = true;
    public boolean offset = false;
    public boolean numberQuantifierRecognize = false;
    public int threadNumber = 1;

    public void updateNerConfig() {
        ner = nameRecognize || translatedNameRecognize || japaneseNameRecognize || placeRecognize || organizationRecognize;
    }
}
