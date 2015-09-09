package dbutil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hexiangpeng on 15/5/26.
 * 数据库，包含消息列表
 */
public class DataBaseSuiyue  extends SQLiteOpenHelper{
    public DataBaseSuiyue(Context context){
         super(context,"suiyue",null,2);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists messagelist1(id varchar(20) primary key,name varchar(50),lastmessage text,count int,photo text)");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

      //  db.execSQL("ALTER TABLE namepasswd ADD balance");
    }








}
