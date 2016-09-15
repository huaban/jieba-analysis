package org.xm.xmnlp.hanlp.corpus.util;

import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionaryTransformMatrixDictionary;
import sun.security.provider.certpath.Vertex;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author xuming
 */
public class CustomNatureUtility {
    private static Map<String,Nature> extraValueMap = new TreeMap<>();
    private static EnumBuster<Nature> enumBuster = new EnumBuster<>(Nature.class,
            CustomDictionary.class,
            Vertex.class,
            PersonRecognition.class,
            OrganizationRecognition.class);
    public static Nature addNature(String name){
        Nature customNature = extraValueMap.get(name);
        if(customNature!=null)return customNature;
        customNature = enumBuster.make(name);
        enumBuster.addByValue(customNature);
        extraValueMap.put(name,customNature);
        CoreDictionaryTransformMatrixDictionary.transformMatrixDictionary.extendSize();
        return customNature;
    }
    public static void registerSwitchClass(Class... switchUsers){
        enumBuster.registerSwitchClass(switchUsers);
    }
    public static void restore(){
        enumBuster.restore();
        extraValueMap.clear();
    }
}
