package com.nimah.khiem.musicbox;

/**
 * Created by Khiem on 8/14/2016.
 */
public class UserProfile {
    private String fb_id;
    private String fb_name;
    private String fb_email;
    private String fb_birthday;

    public UserProfile() {
    }

    public UserProfile(String fb_id, String fb_name, String fb_email, String fb_birthday) {
        this.fb_id = fb_id;
        this.fb_name = fb_name;
        this.fb_email = fb_email;
        this.fb_birthday = fb_birthday;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getFb_name() {
        return fb_name;
    }

    public void setFb_name(String fb_name) {
        this.fb_name = fb_name;
    }

    public String getFb_email() {
        return fb_email;
    }

    public void setFb_email(String fb_email) {
        this.fb_email = fb_email;
    }

    public String getFb_birthday() {
        return fb_birthday;
    }

    public void setFb_birthday(String fb_birthday) {
        this.fb_birthday = fb_birthday;
    }
}
