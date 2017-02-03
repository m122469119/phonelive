package com.bolema.phonelive.bean;

import java.util.List;

/**
 * Created by yuanshuo on 2017/1/25.
 */

public class LocalMusicBean {

    /**
     * ret : 200
     * data : {"code":0,"msg":"","info":[{"id":"4","mc_name":"悟空","singer":"戴荃","url":"http://bolema.wanchuangzhongchou.com/data/upload/music/20170124/5886cd0563433.mp3","sort":"0"}]}
     * msg :
     */

    private int ret;
    private DataBean data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

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

    public static class DataBean {
        /**
         * code : 0
         * msg :
         * info : [{"id":"4","mc_name":"悟空","singer":"戴荃","url":"http://bolema.wanchuangzhongchou.com/data/upload/music/20170124/5886cd0563433.mp3","sort":"0"}]
         */

        private int code;
        private String msg;
        private List<MusicBean> info;

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

        public List<MusicBean> getInfo() {
            return info;
        }

        public void setInfo(List<MusicBean> info) {
            this.info = info;
        }

        public static class MusicBean {
            /**
             * id : 4
             * mc_name : 悟空
                 * singer : 戴荃
             * url : http://bolema.wanchuangzhongchou.com/data/upload/music/20170124/5886cd0563433.mp3
             * sort : 0
             */

            private String id;
            private String mc_name;
            private String singer;
            private String url;
            private String sort;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getMc_name() {
                return mc_name;
            }

            public void setMc_name(String mc_name) {
                this.mc_name = mc_name;
            }

            public String getSinger() {
                return singer;
            }

            public void setSinger(String singer) {
                this.singer = singer;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getSort() {
                return sort;
            }

            public void setSort(String sort) {
                this.sort = sort;
            }
        }
    }
}
