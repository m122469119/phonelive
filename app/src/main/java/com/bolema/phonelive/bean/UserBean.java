package com.bolema.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class UserBean implements Parcelable {
    private int id = 0;
    private String user_login;
    private String user_pass;
    private String user_nicename;
    private String user_email;
    private String user_url;
    private String avatar;
    private int sex;
    private String birthday;
    private String signature;
    private String last_login_ip;
    private String last_login_time;
    private String create_time;
    private String user_activation_key;
    private String user_status;
    private String score;
    private int user_type;
    private String coin;
    private String mobile;
    private String token;
    private String expiretime;
    private String votes;
    private String province;
    private String city;
    private String consumption;
    private int level;
    private int isattention;
    private int attentionnum;
    private String fansnum;
    private int liverecordnum;
    private String title;
    private String nums;
    private List<OrderBean> coinrecord3;
    private int uType;
    private String stream;
    private String avatar_thumb;
    private String distance;
    private int uid;
    private String viplevel; //HHH 2016-09-13
    private String vip_thumb;
    private String vip_type;

    public int getUid() {
        return uid;
    }

    public void setUid() {
        this.uid = uid;
    }

    public String getViplevel() {
        return viplevel;
    }

    public void setViplevel(String viplevel) {
        this.viplevel = viplevel;
    }

    public String getVipthumb() {
        return vip_thumb;
    }

    public void setVipthumb(String vipthumb) {
        this.vip_thumb = vipthumb;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public int getuType() {
        return uType;
    }

    public void setuType(int uType) {
        this.uType = uType;
    }

    public List<OrderBean> getCoinrecord3() {
        return coinrecord3;
    }

    public void setCoinrecord3(List<OrderBean> coinrecord3) {
        this.coinrecord3 = coinrecord3;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public int getAttentionnum() {
        return attentionnum;
    }

    public void setAttentionnum(int attentionnum) {
        this.attentionnum = attentionnum;
    }

    public int getLiverecordnum() {
        return liverecordnum;
    }

    public void setLiverecordnum(int liverecordnum) {
        this.liverecordnum = liverecordnum;
    }

    public String getFansnum() {
        return fansnum;
    }

    public void setFansnum(String fansnum) {
        this.fansnum = fansnum;
    }

    public int getIsattention() {
        return isattention;
    }

    public void setIsattention(int isattention) {
        this.isattention = isattention;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public void setUser_pass(String user_pass) {
        this.user_pass = user_pass;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLast_login_ip() {
        return last_login_ip;
    }

    public void setLast_login_ip(String last_login_ip) {
        this.last_login_ip = last_login_ip;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUser_activation_key() {
        return user_activation_key;
    }

    public void setUser_activation_key(String user_activation_key) {
        this.user_activation_key = user_activation_key;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(String expiretime) {
        this.expiretime = expiretime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getVip_type() {
        return vip_type;
    }

    public void setVip_type(String vip_type) {
        this.vip_type = vip_type;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", user_login='" + user_login + '\'' +
                ", user_pass='" + user_pass + '\'' +
                ", user_nicename='" + user_nicename + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_url='" + user_url + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", signature='" + signature + '\'' +
                ", last_login_ip='" + last_login_ip + '\'' +
                ", last_login_time='" + last_login_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", user_activation_key='" + user_activation_key + '\'' +
                ", user_status='" + user_status + '\'' +
                ", score='" + score + '\'' +
                ", user_type=" + user_type +
                ", coin='" + coin + '\'' +
                ", mobile='" + mobile + '\'' +
                ", token='" + token + '\'' +
                ", expiretime='" + expiretime + '\'' +
                ", votes='" + votes + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", consumption='" + consumption + '\'' +
                ", level=" + level +
                ", isattention=" + isattention +
                ", attentionnum=" + attentionnum +
                ", fansnum='" + fansnum + '\'' +
                ", liverecordnum=" + liverecordnum +
                ", title='" + title + '\'' +
                ", nums='" + nums + '\'' +
                ", coinrecord3=" + coinrecord3 +
                ", uType=" + uType +
                ", stream='" + stream + '\'' +
                ", avatar_thumb='" + avatar_thumb + '\'' +
                ", distance='" + distance + '\'' +
                ", viplevel='" + viplevel + '\'' +
                ", vip_thumb='" + vip_thumb + '\'' +
                ", vip_type='" + vip_type + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.user_login);
        dest.writeString(this.user_pass);
        dest.writeString(this.user_nicename);
        dest.writeString(this.user_email);
        dest.writeString(this.user_url);
        dest.writeString(this.avatar);
        dest.writeInt(this.sex);
        dest.writeString(this.birthday);
        dest.writeString(this.signature);
        dest.writeString(this.last_login_ip);
        dest.writeString(this.last_login_time);
        dest.writeString(this.create_time);
        dest.writeString(this.user_activation_key);
        dest.writeString(this.user_status);
        dest.writeString(this.score);
        dest.writeInt(this.user_type);
        dest.writeString(this.coin);
        dest.writeString(this.mobile);
        dest.writeString(this.token);
        dest.writeString(this.expiretime);
        dest.writeString(this.votes);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.consumption);
        dest.writeInt(this.level);
        dest.writeInt(this.isattention);
        dest.writeInt(this.attentionnum);
        dest.writeString(this.fansnum);
        dest.writeInt(this.liverecordnum);
        dest.writeString(this.title);
        dest.writeString(this.nums);
        dest.writeList(this.coinrecord3);
        dest.writeInt(this.uType);
        dest.writeString(this.stream);
        dest.writeString(this.avatar_thumb);
        dest.writeString(this.distance);
        dest.writeInt(this.uid);
        dest.writeString(this.viplevel);
        dest.writeString(this.vip_thumb);
        dest.writeString(this.vip_type);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.id = in.readInt();
        this.user_login = in.readString();
        this.user_pass = in.readString();
        this.user_nicename = in.readString();
        this.user_email = in.readString();
        this.user_url = in.readString();
        this.avatar = in.readString();
        this.sex = in.readInt();
        this.birthday = in.readString();
        this.signature = in.readString();
        this.last_login_ip = in.readString();
        this.last_login_time = in.readString();
        this.create_time = in.readString();
        this.user_activation_key = in.readString();
        this.user_status = in.readString();
        this.score = in.readString();
        this.user_type = in.readInt();
        this.coin = in.readString();
        this.mobile = in.readString();
        this.token = in.readString();
        this.expiretime = in.readString();
        this.votes = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.consumption = in.readString();
        this.level = in.readInt();
        this.isattention = in.readInt();
        this.attentionnum = in.readInt();
        this.fansnum = in.readString();
        this.liverecordnum = in.readInt();
        this.title = in.readString();
        this.nums = in.readString();
        this.coinrecord3 = new ArrayList<OrderBean>();
        in.readList(this.coinrecord3, OrderBean.class.getClassLoader());
        this.uType = in.readInt();
        this.stream = in.readString();
        this.avatar_thumb = in.readString();
        this.distance = in.readString();
        this.uid = in.readInt();
        this.viplevel = in.readString();
        this.vip_thumb = in.readString();
        this.vip_type = in.readString();
    }

    public static final Parcelable.Creator<UserBean> CREATOR = new Parcelable.Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
