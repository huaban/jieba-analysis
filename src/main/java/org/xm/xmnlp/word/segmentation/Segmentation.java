package org.xm.xmnlp.word.segmentation;

import java.util.List;

/**
 * Created by mingzai on 2016/9/11.
 */
public interface Segmentation {
    List<Word> seg(String text);

    SegmentationAlgorithm getSegmentationAlgorithm();
}
