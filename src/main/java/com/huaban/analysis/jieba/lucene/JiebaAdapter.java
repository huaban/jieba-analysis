package com.huaban.analysis.jieba.lucene;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

public class JiebaAdapter implements Iterator<SegToken> {

	private final JiebaSegmenter jiebaTagger;

	private final SegMode segMode;

	private Iterator<SegToken> tokens;

	private String raw = null;

	public JiebaAdapter(Reader input, String segModeName) {

		this.jiebaTagger = new JiebaSegmenter();

		if (null == segModeName) {
			segMode = SegMode.SEARCH;
		} else {
			segMode = SegMode.valueOf(segModeName);
		}
	}

	public synchronized void reset(Reader input) {
		try {
			StringBuilder bdr = new StringBuilder();
			char[] buf = new char[1024];
			int size = 0;
			while ((size = input.read(buf, 0, buf.length)) != -1) {
				String tempstr = new String(buf, 0, size);
				bdr.append(tempstr);
			}
			raw = bdr.toString().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<SegToken> list = jiebaTagger.process(raw, segMode);
		tokens = list.iterator();
	}

	@Override
	public boolean hasNext() {
		return tokens.hasNext();
	}

	@Override
	public SegToken next() {
		return tokens.next();
	}

	@Override
	public void remove() {
		tokens.remove();
	}
}
