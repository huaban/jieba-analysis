package org.xm.xmnlp.hanlp.corpus.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author xuming
 */
public interface IIOAdapter
{
    InputStream open(String path)throws IOException;
    OutputStream create(String path) throws IOException;
}
