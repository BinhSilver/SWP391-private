package model;

public class FlashcardItem {
    private int flashcardItemID;
    private int flashcardID;
    private Integer vocabID;
    private Integer userVocabID;
    private String note;
    private String frontContent;
    private String backContent;
    private String frontImage;
    private String backImage;
    private int orderIndex;

    public FlashcardItem() {
    }

    public FlashcardItem(int flashcardItemID, int flashcardID, Integer vocabID, Integer userVocabID, String note, String frontContent, String backContent, String frontImage, String backImage, int orderIndex) {
        this.flashcardItemID = flashcardItemID;
        this.flashcardID = flashcardID;
        this.vocabID = vocabID;
        this.userVocabID = userVocabID;
        this.note = note;
        this.frontContent = frontContent;
        this.backContent = backContent;
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.orderIndex = orderIndex;
    }

    public int getFlashcardItemID() {
        return flashcardItemID;
    }

    public void setFlashcardItemID(int flashcardItemID) {
        this.flashcardItemID = flashcardItemID;
    }

    public int getFlashcardID() {
        return flashcardID;
    }

    public void setFlashcardID(int flashcardID) {
        this.flashcardID = flashcardID;
    }

    public Integer getVocabID() {
        return vocabID;
    }

    public void setVocabID(Integer vocabID) {
        this.vocabID = vocabID;
    }

    public Integer getUserVocabID() {
        return userVocabID;
    }

    public void setUserVocabID(Integer userVocabID) {
        this.userVocabID = userVocabID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFrontContent() {
        return frontContent;
    }

    public void setFrontContent(String frontContent) {
        this.frontContent = frontContent;
    }

    public String getBackContent() {
        return backContent;
    }

    public void setBackContent(String backContent) {
        this.backContent = backContent;
    }

    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
} 