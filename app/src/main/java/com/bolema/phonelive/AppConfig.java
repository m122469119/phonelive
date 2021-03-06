package com.bolema.phonelive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import io.socket.client.IO;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 */
public class AppConfig {
    //api地址
    public static final String MAIN_URL = "http://bolema.wanchuangzhongchou.com/api/public/";  //HHH 2016-09-09
    //域名
    public static final String MAIN_URL2 = "http://bolema.wanchuangzhongchou.com";//HHH 2016-09-09
    //推流地址
    // public static final String RTMP_URL = "rtmp://xb.taianweb.com/5showcam/";//HHH 2016-09-09
    public static final String RTMP_URL = "rtmp://video-center.alivecdn.com/5showcam/";//HHH 2016-09-09
    //拉流地址
    //public static final String RTMP_URL2 = "rtmp://xb.taianweb.com/5showcam/";//HHH 2016-09-09
    public static final String RTMP_URL2 = "rtmp://t.wanchuangzhongchou.com/5showcam/";//HHH 2016-09-09

    //nodejs聊天服务器地址
    public static final String CHAT_URL = "http://114.55.249.76:19967";//HHH 2016-09-09
    //支付宝回调地址
    public static final String AP_LI_PAY_NOTIFY_URL = "http://bolema.wanchuangzhongchou.com/alipay_app/notify_url.php";
    //分享
    public static String SHARE_URL = "";

    //QQ音乐接口，APPID 以及 sign
    public static String QQ_MUSIC_URL = "http://route.showapi.com/213-1";
    public static String QQ_MUSIC_APPID = "31538";
    public static String QQ_MUSIC_SIGN = "f3011077286b4e3d89199932415095b3";

    //音效
    public static int voiceLevel = 1;

    private final static String APP_CONFIG = "config";

    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";

    public static final String KEY_TWEET_DRAFT = "KEY_TWEET_DRAFT";
    public static final String KEY_NOTE_DRAFT = "KEY_NOTE_DRAFT";

    public static final String KEY_FRITST_START = "KEY_FRIST_START";

    public static final String KEY_NIGHT_MODE_SWITCH = "night_mode_switch";


    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "phoneLive"
            + File.separator + "live_img" + File.separator;

    // 默认存放文件下载的路径
    public final static String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "phoneLive"
            + File.separator + "download" + File.separator;

    public final static String DEFAULT_SAVE_MUSIC_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "phoneLive"
            + File.separator + "music" + File.separator;


    private Context mContext;
    private static AppConfig appConfig;

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (IOException ignored) {
            ignored.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在files目录下
            // fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }
}
