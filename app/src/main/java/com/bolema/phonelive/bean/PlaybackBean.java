package com.bolema.phonelive.bean;

import java.util.List;

/**
 * 首页关注直播回放
 * Created by yuanshuo on 2017/2/16.
 */

public class PlaybackBean {

    /**
     * back : [{"address":"","city":"天津市","duration":"39秒","endtime":"1487138723","id":"43282","islive":"0","lat":"39.131682","length":"39","light":"0","lng":"117.131568","nums":"61","province":"天津市","showid":"1487138684","starttime":"1487138684","status":"1","stream":"100489_1487138694856","times":"2017-02-15","title":"","uid":"100489","video_url":"http://wanjianin.oss-cn-hangzhou.aliyuncs.com/record/5showcam/100489_1487138694856.m3u8"},{"address":"","city":"天津市","duration":"1分47秒","endtime":"1487054200","id":"43165","islive":"0","lat":"39.131676","length":"107","light":"2","lng":"117.131554","nums":"60","province":"天津市","showid":"1487054093","starttime":"1487054093","status":"1","stream":"100489_1487054102848","times":"2017-02-14","title":"","uid":"100489","video_url":"http://wanjianin.oss-cn-hangzhou.aliyuncs.com/record/5showcam/100489_1487054102848.m3u8"},{"address":"","city":"天津市","duration":"4分15秒","endtime":"1487053538","id":"43160","islive":"0","lat":"39.131676","length":"255","light":"3","lng":"117.131554","nums":"64","province":"天津市","showid":"1487053283","starttime":"1487053283","status":"1","stream":"100489_1487053293101","times":"2017-02-14","title":"","uid":"100489","video_url":"http://wanjianin.oss-cn-hangzhou.aliyuncs.com/record/5showcam/100489_1487053293101.m3u8"},{"address":"","city":"天津市","duration":"24秒","endtime":"1487038201","id":"42742","islive":"0","lat":"39.131671","length":"24","light":"0","lng":"117.131528","nums":"60","province":"天津市","showid":"1487038177","starttime":"1487038177","status":"1","stream":"100489_1487038187164","times":"2017-02-14","title":"","uid":"100489","video_url":"http://wanjianin.oss-cn-hangzhou.aliyuncs.com/record/5showcam/100489_1487038187164.m3u8"}]
     * userinfo : {"avatar":"http://wx.qlogo.cn/mmopen/6zMSicUKBhM52ic32bA2gHVHNDHI37waD8WRUuVh89q5xZzB9P13WotVMQtiaaTk49GsJj7tS9nBUcgB1DzH4W5HA/0","avatar_thumb":"http://wx.qlogo.cn/mmopen/6zMSicUKBhM52ic32bA2gHVHNDHI37waD8WRUuVh89q5xZzB9P13WotVMQtiaaTk49GsJj7tS9nBUcgB1DzH4W5HA/132","city":"天津市","consumption":"880","experience":"8800","id":"100489","isrecommend":"0","level":"5","province":"","sex":"0","signature":"这家伙很懒，什么都没留下","user_nicename":"天涯","vip_buytime":"1487052885","vip_coin":"1000","vip_endtime":"1494381760","vip_id":"1","vip_name":"播粉俱乐部会员","vip_thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20160924/57e6154a1ce3b.png","vip_type":1,"votestotal":"180"}
     */

    private UserinfoBean userinfo;
    private List<BackBean> back;

    public UserinfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public List<BackBean> getBack() {
        return back;
    }

    public void setBack(List<BackBean> back) {
        this.back = back;
    }

    public static class UserinfoBean {
        /**
         * avatar : http://wx.qlogo.cn/mmopen/6zMSicUKBhM52ic32bA2gHVHNDHI37waD8WRUuVh89q5xZzB9P13WotVMQtiaaTk49GsJj7tS9nBUcgB1DzH4W5HA/0
         * avatar_thumb : http://wx.qlogo.cn/mmopen/6zMSicUKBhM52ic32bA2gHVHNDHI37waD8WRUuVh89q5xZzB9P13WotVMQtiaaTk49GsJj7tS9nBUcgB1DzH4W5HA/132
         * city : 天津市
         * consumption : 880
         * experience : 8800
         * id : 100489
         * isrecommend : 0
         * level : 5
         * province :
         * sex : 0
         * signature : 这家伙很懒，什么都没留下
         * user_nicename : 天涯
         * vip_buytime : 1487052885
         * vip_coin : 1000
         * vip_endtime : 1494381760
         * vip_id : 1
         * vip_name : 播粉俱乐部会员
         * vip_thumb : http://bolema.wanchuangzhongchou.com/data/upload/20160924/57e6154a1ce3b.png
         * vip_type : 1
         * votestotal : 180
         */

