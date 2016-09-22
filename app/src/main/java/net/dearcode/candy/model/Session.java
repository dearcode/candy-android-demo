package net.dearcode.candy.model;

/**
 *  * Created by c-wind on 2016/9/21 18:39
 *  * mail：root@codecn.org
 *  
 */
public class Session {
    private String avatar;
    private String title;
    private String date;
    private String msg;

    public Session(String avatar, String title, String date, String msg) {
        this.avatar  = avatar;
        this.title = title;
        this.date = date;
        this.msg = msg;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }
}
