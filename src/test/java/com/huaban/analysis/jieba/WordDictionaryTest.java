/**
 * (C) Copyright 2017 alex qian
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.huaban.analysis.jieba;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 *
 * WordDictionaryTest
 *
 * @author alex.Q
 * @date 2017/7/29
 */
public class WordDictionaryTest {

    @Test
    public void test_loadDict() throws URISyntaxException {
        WordDictionary wordDict = WordDictionary.getInstance();
        double d = wordDict.getFreq("司机");
        System.out.println(d);
        URL url = ClassLoader.getSystemResource("test_user.dict");
        wordDict.loadUserDict(Paths.get(url.toURI()));
        d = wordDict.getFreq("司机");
        System.out.println(d);
    }

    @Test
    public void test_useDefaultDict() {
        WordDictionary wordDict = WordDictionary.getInstance();
        Assert.assertTrue(wordDict.isUseDefaultDict());
    }

    @Test
    public void test_useDefaultDict2() {
        System.setProperty("jieba.defaultDict", "false");
        WordDictionary wordDict = WordDictionary.getInstance();
        Assert.assertTrue(wordDict.isUseDefaultDict() == false);
    }

}
