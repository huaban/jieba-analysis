package org.xm.xmnlp.hanlp.recognition;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.corpus.tag.NT;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.dictionary.nt.OrganizationDictionary;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;

import java.util.Iterator;
import java.util.List;

/**
 * @author xuming
 */
public class OrganizationRecognition {
    public static Boolean recognition(List<Vertex> pWordSegResult, WordNet wordNetOptimum, WordNet wordNetAll) {
        List<EnumItem<NT>> roleTagList = roleTag(pWordSegResult,wordNetAll);
        if(HanLP.Config.DEBUG){
            StringBuilder sbLog = new StringBuilder();
            Iterator<Vertex> iterator = pWordSegResult.iterator();
            for (EnumItem<NT> NTEnumItem : roleTagList)
            {
                sbLog.append('[');
                sbLog.append(iterator.next().realWord);
                sbLog.append(' ');
                sbLog.append(NTEnumItem);
                sbLog.append(']');
            }
            System.out.printf("机构名角色观察：%s\n", sbLog.toString());
        }
        List<NT> NTList = ViterbiExCompute(roleTagList);
        if(HanLP.Config.DEBUG){
            StringBuilder sbLog = new StringBuilder();
            Iterator<Vertex> iterator = pWordSegResult.iterator();
            sbLog.append('[');
            for(NT NT:NTList){
                sbLog.append(iterator.next().realWord);
                sbLog.append('/');
                sbLog.append(NT);
                sbLog.append(" ,");
            }
            if(sbLog.length()>1 )
                sbLog.delete(sbLog.length() -2,sbLog.length());
            sbLog.append(']');
            System.out.printf("机构名角色标注：%s\n", sbLog.toString());
        }
        OrganizationDictionary.parsePattern(NTList,pWordSegResult,wordNetOptimum,wordNetAll);
        return true;
    }

    private static List<EnumItem<NT>> roleTag(List<Vertex> pWordSegResult, WordNet wordNetAll) {
    }
}
