package com.bolema.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/13.
 */
public class PrivateChatUserBean extends UserBean implements Parcelable{
    private String lastMessage;
    private boolean unreadMessage;
    private int isattention2;

    public int getIsattention2() {
        return isattention2;
    }

    public void setIsattention2(int isattention2) {
        this.isattention2 = isattention2;
    }

    public boolean isUnreadMessage() {
        return unreadMessage;
    }

    public void setUnreadMessage(boolean unreadMessage) {
        this.unreadMessage = unreadMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return "PrivateChatUserBean{" +
                "lastMessage='" + lastMessage + '\'' +
                ", unreadMessage=" + unreadMessage +
                ", isattention2=" + isattention2 +
                "} " + super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.lastMessage);
        dest.writeByte(this.unreadMessage ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isattention2);
    }

    public PrivateChatUserBean() {
    }

    protected PrivateChatUserBean(Parcel in) {
        super(in);
        this.lastMessage = in.readString();
        this.unreadMessage = in.readByte() != 0;
        this.isattention2 = in.readInt();
    }

    public static final Creator<PrivateChatUserBean> CREATOR = new Creator<PrivateChatUserBean>() {
        @Override
        public PrivateChatUserBean createFromParcel(Parcel source) {
            return new PrivateChatUserBean(source);
        }

        @Override
        public PrivateChatUserBean[] newArray(int size) {
            return new PrivateChatUserBean[size];
        }
    };
}
