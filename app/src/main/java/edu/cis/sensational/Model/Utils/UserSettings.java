package edu.cis.sensational.Model.Utils;

/**
 * Created by User on 6/30/2017.
 */

public class UserSettings {

    private User user;

    private UserAccountSettings settings;

    public UserSettings()
    {
        user = null;
        settings = null;
    }

    public UserSettings(User user, UserAccountSettings settings)
    {
        this.user = user;
        this.settings = settings;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getSettings() {
        return settings;
    }

    public void setSettings(UserAccountSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + user +
                ", settings=" + settings +
                '}';
    }
}
