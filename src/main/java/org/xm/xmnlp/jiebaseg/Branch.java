package org.xm.xmnlp.jiebaseg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mingzai on 2016/9/10.
 */
public class Branch implements Comparable<Branch> {
    private static final Map<Character, Character> charMap = new HashMap<>(16, 0.95f);
    private static final int LIMIT = 3;
    private Map<Character, Branch> childrenMap;
    private Branch[] childrenArray;
    private Character nodeChar;
    private int storeSize = 0;
    private int nodeState = 0;
    private static final int MATCH = 1;

    Branch(Character nodeChar) {
        if (nodeChar == null) {
            throw new IllegalArgumentException("err");
        }
        this.nodeChar = nodeChar;
    }

    Character getNodeChar() {
        return this.nodeChar;
    }

    public boolean hasNextNode() {
        return this.storeSize > 0;
    }

    public Match match(char[] charArray) {
        return match(charArray, 0, charArray.length, null);
    }

    public Match match(char[] charArray, int begin, int length) {
        return match(charArray, begin, length, null);
    }

    private Match match(char[] charArray, int begin, int length, Match searchMatch) {
        if (searchMatch == null) {
            searchMatch = new Match();
            searchMatch.setBegin(begin);
        } else {
            searchMatch.setUnmatch();
        }
        searchMatch.setEnd(begin);
        Character keyChar = new Character(charArray[begin]);
        Branch branch = null;

        Branch[] branchArray = childrenArray;
        Map<Character, Branch> branchMap = childrenMap;

        //1 search the branch which equal keyChar
        if (branchArray != null) {
            Branch keySegment = new Branch(keyChar);
            int position = Arrays.binarySearch(branchArray, 0, storeSize, keySegment);
            if (position >= 0) {
                branch = branchArray[position];
            }
        } else if (branchMap != null) {
            branch = branchMap.get(keyChar);
        }

        //2 found branch and check state
        if (branch != null) {
            if (length > 1) {
                return branch.match(charArray, begin + 1, length - 1, searchMatch);
            } else if (length == 1) {
                if (branch.nodeState == MATCH) {
                    searchMatch.setMatch();
                }
                if (branch.hasNextNode()) {
                    searchMatch.setPrefix();
                    searchMatch.setBranch(branch);
                }
                return searchMatch;
            }
        }
        //3 not found and set Unmatch
        return searchMatch;
    }

    public void fillBranch(char[] charArray) {
        fillBranch(charArray, 0, charArray.length, 1);
    }

    public void dropBranch(char[] charArray) {
        fillBranch(charArray, 0, charArray.length, 0);
    }

    private synchronized void fillBranch(char[] charArray, int begin, int length, int enabled) {
        Character beginChar = new Character(charArray[begin]);
        Character keyChar = charMap.get(beginChar);
        if (keyChar == null) {
            charMap.put(beginChar, beginChar);
            keyChar = beginChar;
        }

        Branch branch = SearchBranch(keyChar, enabled);
        if (branch != null) {
            // 处理keyChar对应的
            if (length > 1) {
                // 词元还没有完全加入词典树
                branch.fillBranch(charArray, begin + 1, length - 1, enabled);
            } else if (length == 1) {
                // 已经是词元的最后一个char,设置当前节点状态为enabled，
                // enabled=1表明一个完整的词，enabled=0表示从词典中屏蔽当前词
                branch.nodeState = enabled;
            }
        }
    }

    private Branch SearchBranch(Character keyChar, int enabled) {
        Branch branch = null;
        if (storeSize <= LIMIT) {
            Branch[] branchArray = getChildrenArray();
            Branch keyBranch = new Branch(keyChar);
            int position = Arrays.binarySearch(branchArray, 0, storeSize, keyBranch);
            if (position >= 0) {
                branch = branchArray[position];
            }
            if (branch == null && enabled == MATCH) {
                branch = keyBranch;
                if (storeSize < LIMIT) {
                    branchArray[storeSize] = branch;
                    storeSize++;
                    Arrays.sort(branchArray, 0, storeSize);
                } else {
                    Map<Character, Branch> characterBranchMap = getChildrenMap();
                    migrate(branchArray, characterBranchMap);
                    characterBranchMap.put(keyChar, branch);
                    storeSize++;
                    childrenArray = null;
                }
            }
        } else {
            Map<Character, Branch> characterBranchMap = getChildrenMap();
            branch = characterBranchMap.get(keyChar);
            if (branch == null && enabled == MATCH) {
                branch = new Branch(keyChar);
                characterBranchMap.put(keyChar, branch);
                storeSize++;
            }
        }

        return branch;
    }

    public int compareTo(Branch o) {
        // 对当前节点存储的char进行比较
        return this.nodeChar.compareTo(o.nodeChar);
    }

    private void migrate(Branch[] branchArray, Map<Character, Branch> characterBranchMap) {
        for (Branch branch : branchArray) {
            if (branch != null) {
                characterBranchMap.put(branch.nodeChar, branch);
            }
        }
    }

    private Map<Character, Branch> getChildrenMap() {
        if (childrenMap == null) {
            synchronized (this) {
                if (childrenMap == null) {
                    childrenMap = new HashMap<>(LIMIT * 2, 0.8f);
                }
            }
        }
        return childrenMap;
    }

    private Branch[] getChildrenArray() {
        if (childrenArray == null) {
            synchronized (this) {
                if (childrenArray == null) {
                    childrenArray = new Branch[LIMIT];
                }
            }
        }
        return childrenArray;
    }


}
