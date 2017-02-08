package com.bolema.phonelive.bean;

import java.util.List;

/**
 *
 * Created by yuanshuo on 2017/2/7.
 */

public class TopicListBean {

    /**
     * data : {"code":0,"info":[{"count":"2","id":"38","name":"#政策解读#","orderlist":"0","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f4e06ebd.png","type":"0"},{"count":"3","id":"15","name":"#红色旅游#","orderlist":"1","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f42d4c86.png","type":"0"},{"count":"0","id":"40","name":"#回家的路#","orderlist":"2","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f3712400.png","type":"0"},{"count":"10","id":"26","name":"#公益#","orderlist":"3","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f2c697d7.png","type":"0"},{"count":"17","id":"5","name":"#女神#","orderlist":"4","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f203c49f.png","type":"0"},{"id":"4","name":"#男神#","orderlist":"5","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f16eb076.png","type":"0"},{"count":"676","id":"3","name":"#好声音#","orderlist":"6","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f0de1155.png","type":"0"},{"count":"7","id":"7","name":"#脱口秀#","orderlist":"7","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f02ca87e.png","type":"0"},{"count":"3","id":"23","name":"#财经#","orderlist":"8","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734efa81b68.png","type":"0"},{"count":"3","id":"20","name":"#模仿达人#","orderlist":"9","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734ef022968.png","type":"0"},{"count":"2","id":"21","name":"#相声#","orderlist":"10","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c346f27c.png","type":"0"},{"count":"2","id":"17","name":"#记者在线#","orderlist":"11","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c2d538f0.png","type":"0"},{"count":"1","id":"22","name":"#健身#","orderlist":"12","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c2525db7.png","type":"0"},{"count":"657","id":"2","name":"#名师讲堂#","orderlist":"13","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c197dc43.png","type":"0"},{"count":"3","id":"19","name":"#儿童#","orderlist":"14","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c107240a.png","type":"0"},{"count":"1","id":"24","name":"#活动#","orderlist":"15","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c06f2281.png","type":"0"},{"count":"0","id":"16","name":"#美食#","orderlist":"16","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bfac9ad0.png","type":"0"},{"count":"1","id":"18","name":"#原创#","orderlist":"17","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bf05263e.png","type":"0"},{"count":"1","id":"36","name":"#校园#","orderlist":"18","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734be6df532.png","type":"0"},{"count":"2","id":"37","name":"#美妆#","orderlist":"19","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bda730ab.png","type":"0"},{"count":"0","id":"25","name":"#其他#","orderlist":"20","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bcabc1c7.png","type":"0"}],"msg":""}
     * msg :
     * ret : 200
     */

    private DataBean data;
    private String msg;
    private int ret;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public static class DataBean {
        /**
         * code : 0
         * info : [{"count":"2","id":"38","name":"#政策解读#","orderlist":"0","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f4e06ebd.png","type":"0"},{"count":"3","id":"15","name":"#红色旅游#","orderlist":"1","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f42d4c86.png","type":"0"},{"count":"0","id":"40","name":"#回家的路#","orderlist":"2","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f3712400.png","type":"0"},{"count":"10","id":"26","name":"#公益#","orderlist":"3","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f2c697d7.png","type":"0"},{"count":"17","id":"5","name":"#女神#","orderlist":"4","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f203c49f.png","type":"0"},{"id":"4","name":"#男神#","orderlist":"5","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f16eb076.png","type":"0"},{"count":"676","id":"3","name":"#好声音#","orderlist":"6","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f0de1155.png","type":"0"},{"count":"7","id":"7","name":"#脱口秀#","orderlist":"7","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f02ca87e.png","type":"0"},{"count":"3","id":"23","name":"#财经#","orderlist":"8","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734efa81b68.png","type":"0"},{"count":"3","id":"20","name":"#模仿达人#","orderlist":"9","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734ef022968.png","type":"0"},{"count":"2","id":"21","name":"#相声#","orderlist":"10","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c346f27c.png","type":"0"},{"count":"2","id":"17","name":"#记者在线#","orderlist":"11","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c2d538f0.png","type":"0"},{"count":"1","id":"22","name":"#健身#","orderlist":"12","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c2525db7.png","type":"0"},{"count":"657","id":"2","name":"#名师讲堂#","orderlist":"13","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c197dc43.png","type":"0"},{"count":"3","id":"19","name":"#儿童#","orderlist":"14","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c107240a.png","type":"0"},{"count":"1","id":"24","name":"#活动#","orderlist":"15","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734c06f2281.png","type":"0"},{"count":"0","id":"16","name":"#美食#","orderlist":"16","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bfac9ad0.png","type":"0"},{"count":"1","id":"18","name":"#原创#","orderlist":"17","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bf05263e.png","type":"0"},{"count":"1","id":"36","name":"#校园#","orderlist":"18","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734be6df532.png","type":"0"},{"count":"2","id":"37","name":"#美妆#","orderlist":"19","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bda730ab.png","type":"0"},{"count":"0","id":"25","name":"#其他#","orderlist":"20","thumb":"http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734bcabc1c7.png","type":"0"}]
         * msg :
         */

        private int code;
        private String msg;
        private List<InfoBean> info;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<InfoBean> getInfo() {
            return info;
        }

        public void setInfo(List<InfoBean> info) {
            this.info = info;
        }

        public static class InfoBean {
            /**
             * count : 2
             * id : 38
             * name : #政策解读#
             * orderlist : 0
             * thumb : http://bolema.wanchuangzhongchou.com/data/upload/20170109/58734f4e06ebd.png
             * type : 0
             */

            private String count;
            private String id;
            private String name;
            private String orderlist;
            private String thumb;
            private String type;

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getOrderlist() {
                return orderlist;
            }

            public void setOrderlist(String orderlist) {
                this.orderlist = orderlist;
            }

            public String getThumb() {
                return thumb;
            }

            public void setThumb(String thumb) {
                this.thumb = thumb;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
