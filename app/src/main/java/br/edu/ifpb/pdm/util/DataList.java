package br.edu.ifpb.pdm.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by emanuel on 17/03/16.
 */
public class DataList extends SQLiteOpenHelper {

    private static final String NOME_DATA = "banco.db";
    public static final String NOME_TABLE = "List";
    public static final String ID = "id";
    public static final String VALUE = "value";
    private static final Integer VERSION = 1;

    public DataList(Context context) {
        super(context, NOME_DATA, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(NOME_TABLE).append("(")
                .append(ID).append(" integer primary key autoincrement,")
                .append(VALUE).append(" text")
                .append(")");
        db.execSQL(sql.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOME_TABLE);
        onCreate(db);
    }

    public void save(String nome) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VALUE, nome);
        db.insert(NOME_TABLE, null, contentValues);
        db.close();
    }

    public List<String> findAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] campos = {VALUE};
        Cursor cursor = db.query(NOME_TABLE, campos, null, null, null, null, null);
        List<String> list = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            //
            while (!cursor.isAfterLast()) {
                list.add(cursor.getString(0));
                cursor.moveToNext();
            }
            return list;
        }
        db.close();
        return list;
    }

    public void save(List<String> list) {
        for (String value : list) {
            save(value);
        }
    }
}
