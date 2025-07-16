package model;

public class FeedbackVote {
    private int voteID;
    private int feedbackID;
    private int userID;
    private int voteType; // 1: like, -1: dislike

    public FeedbackVote() {}

    public FeedbackVote(int voteID, int feedbackID, int userID, int voteType) {
        this.voteID = voteID;
        this.feedbackID = feedbackID;
        this.userID = userID;
        this.voteType = voteType;
    }

    // Getter/setter
    public int getVoteID() { return voteID; }
    public void setVoteID(int voteID) { this.voteID = voteID; }
    public int getFeedbackID() { return feedbackID; }
    public void setFeedbackID(int feedbackID) { this.feedbackID = feedbackID; }
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    public int getVoteType() { return voteType; }
    public void setVoteType(int voteType) { this.voteType = voteType; }
} 