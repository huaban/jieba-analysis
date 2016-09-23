package org.xm.xmnlp.hanlp.corpus.synonym;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuming
 */
public class Synonym implements ISynonym {
    public String realWord;
    public long id ;
    public Type type;
    public Synonym(String realWord,long id,Type type){
        this.realWord = realWord;
        this.id = id;
        this.type = type;
    }
    public enum Type{
        EQUAL,
        LIKE,
        SINGLE,
        UNDEFINED,
    }
    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getIdString() {
        return realWord;
    }
    @Override
    public String getRealWord(){
        return SynonymHelper.convertId2StringWithIndex(id);
    }
    public static List<Synonym> create(String param){
        if(param == null)return null;
        String[] strArray = param.split(" ");
        return create(strArray);
    }
    public static ArrayList<Synonym> create(String[] strArray){
        ArrayList<Synonym> synonymList = new ArrayList<>(strArray.length -1);
        String idString = strArray[0];
        Type  type;
        switch (idString.charAt(idString.length() -1)){
            case '=':
                type = Type.EQUAL;
                break;
            case '#':
                type = Type.LIKE;
                break;
            default:
                type = Type.SINGLE;
                break;
        }
        long startId = SynonymHelper.convertString2IdWithIndex(idString,0);
        for(int i =1;i<strArray.length;++i){
            if(type == Type.LIKE){
                synonymList.add(new Synonym(strArray[i],startId +i,type));
            }else{
                synonymList.add(new Synonym(strArray[i],startId,type));
            }
        }
        return synonymList;
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append(realWord);
        switch (type){
            case EQUAL:
                sb.append('=');
                break;
            case LIKE:
                sb.append('#');
                break;
            case SINGLE:
                sb.append('@');
                break;
            case UNDEFINED:
                sb.append('?');
                break;
        }
        sb.append(getIdString());
        return sb.toString();
    }
    public long distance(Synonym other){
        return Math.abs(id - other.id);
    }
}
