package model;

public class Kanji {
    private int kanjiID;
    private String character;
    private String onyomi;
    private String kunyomi;
    private String meaning;
    private int strokeCount;
    private int lessonID;

    public Kanji() {}

    public int getKanjiID() {
        return kanjiID;
    }

    public void setKanjiID(int kanjiID) {
        this.kanjiID = kanjiID;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getOnyomi() {
        return onyomi;
    }

    public void setOnyomi(String onyomi) {
        this.onyomi = onyomi;
    }

    public String getKunyomi() {
        return kunyomi;
    }

    public void setKunyomi(String kunyomi) {
        this.kunyomi = kunyomi;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public int getStrokeCount() {
        return strokeCount;
    }

    public void setStrokeCount(int strokeCount) {
        this.strokeCount = strokeCount;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    @Override
    public String toString() {
        return "Kanji{" + "kanjiID=" + kanjiID + ", character=" + character + ", onyomi=" + onyomi + ", kunyomi=" + kunyomi + ", meaning=" + meaning + ", strokeCount=" + strokeCount + ", lessonID=" + lessonID + '}';
    }

}
