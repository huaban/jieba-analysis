package org.xm.xmnlp.hanlp.corpus.io;

import java.io.DataOutputStream;

/**
 * @author xuming
 */
public interface ICacheAble {
    void save(DataOutputStream out)throws Exception;
    boolean load(ByteArray byteArray);
}
