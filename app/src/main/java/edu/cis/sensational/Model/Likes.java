package edu.cis.sensational.Model;

/**
 * Created by User on 8/21/2017.
 */

public class Likes {

    private String user_id;

    public Likes(String user_id) {
        this.user_id = user_id;
    }

    public Likes() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Like{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
