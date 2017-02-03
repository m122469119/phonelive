package com.bolema.phonelive.bean;

/**
 * Created by weipeng on 16/7/28.
 */
public class VipBean {

    /**
     * id : 1
     * viplevel : 1
     * needcoin : 30
     * thumb : http://changke.yunbaozhibo.com/public/appcmf/data/upload/20160726/57970f8a83cca.png
     * addtime : 1469502343
     */

    private String id;
    private String viplevel;
    private String needcoin;
    private String thumb;
    private String addtime;
    private String validtime;

    public String getValidtime() {
        return validtime;
    }

    public void setValidtime(String validtime) {
        this.validtime = validtime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getViplevel() {
        return viplevel;
    }

    public void setViplevel(String viplevel) {
        this.viplevel = viplevel;
    }

    public String getNeedcoin() {
        return needcoin;
    }

    public void setNeedcoin(String needcoin) {
        this.needcoin = needcoin;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
