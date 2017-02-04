/**
 * douzifly @Aug 10, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.bolema.phonelive.widget.music;

import android.util.Log;

import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** default lrc builder,convert raw lrc string to lrc rows */

public class DefaultLrcBuilder implements  ILrcBuilder{
    static final String TAG = "DefaultLrcBuilder";
    public List<LrcRow> getLrcRows(String rawLrc) {
        Log.d(TAG,"getLrcRows by rawString");
        if(rawLrc == null || rawLrc.length() == 0){
            Log.e(TAG,"getLrcRows rawLrc null or empty");
            return null;
        }
        StringReader reader = new StringReader(rawLrc);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        List<LrcRow> rows = new ArrayList<LrcRow>();
        try{
            ArrayList<String> list = revertStringList(br.readLine());
            for (int i=0;i<list.size();i++) {
                line = list.get(i);
                if(line != null && line.length() > 0){
                    List<LrcRow> lrcRows = LrcRow.createRows(line);

                    if(lrcRows != null && lrcRows.size() > 0){
                        for(LrcRow row : lrcRows){
                            rows.add(row);
                        }
                    }
                }
            }
            if( rows.size() > 0 ){
                // sort by time:
                Collections.sort(rows);
            }
            
        }catch(Exception e){
            Log.e(TAG,"parse exceptioned:" + e.getMessage());
            return null;
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
        return rows;
    }

    private static ArrayList<String> revertStringList(String s){
        Pattern p = Pattern.compile("\\[[^\\[]*[\u4e00-\u9fa5\\]]");
        Matcher m = p.matcher(s);
        ArrayList<String> list = new ArrayList<>();
        while(m.find()){
            list.add(m.group());
        }
        return list;
    }
}
