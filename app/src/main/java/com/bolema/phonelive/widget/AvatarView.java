package com.bolema.phonelive.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.bolema.phonelive.R;
import com.bolema.phonelive.utils.BlurUtil;
import com.socks.library.KLog;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.utils.StringUtils;

public class AvatarView extends CircleImageView {
    public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
    public static final String MIDDLE_SIZE = "_100";
    public static final String LARGE_SIZE = "_200";

    private int id;
    private String name;
    private Activity aty;

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AvatarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        aty = (Activity) context;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name)) {
                    //UIHelper.showUserCenter(getContext(), id, name);
                }
            }
        });
    }

    public void setUserInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setAvatarUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            setImageResource(R.drawable.null_blacklist);
            return;
        }
        // 由于头像地址默认加了一段参数需要去掉
        int end = url.indexOf('?');
        final String headUrl;
        if (end > 0) {
            headUrl = url.substring(0, end);
        } else {
            headUrl = url;
        }
        KLog.d("hello!nihao!",headUrl);
//        .display(this, headUrl, R.drawable.null_blacklist, 0, 0,
        Core.getKJBitmap().doDisplay(this, headUrl,0,0, ContextCompat.getDrawable(getContext(),R.drawable.null_blacklist), 0, null,0,
                new BitmapCallBack() {
                    @Override
                    public void onFailure(Exception e) {
                        super.onFailure(e);
                        aty.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setImageResource(R.drawable.null_blacklist);
                            }
                        });
                        setImageResource(R.drawable.null_blacklist);
                    }
                });
    }


    public static String getSmallAvatar(String source) {
        return source;
    }

    public static String getMiddleAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
    }

    public static String getLargeAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
    }
}
