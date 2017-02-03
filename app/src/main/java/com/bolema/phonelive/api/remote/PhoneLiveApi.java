package com.bolema.phonelive.api.remote;
import android.util.Log;

import com.bolema.phonelive.AppConfig;
import com.bolema.phonelive.AppContext;
import com.bolema.phonelive.bean.GiftBean;
import com.bolema.phonelive.bean.UserBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.sharesdk.framework.PlatformDb;

/**
 * 接口获取
 */
public class PhoneLiveApi {

    /**
    * 登陆
    * @param phone
    * @param code
    * */

    //HHH 2016-09-09
    public static void login(String phone,String password,String code,StringCallback callback){
        String url = AppConfig.MAIN_URL;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","user.userlogin")
                    .addParams("user_login",phone)
                    .addParams("user_pass",URLEncoder.encode(password,"UTF-8"))
                     //.addParams("code",code)
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    //HHH 2016-09-09
    public static void reg(String user_login,String user_pass,String user_pass2,String code,StringCallback callback){
        String url = AppConfig.MAIN_URL;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","user.userReg")
                    .addParams("user_login",user_login)
                    .addParams("user_pass",URLEncoder.encode(user_pass,"UTF-8"))
                    .addParams("user_pass2",URLEncoder.encode(user_pass2,"UTF-8"))
                    .addParams("code",code)
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void findPass(String user_login,String user_pass,String user_pass2,String code,StringCallback callback){
        String url = AppConfig.MAIN_URL;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","user.userFindPass")
                    .addParams("user_login",user_login)
                    .addParams("user_pass",URLEncoder.encode(user_pass,"UTF-8"))
                    .addParams("user_pass2",URLEncoder.encode(user_pass2,"UTF-8"))
                    .addParams("code",code)
                    .tag("findPass")
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    /**
     * 获取用户信息
     * @param token appkey
     * @param uid 用户id
     * @param callback 回调
     * */
    public static void getMyUserInfo(int uid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getBaseInfo")
                .addParams("uid", String.valueOf(uid))
                .addParams("token",token)
                .tag("getMyUserInfo")
                .build()
                .execute(callback);

    }
    /**
     * 获取其他用户信息
     * @param uid 用户id
     * @param callback 回调
     * */
    public static void getOtherUserInfo(int uid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getUserInfo")
                .addParams("uid", String.valueOf(uid))
                .tag("getOtherUserInfo")
                .build()
                .execute(callback);

    }
    /**
     * @dw 修改用户信息
     * @param key   参数
     * @param value 修改
     * */
    public static void saveInfo(String key, String value, int uid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.userUpdate")
                .addParams("field", key)
                .addParams("value",value)
                .addParams("uid", String.valueOf(uid))
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取热门首页轮播
    * */
    public static void getIndexHotRollpic(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getSlide")
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 获取热门首页主播
     * */
    public static void getIndexHotUserList(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.searchArea")
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 进入直播间初始化信息
     * @param uid 当前用户id
     * @param showId 主播id
     * @param token  token
     * */
    public static void initRoomInfo(int uid,int showId,String token,String address,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.setNodejsInfo")
                .addParams("uid", String.valueOf(uid))
                .addParams("showid", String.valueOf(showId))
                .addParams("token", token)
                .addParams("city", address)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    *
    * @dw 获取用户列表
    * @param showid 房间号码
    *
    * */
    public static void getRoomUserList(int showid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getRedislist")
                .addParams("size","0")
                .addParams("showid", String.valueOf(showid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 开始直播
     * @param uid 主播id
     * @param title 开始直播标题
     * @param token
     * */
    public static void createLive(int uid,String stream, String title, StringCallback callback, String token) {
        try {
            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL)
                    .addParams("service", "User.createRoom")
                    .addParams("uid", String.valueOf(uid))
                    .addParams("title",URLEncoder.encode(title,"UTF-8"))
                    .addParams("stream",stream)
                    .addParams("city", AppContext.address)
                    .addParams("province",AppContext.province)
                    .addParams("lat", AppContext.lat)  //HHH 2016-09-09
                    .addParams("lng",AppContext.lng)
                    .addParams("token",token)
                    .tag("phonelive")
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
        }

    }
    /**
    * @dw 关闭直播
    * @param token 用户的token
    * */
    public static void closeLive(int id,String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.stopRoom")
                .addParams("uid",String.valueOf(id))
                .addParams("token",token)
                .tag("closeLive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取礼物列表
    * @param callback
    * */
    public static void getGiftList(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getGifts")
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 赠送礼物
    * @param g 赠送礼物信息
    * @param mUser 用户信息
    * @param mNowRoomNum 房间号(主播id)
    * */
    public static void sendGift(UserBean mUser, GiftBean g, int mNowRoomNum, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.sendGift")
                .addParams("token",mUser.getToken())
                .addParams("uid",String.valueOf(mUser.getId()))
                .addParams("touid", String.valueOf(mNowRoomNum))
                .addParams("giftid", String.valueOf(g.getId()))
                .addParams("giftcount","1")
                .tag("phonelive")
                .build()
                .execute(callback);
    }
     /**
     * @dw 发送弹幕 HHH
     * @param content  弹幕信息
     * @param mUser 用户信息
     * @param mNowRoomNum 房间号(主播id)
     * */
    public static void sendBarrage(UserBean mUser, String content, int mNowRoomNum, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.sendBarrage")
                .addParams("token",mUser.getToken())
                .addParams("uid",String.valueOf(mUser.getId()))
                .addParams("touid", String.valueOf(mNowRoomNum))
                .addParams("content",content)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取其他用户信息
    * @param uid 其他用户id
    * @param ucuid 当前用户自己的id
    * */
    public static void getUserInfo(int uid,int ucuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getPopup")
                .addParams("uid",String.valueOf(uid))
                .addParams("ucuid",String.valueOf(ucuid))
                .tag("getUserInfo")
                .build()
                .execute(callback);
    }
    /**
    * @dw 判断是否关注
    * @param touid 当前主播id\
    * @param uid 当前用户uid
    * */
    public static void getIsFollow(int uid, int touid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.isAttention")
                .addParams("uid",String.valueOf(uid))
                .addParams("touid",String.valueOf(touid))
                .tag("getIsFollow")
                .build()
                .execute(callback);

    }
    /**
     * @dw 关注
     * @param uid 当前用户id
     * @param touid 关注用户id
     */
    public static void showFollow(int uid, int touid,String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.setAttention")
                .addParams("uid",String.valueOf(uid))
                .addParams("showid",String.valueOf(touid))
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取homepage中的用户信息
    * @param uid 查询用户id
    * */
    public static void getHomePageUInfo(int uid,int ucuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.getUserHome")
                .addParams("uid",String.valueOf(uid))
                .addParams("ucuid",String.valueOf(ucuid))
                .tag("getHomePageUInfo")
                .build()
                .execute(callback);
    }
    /**
     * @dw 获取homepage用户的fans
     * @param ucid 自己的id
     * @param uid 查询用户id  */
    public static void getFansList(int uid, int ucid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.getFans")
                .addParams("uid",String.valueOf(uid))
                .addParams("ucuid",String.valueOf(ucid))
                .tag("getFansList")
                .build()
                .execute(callback);
    }
    /**
     * @dw 获取homepage用户的关注用户列表
     * @param ucid 自己的id
     * @param uid 查询用户id
     * */
    public static void getAttentionList(int uid, int ucid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.getAttention")
                .addParams("uid",String.valueOf(uid))
                .addParams("ucuid",String.valueOf(ucid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 获取魅力值排行
     * @param uid 查询用户id
     * */
    public static void getYpOrder(int uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getCoinRecord")
                .addParams("uid",String.valueOf(uid))
                .tag("getYpOrder")
                .build()
                .execute(callback);

    }
    /**
     * @dw 获取收益信息
     * @param uid 查询用户id
     * @param token token
     * */
    public static void getWithdraw(int uid, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.getWithdraw")
                .addParams("uid",String.valueOf(uid))
                .addParams("token",token)
                .tag("getWithdraw")//BBB
                .build()
                .execute(callback);

    }
    /**
     * @dw 获取最新
     * */
    public static void getNewestUserList(StringCallback callback) {

        //HHH 2016-09-09
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service", "User.getNew")
                .addParams("lng", AppContext.lng)
                .addParams("lat", AppContext.lat)
                .build()
                .execute(callback);

    }
    /**
    * @dw 更新头像
    * @param uid 用户id
    * @param token
    * @param protraitFile 图片文件
    *
    * */
    public static void updatePortrait(int uid,String token, File protraitFile, StringCallback callback) {
        OkHttpUtils.post()
                .url(AppConfig.MAIN_URL+"appapi/")
                .addParams("service","User.upload")
                .addFile("file", "wp.png", protraitFile)
                .addParams("uid", String.valueOf(uid))
                .addParams("token", token)
                //.url(AppConfig.MAIN_URL + "appapi/?service=User.upload")
                .tag("phonelive")
                .build()
                .execute(callback);

    }

    /**
    * @dw 提现
    * @param uid 用户id
    * @param token
    * */
    public static void requestCash(int uid, String token,String money, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.userCash")
                .addParams("uid",String.valueOf(uid))
                .addParams("token", token)
                .addParams("money", money)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 直播记录
     * @param uid 用户id
     * */
    public static void getLiveRecord(int uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getLiveRecord")
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 支付宝下订单
    * @param uid 用户id
    * */
    public static void getAliPayOrderNum(int uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getAliOrderId")
                .addParams("uid",String.valueOf(uid))
                .addParams("money","1")
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    //定位
    public static void getAddress(StringCallback callback) {
        OkHttpUtils.get()
                .url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json")
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    *@dw 搜索
    *@param screenKey 搜索关键词
    *@param uid 用户id
    * */
    public static void search(String screenKey, StringCallback callback,int uid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.search")
                .addParams("key",screenKey)
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取地区列表
    *
    * */
    public static void getAreaList(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getArea")
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 地区检索
    * @param sex 性别
    * @param area 地区
    * */

    public static void selectTermsScreen(int sex, String area, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.searchArea")
                .addParams("sex",String.valueOf(sex))
                .addParams("key",area)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 批量获取用户信息
    * @param uidList 用户id字符串 以逗号分割
    *
    * */
    public static void getMultiBaseInfo(int action,int uid,String uidList, StringCallback callback) {

            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL)
                    .addParams("service","User.getMultiBaseInfo")
                    .addParams("uids",uidList)
                    .addParams("type",String.valueOf(action))
                    .addParams("uid",String.valueOf(uid))
                    .tag("phonelive")
                    .build()
                    .execute(callback);

    }

    /**
    * @dw 获取已关注正在直播的用户
    * @param uid 用户id
    * */
    public static void getAttentionLive(int uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.attentionLive")
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取用户信息私聊专用
    * @param uid  当前用户id
    * @param ucuid  to uid
    * */

    public static void getPmUserInfo(int uid, int ucuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getPmUserInfo")
                .addParams("uid",String.valueOf(uid))
                .addParams("ucuid",String.valueOf(ucuid))
                .tag("getPmUserInfo")
                .build()
                .execute(callback);

    }

    /**
     * @dw 微信支付
     * @param uid 用户id
     * @param price 价格
     **/
    public static void wxPay(int uid,String token, String price, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getWxOrderId")
                .addParams("uid",String.valueOf(uid))
                .addParams("token",token)
                .addParams("money",price)
                .build()
                .execute(callback);

    }
    /**
    * @dw 第三方登录
    * @param platDB  用户信息
    * @param type 平台
    * */
    public static void otherLogin(String type,PlatformDb platDB, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.userLoginByThird")
                .addParams("openid",platDB.getUserId())
                .addParams("nicename",platDB.getUserName())
                .addParams("type",type)
                .addParams("avatar",platDB.getUserIcon())
                .tag("phonelive")
                .build()
                .execute(callback);
  
    }
    /**
    * @dw 设为管理员
    * @param roomnum 房间号码
    * @param touid 操作id
    * @param token 用户登录token
    * */
    public static void setManage(int roomnum, int touid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.setAdmin")
                .addParams("showid",String.valueOf(roomnum))
                .addParams("uid",String.valueOf(touid))
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);

    }
    /**
    * @dw 判断是否为管理员
    * @param uid 用户id
    * @param showid 房间号码
    * */

    public static void isManage(int showid, int uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getIsAdmin")
                .addParams("showid",String.valueOf(showid))
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);

    }
    /**
    * @dw 禁言
    * @param showid 房间id
    * @param touid 被禁言用户id
    * @param token 用户登录token
    * */
    public static void setShutUp(int showid, int touid, int uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.setShutUp")
                .addParams("showid",String.valueOf(showid))
                .addParams("touid",String.valueOf(touid))
                .addParams("uid",String.valueOf(uid))
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    //是否禁言解除
    public static void isShutUp(int uid, int showid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.isShutUp")
                .addParams("showid",String.valueOf(showid))
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    //token是否过期
    public static void tokenIsOutTime(int uid, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.iftoken")
                .addParams("uid",String.valueOf(uid))
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 拉黑
    * */
    public static void pullTheBlack(int uid, int touid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.setBlackList")
                .addParams("uid", String.valueOf(uid))
                .addParams("showid", String.valueOf(touid))
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);

    }
    /**
    * @dw 黑名单列表
    * */
    public static void getBlackList(int uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getBlackList")
                .addParams("uid", String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }

    public static void getMessageCode(String phoneNum) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getCode")
                .addParams("mobile", phoneNum)
                .tag("phonelive")
                .build()
                .execute(null);
    }
    /**
    * @dw 获取用户余额
    * @param uid 用户id
    * */
    public static void getUserDiamondsNum(int uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getUserPrivateInfo")
                .addParams("uid",String.valueOf(uid))
                .addParams("token",token)
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 点亮
    * @param uid 用户id
    * @param token 用户登录token
    * @param showid 房间号
    * */
    public static void showLit(int uid,String token,int showid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.setLight")
                .addParams("uid",String.valueOf(uid))
                .addParams("token",token)
                .addParams("showid", String.valueOf(showid))
                .tag("phonelive")
                .build()
                .execute(null);
    }
    /**
    * @dw 百度接口搜索音乐
    * @param keyword 歌曲关键词
    * */
    public static void searchMusic(String keyword, StringCallback callback) {
        //baidu music interface (http://tingapi.ting.baidu.com/v1/restserver/ting)
//        OkHttpUtils.get()
//                .url("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.catalogSug&query=" + keyword)
//                .tag("phonelive")
//                .build()
//                .execute(callback);
        OkHttpUtils.get()
                .url("http://bolema.wanchuangzhongchou.com//api/public//?service=Music.getmusic&mc_name=" + keyword)
                .tag("phonelive")
                .build()
                .execute(callback);

    }
    /**
    * @dw 获取music信息
    * @param songid 歌曲id
     *              http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.downWeb&songid=22055156&bit=24&_t=
    * */
    public static void getMusicFileUrl(String songid, StringCallback callback) {
        OkHttpUtils.get()
                .url("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.downWeb&songid=" + songid + "&bit=24&_t=" + System.currentTimeMillis())
                .tag("phonelive")
                .build()
                .execute(callback);
    }

    /**
     * @param musicUrl 下载地址
     * @dw 下载音乐文件
     */
    public static void downloadMusic(String musicUrl, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicUrl)
                .tag("phonelive")
                .build()
                .execute(fileCallBack);
    }
    /**
     * @dw 开播等级限制
     * */
    public static void getLevelLimit(int uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getLevelLimit")
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 检查新版本
     * */
    public static void checkUpdate(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getVersion")
                .tag("phonelive")
                .build()
                .execute(callback);
    }

    public static void downloadLrc(String musicLrc, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicLrc)
                .tag("phonelive")
                .build()
                .execute(fileCallBack);
    }
    /**
    * @dw 下载最新apk
    * @param apkUrl  下载地址
    * */
    public static void getNewVersionApk(String apkUrl,FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(apkUrl)
                .tag("phonelive")
                .build()
                .execute(fileCallBack);
    }
    /**
    * @dw 获取管理员列表
    * @param uid  用户id
    * */
    public static void getManageList(int uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getAdminList")
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取更多用户列表
    * @param mRoomNum 房间号码
    * @param size 当前人数
    * */
    public static void loadMoreUserList(int size, int mRoomNum, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getRedislist")
                .addParams("size", String.valueOf(size))
                .addParams("showid", String.valueOf(mRoomNum))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 判断是否是第一次充值
     * @param uid 用户id
     * */

    public static void getCharge(int uid,StringCallback callback){
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getCharge")
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    //举报
    public static void report(int uid, int touid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.setReport")
                .addParams("uid",String.valueOf(uid))
                .addParams("touid",String.valueOf(touid))
                .addParams("content","涉嫌传播淫秽色情信息")
                .tag("report")
                .build()
                .execute(null);

    }
    public static void getVideoCode(String url,StringCallback callback){
        OkHttpUtils.get()
                .url(url)
                .tag("getVideoCode")
                .build()
                .execute(callback);
    }

    //获取直播记录
    public static void getLiveRecordById(String showid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getAliCdnRecord")
                .addParams("id",showid)
                .tag("getLiveRecordById")
                .build()
                .execute(callback);



    }

    //HHH 2016-09-13
    /**
     * @dw 获取vip列表
     * */
    public static void getVipList(int uid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getVipThumb")
                .addParams("uid",String.valueOf(uid))
                .build()
                .execute(callback);
    }

    public static void getShowVip(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.showvip")
                .tag("getShowVip")
                .build()
                .execute(callback);
    }

    /**
     * @dw 判断是否购买vip
     * @param uid 用户id
     * @param viplevel vip登记
     *
     * */
    public static void isBuyVip(int uid, String viplevel, StringCallback isBuyVipCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.isBuyVip")
                .addParams("uid",String.valueOf(uid))
                .addParams("viplevel",viplevel)
                .build()
                .execute(isBuyVipCallback);
    }
    /**
     * @dw 购买vip
     * @param uid 用户id
     * @param token
     * */
    public static void buyVip(int uid, String token,StringCallback buyVipCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.buyvip")
                .addParams("uid",String.valueOf(uid))
                .addParams("token",token)
                .build()
                .execute(buyVipCallback);
    }

    //HHH 2016-09-13
    public static void getConfig(StringCallback buyVipCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getConfig")
                .build()
                .execute(buyVipCallback);
    }

    public static void exchangVote(int uid, String  votes,  StringCallback exchangVoteCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.exchange")
                .addParams("uid",String.valueOf(uid))
                .addParams("votes",votes)
                .build()
                .execute(exchangVoteCallback);
    }


    public static void getTopics(String count,String order,String type , StringCallback getTopicsCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.getTopiclist")
                .addParams("count",count)
                .addParams("order",order)
                .addParams("type",type)
                .build()
                .execute(getTopicsCallback);
    }

    public static void getTopicRooms(String topic, StringCallback getTopicsCallback) {
        try {
            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL)
                    .addParams("service","User.getTopicroom")
                    .addParams("topic", URLEncoder.encode(topic,"UTF-8"))
                    .tag("getTopicRooms")
                    .build()
                    .execute(getTopicsCallback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getChangePass(String uid,String token,String oldpass,String pass1st,String pass2nd, StringCallback getChangePassCallback) {
        try {
            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL)
                    .addParams("service","User.changepass")
                    .addParams("uid",uid)
                    .addParams("token",token)
                    .addParams("oldpass",URLEncoder.encode(oldpass,"UTF-8"))
                    .addParams("pass1st",URLEncoder.encode(pass1st,"UTF-8"))
                    .addParams("pass2nd",URLEncoder.encode(pass2nd,"UTF-8"))
                    .build()
                    .execute(getChangePassCallback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getPubMsg(StringCallback getPubMsgCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL)
                .addParams("service","User.pub_msg")
                .build()
                .execute(getPubMsgCallback);
    }



}
