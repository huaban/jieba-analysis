package org.xm.xmnlp.jiebaseg;


/**
 * Created by mingzai on 2016/9/10.
 */
public class Match {
    private static final int UNMATCH = 0x00000000;
    //完全匹配
    private static final int MATCH = 0x00000001;
    //前缀匹配
    private static final int PREFIX = 0x00000010;


    //该HIT当前状态，默认未匹配
    private int state = UNMATCH;

    //记录词典匹配过程中，当前匹配到的词典分支节点
    private Branch branch;
    /*
     * 词段开始位置
     */
    private int begin;
    /*
     * 词段的结束位置
     */
    private int end;


    /**
     * 判断是否完全匹配
     */
    public boolean isMatch() {
        return (this.state & MATCH) > 0;
    }
    /**
     *
     */
    public void setMatch() {
        this.state = this.state | MATCH;
    }

    /**
     * 判断是否是词的前缀
     */
    public boolean isPrefix() {
        return (this.state & PREFIX) > 0;
    }
    /**
     *
     */
    public void setPrefix() {
        this.state = this.state | PREFIX;
    }
    /**
     * 判断是否是不匹配
     */
    public boolean isUnmatch() {
        return this.state == UNMATCH ;
    }
    /**
     *
     */
    public void setUnmatch() {
        this.state = UNMATCH;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}

