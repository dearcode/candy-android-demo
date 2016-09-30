package net.dearcode.candy.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *  * Created by c-wind on 2016/9/30 10:59
 *  * mail：root@codecn.org
 *  
 */
public class Message implements Parcelable {
    private long id;
    private long method;
    private long group;
    private long from;
    private long to;
    private String msg;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMethod() {
        return method;
    }

    public void setMethod(long method) {
        this.method = method;
    }

    public long getGroup() {
        return group;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //long id, long method, long group, long from, long to, String msg)
        dest.writeLong(id);
        dest.writeLong(method);
        dest.writeLong(group);
        dest.writeLong(from);
        dest.writeLong(to);
        dest.writeString(msg);
    }

    public Message(long id, long method, long group, long from, long to, String msg) {
        this.id = id;
        this.method = method;
        this.group = group;
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    public Message() {
    }

    protected Message(Parcel in) {
        this.id = in.readLong();
        this.method = in.readLong();
        this.group = in.readLong();
        this.from = in.readLong();
        this.to = in.readLong();
        this.msg = in.readString();
    }

    public boolean isGroupMessage() {
        return group != 0;
    }

    public void setBundle(Bundle b) {
        b.putLong("id", id);
        b.putLong("method", method);
        b.putLong("group", group);
        b.putLong("from", from);
        b.putLong("to", to);
        b.putString("msg", msg);
    }

    public String getDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date((id >> 32) * 1000));
    }

    public static final Parcelable.Creator<Message> CREATOR = new
            Parcelable.Creator<Message>() {
                public Message createFromParcel(Parcel in) {
                    return new Message(in);
                }

                public Message[] newArray(int size) {
                    return new Message[size];
                }
            };
}
