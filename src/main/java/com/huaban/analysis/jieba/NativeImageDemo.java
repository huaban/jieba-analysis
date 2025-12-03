package com.huaban.analysis.jieba;

import java.util.List;

/**
 * Simple demo application for testing Native Image build
 */
public class NativeImageDemo {

    public static void main(String[] args) {
        System.out.println("=== Jieba Analysis Native Image Demo ===");
        System.out.println("Running in Native Image: " + ResourceLoader.isNativeImage());
        System.out.println();

        // Test basic segmentation
        JiebaSegmenter segmenter = new JiebaSegmenter();

        String[] testSentences = {
                "这是一个伸手不见五指的黑夜。",
                "我叫孙悟空,我爱北京,我爱Python和C++。",
                "结巴分词是一个很好用的中文分词工具。"
        };

        System.out.println("Testing segmentation:");
        for (String sentence : testSentences) {
            System.out.println("\nInput: " + sentence);
            List<String> tokens = segmenter.sentenceProcess(sentence);
            System.out.println("Output: " + tokens);
        }

        System.out.println("\n=== Demo completed successfully! ===");
    }
}
