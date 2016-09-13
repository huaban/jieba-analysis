package org.xm.xmnlp.word.segmentation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by mingzai on 2016/9/11.
 */
public class Word implements Comparable {
    private String text;
    private String acronymPinYin;
    private String fullPinYin;
    private PartOfSpeech partOfSpeech = null;
    private int frequency;
    private List<Word> synonym = null;
    private List<Word> antonym = null;
    private Float weight;

    public Word(String text) {
        this.text = text;
    }

    public Word(String text, PartOfSpeech partOfSpeech, int frequency) {
        this.text = text;
        this.partOfSpeech = partOfSpeech;
        this.frequency = frequency;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAcronymPinYin() {
        if (acronymPinYin == null) {
            return "";
        }
        return acronymPinYin;
    }

    public void setAcronymPinYin(String acronymPinYin) {
        this.acronymPinYin = acronymPinYin;
    }

    public String getFullPinYin() {
        if (fullPinYin == null) {
            return "";
        }
        return fullPinYin;
    }

    public void setFullPinYin(String fullPinYin) {
        this.fullPinYin = fullPinYin;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public List<Word> getSynonym() {
        if (synonym == null) {
            return Collections.emptyList();
        }
        return synonym;
    }

    public void setSynonym(List<Word> synonym) {
        if (synonym != null) {
            Collections.sort(synonym);
            this.synonym = synonym;
        }
    }

    public List<Word> getAntonym() {
        if (antonym == null) {
            return Collections.emptyList();
        }
        return antonym;
    }

    public void setAntonym(List<Word> antonym) {
        if (antonym != null) {
            Collections.sort(antonym);
            this.antonym = antonym;
        }
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.text);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        final Word other = (Word) o;
        return Objects.equals(this.text, other.text);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (text != null) str.append(text);
        if (acronymPinYin != null) {
            str.append(" ").append(acronymPinYin);
        }
        if (fullPinYin != null) {
            str.append(" ").append(fullPinYin);
        }
        if (frequency > 0) {
            str.append("/").append(frequency);
        }
        if (partOfSpeech != null) {
            str.append("/").append(partOfSpeech.getPos());
        }
        if (synonym != null) {
            str.append(synonym.toString());
        }
        if (antonym != null) {
            str.append(antonym.toString());
        }
        return str.toString();
    }


    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (this.text == null) return -1;
        if (o == null) return 1;
        if (!(o instanceof Word)) return 1;
        String t = ((Word) o).getText();
        if (t == null) return 1;
        return this.text.compareTo(t);

    }
}
