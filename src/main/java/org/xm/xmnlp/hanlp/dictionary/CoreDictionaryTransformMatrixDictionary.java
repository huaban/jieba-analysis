package org.xm.xmnlp.hanlp.dictionary;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class CoreDictionaryTransformMatrixDictionary {
    public static TransformMatrixDictionary<Nature> transformMatrixDictionary;

    static {
        transformMatrixDictionary = new TransformMatrixDictionary<Nature>(Nature.class);
        if (!transformMatrixDictionary.load(HanLP.Config.CoreDictionaryTransformMatrixDictionaryPath)) {
            System.err.println("load dict:" + HanLP.Config.CoreDictionaryTransformMatrixDictionaryPath);
            System.exit(-1);
        } else {
            logger.info("load succeed:" + HanLP.Config.CoreDictionaryTransformMatrixDictionaryPath);
        }
    }
}
