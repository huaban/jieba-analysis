package org.xm.xmnlp.hanlp.utility;

import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.corpus.util.CustomNatureUtility;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;

import java.util.LinkedHashSet;

/**
 * @author xuming
 */
public class LexiconUtility {
    public static CoreDictionary.Attribute getAttribute(String word){
        CoreDictionary.Attribute attribute = CoreDictionary.get(word);
        if(attribute != null)return attribute;
        return CustomDictionary.get(word);
    }

    public static Nature convertStringToNature(String name, LinkedHashSet<Nature> customNaureCollector){
        try{
            return Nature.valueOf(name);
        }catch (Exception e){
            Nature nature = CustomNatureUtility.addNature(name);
            if(customNaureCollector !=null)customNaureCollector.add(nature);
            return nature;
        }
    }
}