        private String avatar;
        private String avatar_thumb;
        private String city;
        private String consumption;
        private String experience;
        private String id;
        private String isrecommend;
        private String level;
        private String province;
        private String sex;
        private String signature;
        private String user_nicename;
        private String vip_buytime;
        private String vip_coin;
        private String vip_endtime;
        private String vip_id;
        private String vip_name;
        private String vip_thumb;
        private int vip_type;
        private String votestotal;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatar_thumb() {
            return avatar_thumb;
        }

        public void setAvatar_thumb(String avatar_thumb) {
            this.avatar_thumb = avatar_thumb;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getConsumption() {
            return consumption;
        }

        public void setConsumption(String consumption) {
            this.consumption = consumption;
        }

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIsrecommend() {
            return isrecommend;
        }

        public void setIsrecommend(String isrecommend) {
            this.isrecommend = isrecommend;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getVip_buytime() {
            return vip_buytime;
        }

        public void setVip_buytime(String vip_buytime) {
            this.vip_buytime = vip_buytime;
        }

        public String getVip_coin() {
            return vip_coin;
        }

        public void setVip_coin(String vip_coin) {
            this.vip_coin = vip_coin;
        }

        public String getVip_endtime() {
            return vip_endtime;
        }

        public void setVip_endtime(String vip_endtime) {
            this.vip_endtime = vip_endtime;
        }

        public String getVip_id() {
            return vip_id;
        }

        public void setVip_id(String vip_id) {
            this.vip_id = vip_id;
        }

        public String getVip_name() {
            return vip_name;
        }

        public void setVip_name(String vip_name) {
            this.vip_name = vip_name;
        }

        public String getVip_thumb() {
            return vip_thumb;
        }

        public void setVip_thumb(String vip_thumb) {
            this.vip_thumb = vip_thumb;
        }

        public int getVip_type() {
            return vip_type;
        }

        public void setVip_type(int vip_type) {
            this.vip_type = vip_type;
        }

        public String getVotestotal() {
            return votestotal;
        }

        public void setVotestotal(String votestotal) {
            this.votestotal = votestotal;
        }
    }

    public static class BackBean {
        /**
         * address :
         * city : 天津市
         * duration : 39秒
         * endtime : 1487138723
         * id : 43282
         * islive : 0
         * lat : 39.131682
         * length : 39
         * light : 0
         * lng : 117.131568
         * nums : 61
         * province : 天津市
         * showid : 1487138684
         * starttime : 1487138684
         * status : 1
         * stream : 100489_1487138694856
         * times : 2017-02-15
         * title :
         * uid : 100489
         * video_url : http://wanjianin.oss-cn-hangzhou.aliyuncs.com/record/5showcam/100489_1487138694856.m3u8
         */

        private String address;
        private String city;
        private String duration;
        private String endtime;
        private String id;
        private String islive;
        private String lat;
        private String length;
        private String light;
        private String lng;
        private String nums;
        private String province;
        private String showid;
        private String starttime;
        private String status;
        private String stream;
        private String datetime;
        private String title;
        private String uid;
        private String video_url;

        @Override
        public String toString() {
            return  "{" +
                    "\"address\":" + '\"'+address + '\"' +
                    ",\"city\":" + '\"'+city + '\"' +
                    ", \"duration\":" + '\"'+duration + '\"' +
                    ", \"endtime\":" + '\"'+endtime + '\"' +
                    ",\"id\":" + '\"'+id + '\"' +
                    ",\"islive\":" + '\"'+islive + '\"' +
                    ", \"lat\":" +'\"'+ lat + '\"' +
                    ",\"length\":" + '\"'+length + '\"' +
                    ",\"light\":" +'\"'+ light + '\"' +
                    ", \"lng\":" + '\"'+lng + '\"' +
                    ", \"nums\":" + '\"'+nums + '\"' +
                    ",\"province\":" + '\"'+province + '\"' +
                    ",\"showid\":" +'\"'+ showid + '\"' +
                    ", \"starttime\":" + '\"'+starttime + '\"' +
                    ", \"status\":" +'\"'+ status + '\"' +
                    ",\"stream\":" +'\"'+ stream + '\"' +
                    ",\"datetime\":" + '\"'+datetime + '\"' +
                    ",\"title\":" + '\"'+title + '\"' +
                    ",\"uid\":" + '\"'+uid + '\"' +
                    ",\"video_url\":" +'\"'+ video_url + '\"' +
                    '}';
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

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIslive() {
            return islive;
        }

        public void setIslive(String islive) {
            this.islive = islive;
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

        public String getNums() {
            return nums;
        }

        public void setNums(String nums) {
            this.nums = nums;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getShowid() {
            return showid;
        }

        public void setShowid(String showid) {
            this.showid = showid;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
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

        public String getTimes() {
            return datetime;
        }

        public void setTimes(String datetime) {
            this.datetime = datetime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }
    }
}
