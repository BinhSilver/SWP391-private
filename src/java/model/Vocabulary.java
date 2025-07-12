package model;

public class Vocabulary {

    private int vocabID;
    private int lessonID;
    private String word;
    private String meaning;
    private String reading;
    private String example;
    private String imagePath; // Thêm thuộc tính imagePath

    // Constructors
    public Vocabulary() {
    }

    public Vocabulary(int vocabID, int lessonID, String word, String meaning, String reading, String example, String imagePath) {
        this.vocabID = vocabID;
        this.lessonID = lessonID;
        this.word = word;
        this.meaning = meaning;
        this.reading = reading;
        this.example = example;
        this.imagePath = imagePath; // Thêm imagePath
    }

    // Getters and Setters
    public int getVocabID() {
        return vocabID;
    }

    public void setVocabID(int vocabID) {
        this.vocabID = vocabID;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
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

    public String getImagePath() {
        return imagePath;
    } // Thêm getter

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    } // Thêm setter
}
