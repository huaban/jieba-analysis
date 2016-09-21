package org.xm.xmnlp.hanlp.dictionary.stopword;

import org.xm.xmnlp.hanlp.collection.MDAG.MDAGSet;
import org.xm.xmnlp.hanlp.seg.common.Term;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


/**
 * @author xuming
 */
public class StopWordDictionary extends MDAGSet implements Filter {
    @Override
    public boolean shouldInclude(Term term) {
        return contains(term.word);
    }
    public StopWordDictionary(){
    }
    public StopWordDictionary(Collection<String> stringCollection){
        super(stringCollection);
    }
    public StopWordDictionary(File file )throws IOException{
        super(file);
    }
}
