package com.bolema.phonelive.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class LiveRecordBean implements Parcelable {
    private int uid;
    private String showid;
    private int islive;
    private String starttime;
    private String endtime;
    private String nums;
    private String title;
    private String datetime;
    private String video_url;
    private String id;
    /**
     * address :
     * city : 天津市
     * lat : 39.131676
     * length : 107
     * light : 2
     * lng : 117.131554
     * province : 天津市
     * status : 1
     * stream : 100489_1487054102848
     */

    private String address;
    private String city;
    private String lat;
    private String length;
    private String light;
    private String lng;
    private String province;
    private String status;
    private String stream;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public int getIslive() {
        return islive;
    }

    public void setIslive(int islive) {
        this.islive = islive;
    }

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeString(this.showid);
        dest.writeInt(this.islive);
        dest.writeString(this.starttime);
        dest.writeString(this.endtime);
        dest.writeString(this.nums);
        dest.writeString(this.title);
        dest.writeString(this.datetime);
        dest.writeString(this.video_url);
        dest.writeString(this.id);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.lat);
        dest.writeString(this.length);
        dest.writeString(this.light);
        dest.writeString(this.lng);
        dest.writeString(this.province);
        dest.writeString(this.status);
        dest.writeString(this.stream);
    }

    public LiveRecordBean() {
    }

    protected LiveRecordBean(Parcel in) {
        this.uid = in.readInt();
        this.showid = in.readString();
        this.islive = in.readInt();
        this.starttime = in.readString();
        this.endtime = in.readString();
        this.nums = in.readString();
        this.title = in.readString();
        this.datetime = in.readString();
        this.video_url = in.readString();
        this.id = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.lat = in.readString();
        this.length = in.readString();
        this.light = in.readString();
        this.lng = in.readString();
        this.province = in.readString();
        this.status = in.readString();
        this.stream = in.readString();
    }

    public static final Creator<LiveRecordBean> CREATOR = new Creator<LiveRecordBean>() {
        @Override
        public LiveRecordBean createFromParcel(Parcel source) {
            return new LiveRecordBean(source);
        }

        @Override
        public LiveRecordBean[] newArray(int size) {
            return new LiveRecordBean[size];
        }
    };
}
