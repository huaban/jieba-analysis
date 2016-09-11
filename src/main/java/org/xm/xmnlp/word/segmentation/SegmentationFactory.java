package org.xm.xmnlp.word.segmentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mingzai on 2016/9/11.
 */
public class SegmentationFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentationFactory.class);
    private static final Map<String, Segmentation> pool = new HashMap<>();

    private SegmentationFactory() {
    }

    public static synchronized Segmentation getSegmentation(SegmentationAlgorithm segmentationAlgorithm) {
        String clazz = "org.xm.xmnlp.word.segmentation.impl." + segmentationAlgorithm.name();
        Segmentation segmentation = pool.get(clazz);
        if (segmentation == null) {
            LOGGER.info("build segmentation class:" + clazz);
            try {
                segmentation = (Segmentation) Class.forName(clazz).newInstance();
                pool.put(clazz, segmentation);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                LOGGER.info("build segmentation class failure:" + e);
            }
        }
        return segmentation;
    }
}
