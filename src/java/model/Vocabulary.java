package model;

public class Vocabulary {
    private int vocabID;
    private String word;
    private String meaning;
    private String reading;
    private String example;

    public Vocabulary() {}

    public Vocabulary(int vocabID, String word, String meaning, String reading, String example) {
        this.vocabID = vocabID;
        this.word = word;
        this.meaning = meaning;
        this.reading = reading;
        this.example = example;
    }

    public int getVocabID() {
        return vocabID;
    }

    public void setVocabID(int vocabID) {
        this.vocabID = vocabID;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public String toString() {
        return "Vocabulary{" + "vocabID=" + vocabID + ", word=" + word + ", meaning=" + meaning + ", reading=" + reading + ", example=" + example + '}';
    }

}