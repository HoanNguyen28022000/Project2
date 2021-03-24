package Model;

public class Message {
    private User user;
    private String timeSent;
    private String message;

    public Message(User user, String timeSent, String message) {
        this.user= user;
        this.timeSent=timeSent;
        this.message=message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
