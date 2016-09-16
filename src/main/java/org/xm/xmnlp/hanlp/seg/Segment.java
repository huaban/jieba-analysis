package org.xm.xmnlp.hanlp.seg;

import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.other.CharType;
import org.xm.xmnlp.hanlp.seg.NShort.AtomNode;
import sun.security.provider.certpath.Vertex;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xuming
 */
public abstract class Segment {
    protected Config config;
    public Segment(){
        config = new Config();
    }

    protected static List<AtomNode> quickAtomSegment(char[] charArray, int start, int end){
        List<AtomNode> atomNodeList = new LinkedList<>();
        int offsetAtom= start;
        int preType = CharType.get(charArray[offsetAtom]);
        int curType;
        while(++offsetAtom<end){
            curType = CharType.get(charArray[offsetAtom]);
            if(curType!=preType){
                if(charArray[offsetAtom] == '.' && preType==CharType.CT_NUM){
                    while (++offsetAtom<end){
                        curType = CharType.get(charArray[offsetAtom]);
                        if(curType !=CharType.CT_NUM)break;
                    }
                }
                atomNodeList.add(new AtomNode(new String(charArray ,start,offsetAtom-start),preType));
                start = offsetAtom;
            }
            preType = curType;
        }
        if(offsetAtom == end){
            atomNodeList.add(new AtomNode(new String(charArray,start,offsetAtom - start),preType));
        }
        return atomNodeList;
    }

    protected static List<Vertex> combineByCustomDictionary(List<Vertex> vertexList){
        Vertex[] wordNet = new vertex[vertexList.size()];
        vertexList.toArray(wordNet);
        DoubleArrayTrie<CoreDictionary.Attribute> dat = CustomDictionary.dat;
        for(int i = 0;i<wordNet.length;++i){
            int state = 1;
            state = dat.transition(wordNet[i].realWord,state);
            if(state>0){
                int start = i;
                int to = i+1;
                int end = to;
                CoreDictionary.Attribute value = dat.output(state);
                for(;to<wordNet.length;++to){
                    state = dat.transition(wordNet[to].realWord,state);
                    if(state <0)break;
                    CoreDictionary.Attribute output = dat.output(state);
                    if(output!=null){
                        value = output;
                        end = to+1;
                    }
                }
                if(value !=null){
                    StringBuilder sbTerm = new StringBuilder();
                    for(int j = start;i<end;++j){
                        sbTerm.append(wordNet[j]);
                        wordNet[j] = null;
                    }
                    wordNet[i] = new Vertex(sbTerm.toString(),value);
                    i = end-1;
                }
            }
        }
        if(CustomDictionay.trie !=null){
            for(int i= 0;i<wordNet.length;++i){
                if(wordNet[i] == null)continue;
                BaseNode<CoreDictionary.Attribute> state = CustomDictionary.trie.transition(wordNet[i].realWord.toCharArray(),0);
                if(state !=null){
                    int start = i;
                    int to = i+1;
                    int end = to;
                    CoreDictionary.Attribute value = state.getValue();
                    for(;to<wordNet.length;++to){
                        if(wordNet[to] == null)continue;
                        state = state.transition(wordNet[to].realWord.toCharArray(),0);
                        if(state ==null)break;
                        if(state.getValue()!=null){
                            value = state.getValue();
                            end = to +1;
                        }
                    }
                    if(value !=null){
                        StringBuilder sbTerm = new StringBuilder();
                        for(int j = start;i<end;++j){
                            if(wordNet[j] == null)continue;
                            sbTerm.append(wordNet[j]);
                            wordNet[j] = null;
                        }
                        wordNet[i] = new Vertex(sbTerm.toString(),value);
                        i = end-1;
                    }
                }
            }
        }
        vertexList.clear();
        for(Vertex vertex:wordNet){
            if(vertex!=null)vertexList.add(vertex);
        }
        return vertexList;
    }


}

