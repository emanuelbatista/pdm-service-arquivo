package br.edu.ifpb.pdm.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by emanuel on 17/03/16.
 */
public class PreferenceList {

    private final String FILE="MY_PREFERNCE";
    private final String OFFSET="br.edu.ifpb.pdm.OFFSET";
    private final String HASH="br.edu.ifpb.pdm.HASH";
    public SharedPreferences sharedPreferences;

    public PreferenceList(Context context) {
        this.sharedPreferences = context.getSharedPreferences(FILE,0);
    }


    public void setOffset(Integer offset){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(OFFSET,offset);
        editor.commit();
    }

    public Integer getOffset(){
        return Integer.valueOf(sharedPreferences.getInt(OFFSET,0));
    }
    
    public void setHash(String hash){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(HASH, hash);
        editor.commit();
    }

    public String getHash(){
        return sharedPreferences.getString(HASH, null);
    }
}
