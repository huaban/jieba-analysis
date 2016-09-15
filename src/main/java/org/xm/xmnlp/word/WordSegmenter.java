package org.xm.xmnlp.word;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.recognition.StopWord;
import org.xm.xmnlp.word.segmentation.SegmentationAlgorithm;
import org.xm.xmnlp.word.segmentation.SegmentationFactory;
import org.xm.xmnlp.word.segmentation.Word;

import java.util.List;

/**
 * Created by mingzai
 */
public class WordSegmenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordSegmenter.class);

    public static List<Word> seg(String text, SegmentationAlgorithm algorithm) {
        return SegmentationFactory.getSegmentation(algorithm).seg(text);
    }

    public static List<Word> seg(String text) {
        return SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaxNgramScore).seg(text);
    }

    public static List<Word> segWithoutStopWord(String text, SegmentationAlgorithm algorithm) {
        List<Word> words = SegmentationFactory.getSegmentation(algorithm).seg(text);
        StopWord.filterStopWords(words);
        return words;
    }

    public static List<Word> segWithoutStopWord(String text) {
        List<Word> words = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaxNgramScore).seg(text);
        StopWord.filterStopWords(words);
        return words;
    }

}
