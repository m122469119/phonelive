package com.bolema.phonelive.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bolema.phonelive.interf.DialogInterface;
import com.bolema.phonelive.R;

/**
 * 对话框辅助类
 */
public class DialogHelp {

    /***
     * 获取一个dialog
     * @param context
     * @return
     */
    public static AlertDialog.Builder getDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        return builder;
    }

    /***
     * 获取一个耗时等待对话框
     * @param context
     * @param message
     * @return
     */
    public static ProgressDialog getWaitDialog(Context context, String message) {
        ProgressDialog waitDialog = new ProgressDialog(context);
        if (!TextUtils.isEmpty(message)) {
            waitDialog.setMessage(message);
        }
        return waitDialog;
    }

    /***
     * 获取一个信息对话框，注意需要自己手动调用show方法显示
     * @param context
     * @param message
     * @param onClickListener
     * @return
     */
    public static AlertDialog.Builder getMessageDialog(Context context, String message, android.content.DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(message);
        builder.setPositiveButton("确定", onClickListener);
        return builder;
    }

    public static AlertDialog.Builder getMessageDialog(Context context, String message) {
        return getMessageDialog(context, message, null);
    }

    public static AlertDialog.Builder getConfirmDialog(Context context, String message, android.content.DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);

//        builder.setMessage(Html.fromHtml(message));
//        builder.setPositiveButton("确定", onClickListener);
//        builder.setNegativeButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getConfirmDialog(Context context, String message, android.content.DialogInterface.OnClickListener onOkClickListener, android.content.DialogInterface.OnClickListener onCancleClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setMessage(message);
        builder.setPositiveButton("确定", onOkClickListener);
        builder.setNegativeButton("取消", onCancleClickListener);
        return builder;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays, android.content.DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setItems(arrays, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setPositiveButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String[] arrays, android.content.DialogInterface.OnClickListener onClickListener) {
        return getSelectDialog(context, "", arrays, onClickListener);
    }

    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title, String[] arrays, int selectIndex, android.content.DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context);
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setNegativeButton("取消", null);
        return builder;
    }

    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String[] arrays, int selectIndex, android.content.DialogInterface.OnClickListener onClickListener) {
        return getSingleChoiceDialog(context, "", arrays, selectIndex, onClickListener);
    }
    public static void showDialog(LayoutInflater inflater,Context context,String msg,final DialogInterface dialogInface){
        View v = inflater.inflate(R.layout.dialog_show_onback_view,null);
        final Dialog dialog = new Dialog(context,R.style.dialog);
        dialog.setContentView(v);
        dialog.show();
        TextView mContent = (TextView) v.findViewById(R.id.tv_dialog_msg);
        Button   mCancel = (Button) v.findViewById(R.id.btn_dialog_cancel);
        Button   mDetermine = (Button) v.findViewById(R.id.btn_dialog_determine);
        mContent.setText(msg);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInface.cancelDialog(v,dialog);
            }
        });
        mDetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInface.determineDialog(v,dialog);
            }
        });
    }
    public static void showDialog(Context context,String msg,final DialogInterface dialogInface){
        View v = View.inflate(context,R.layout.dialog_show_onback_view,null);
        final Dialog dialog = new Dialog(context,R.style.dialog);
        dialog.setContentView(v);
        dialog.setCancelable(false);
        dialog.show();
        TextView mContent = (TextView) v.findViewById(R.id.tv_dialog_msg);
        Button   mCancel = (Button) v.findViewById(R.id.btn_dialog_cancel);
        Button   mDetermine = (Button) v.findViewById(R.id.btn_dialog_determine);
        mContent.setText(msg);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInface.cancelDialog(v,dialog);
            }
        });
        mDetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInface.determineDialog(v,dialog);
            }
        });
    }
    public static void showPromptDialog(LayoutInflater inflater,Context context,String msg,final DialogInterface dialogInface){
        View v = inflater.inflate(R.layout.dialog_show_prompt_view,null);
        final Dialog dialog = new Dialog(context,R.style.dialog);
        dialog.setContentView(v);
        dialog.show();
        TextView mContent = (TextView) v.findViewById(R.id.tv_dialog_msg);;
        Button   mDetermine = (Button) v.findViewById(R.id.btn_dialog_determine);
        mContent.setText(msg);
        mDetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInface.determineDialog(v,dialog);
            }
        });
    }
    public static void showPromptDialog2(Context context,String msg,final DialogInterface dialogInface){
        View v = View.inflate(context,R.layout.dialog_show_prompt_view,null);
        final Dialog dialog = new Dialog(context,R.style.dialog);
        dialog.setContentView(v);
        dialog.show();
        TextView mContent = (TextView) v.findViewById(R.id.tv_dialog_msg);;
        Button   mDetermine = (Button) v.findViewById(R.id.btn_dialog_determine);
        mContent.setText(msg);
        mDetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInface.determineDialog(v,dialog);
            }
        });
    }

    //iOS效果的确认框
    public static void showIOSStyleDialog(Context context, String message ,final DialogInterface dialogInterface) {
        View mDialogView = View.inflate(context, R.layout.dialog_show_own_info_detail, null);
        final Dialog dialog = new Dialog(context, R.style.dialog);
        TextView textView = (TextView) mDialogView.findViewById(R.id.show_msg);
        textView.setText(message);
        dialog.setContentView(mDialogView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        mDialogView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialogInterface.determineDialog(v,dialog);
            }
        });
        mDialogView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInterface.cancelDialog(v, dialog);
            }
        });
    }

}
