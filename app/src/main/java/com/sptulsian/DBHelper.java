package com.sptulsian;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sptdb.db";
    public static final String User_TABLE_NAME = "User";

    public static final String User_COLUMN_ID = "id";
    public static final String User_COLUMN_username = "username";
    public static final String User_COLUMN_password = "password";
    public static final String User_COLUMN_token = "token";
    public static final String User_COLUMN_deviceId = "deviceId";


    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Users " +
                        "(id integer primary key, username text,password text, token text, deviceId text)"
        );
        db.execSQL(
                "insert into Users " +
                        "(id,username,password,token,deviceId) values(1,NULL,NULL,NULL,NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion==1 && newVersion==4) {
            db.execSQL("DROP TABLE IF EXISTS appToken");
            db.execSQL("DROP TABLE IF EXISTS appToken1");
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS Users " +
                            "(id integer primary key, username text,password text, token text, deviceId text)"
            );
            db.execSQL(
                    "insert into Users " +
                            "(id,username,password,token,deviceId) values(1,NULL,NULL,NULL,NULL)"
            );

        }else if(oldVersion==2 && newVersion==4){
            db.execSQL("DROP TABLE IF EXISTS appToken");
            db.execSQL("DROP TABLE IF EXISTS appToken1");
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS Users " +
                            "(id integer primary key, username text,password text, token text, deviceId text)"
            );
            db.execSQL(
                    "insert into Users " +
                            "(id,username,password,token,deviceId) values(1,NULL,NULL,NULL,NULL)"
            );
        }else if(oldVersion==3 && newVersion==4) {
            db.execSQL("DROP TABLE IF EXISTS appToken");
            db.execSQL("DROP TABLE IF EXISTS appToken1");
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS Users " +
                            "(id integer primary key, username text,password text, token text, deviceId text)"
            );
            db.execSQL(
                    "insert into Users " +
                            "(id,username,password,token,deviceId) values(1,NULL,NULL,NULL,NULL)"
            );
        }else{
            db.execSQL("DROP TABLE IF EXISTS appToken");
            db.execSQL("DROP TABLE IF EXISTS appToken1");

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS Users " +
                            "(id integer primary key, username text,password text, token text, deviceId text)"
            );
            db.execSQL(
                    "insert into Users " +
                            "(id,username,password,token,deviceId) values(1,NULL,NULL,NULL,NULL)"
            );

        }
       // onCreate(db);
    }

    public boolean insertUser(String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);

        db.insert("Users", null, contentValues);
        return true;
    }

    public Cursor getDBToken(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select token from Users where id="+id+"", null );
        return res;
    }

    public Cursor getDBDeviceId(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select deviceId from Users where id="+id+"", null );
        return res;
    }

    public Cursor getDBUsername(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select username from Users where id="+id+"", null );
        return res;
    }

    public Cursor getDBPassword(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select password from Users where id="+id+"", null );
        return res;
    }

    public boolean updateDBUser(Integer id, String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);

        db.update("Users", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateDBToken(Integer id, String token, String deviceId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("token", token);
        contentValues.put("deviceId", deviceId);
        db.update("Users", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    /////////////////////////////////////////////////////
    /*

    public long addNotification(String title, String desc, String link, String recdate, String visited, String zone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("desc", desc);
        contentValues.put("link", link);
        contentValues.put("recdate", recdate);
        contentValues.put("visited", visited);
        contentValues.put("zone", zone);

        long id = db.insert("notification", null, contentValues);
        return id;
       // return true;
    }

    public boolean insertToken(String token, String devicetype, String endpoint)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("token", token);
        contentValues.put("devicetype", devicetype);
        contentValues.put("endpoint", endpoint);
        db.insert("appToken", null, contentValues);
        return true;
    }



    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from appUser where id="+id+"", null );
        return res;
    }

    public Cursor getVolume(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from appVolume where id="+id+"", null );
        return res;
    }

    public boolean updateVolume (Integer id, float vol)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("vulume", vol);
        db.update("appVolume", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getAllNotifications(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification", null );
        return res;
    }

    public Cursor getFirstBoot(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from firstboot where id="+id+"", null );
        return res;
    }

    public Cursor getNotification(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification where id="+id+"", null );
        return res;
    }



    public Cursor getDataToken1(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from appToken1 where id="+id+"", null );
        return res;
    }



    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, User_TABLE_NAME);
        return numRows;
    }

    public int numberOfNotifications(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, Notification_TABLE_NAME);
        return numRows;
    }

    public int numberOfUnread(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "notification", "visited='0'");
        return numRows;
    }

    public int numberOfRowsNotification(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, Notification_TABLE_NAME);
        return numRows;
    }

    public int numberOfRowsToken(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, Token_TABLE_NAME);
        return numRows;
    }

    public Cursor getLastNotificationID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id from notification order by id DESC limit 1",null);
        return c;
    }


    public boolean updateNoteStatus (Integer id, String visited)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("visited", visited);


        db.update("notification", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateFirstBoot (Integer id, String first, String extra)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("first", first);
        contentValues.put("extra", extra);


        db.update("firstboot", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }





    public boolean updateToken1(Integer id, String token, String devicetype, String endpoint)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("token", token);
        contentValues.put("devicetype", devicetype);
        contentValues.put("endpoint", endpoint);
        db.update("appToken1", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMemberSettings(Integer id, String[] set)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ac_sys_alerts", set[0]);
        contentValues.put("stq_alerts", set[1]);
        contentValues.put("option_calls_stk", set[2]);
        contentValues.put("premium_picks_stk", set[3]);
        contentValues.put("golden_stocks_stk", set[4]);
        contentValues.put("portfolio_stocks_stk", set[5]);
        contentValues.put("multibagger_stocks_stk", set[6]);
        contentValues.put("danger_stocks_stk", set[7]);
        contentValues.put("stock_reco_stk", set[8]);
        contentValues.put("intraday_calls_stk", set[9]);
        contentValues.put("future_calls_stk", set[10]);
        contentValues.put("market_whispers_stk", set[11]);

        db.update("memberSettings", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateFreeSettings(Integer id,String[] set)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ac_sys_alerts", set[0]);
        contentValues.put("stq_alerts", set[1]);
        contentValues.put("option_calls_stk", set[2]);
        contentValues.put("premium_picks_stk", set[3]);
        contentValues.put("golden_stocks_stk", set[4]);
        contentValues.put("portfolio_stocks_stk", set[5]);
        contentValues.put("multibagger_stocks_stk", set[6]);
        contentValues.put("danger_stocks_stk", set[7]);
        contentValues.put("intraday_calls_stk", set[8]);


        db.update("freeSettings", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public void resetUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ User_TABLE_NAME);

    }

    public Integer deleteUser (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("appUser",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteAllNotifications ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
         db.execSQL("delete from "+ Notification_TABLE_NAME);

    }

    public Integer deleteNotification (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("notification",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteToken (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("appToken",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteMemberSettings (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("memberSettings",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteFreeSettings (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("freeSettings",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor getLink(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select link from notification where id=" + id + "", null);
        return res;
    }


    public ArrayList<String> getAllUsers()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from appUser", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(User_COLUMN_username)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllNotificationsID()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification;", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllNotificationsTitle()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification;", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_title)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllNotificationsType()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification;", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_zone)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllNotificationsVisited()
    {
        ArrayList<String> array_list = new ArrayList<String>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification;", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_visited)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllNotificationsDesc()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification;", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_desc)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllNotificationsLink()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification;", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_link)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllNotificationsState()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification ORDER BY id DESC;", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_visited)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllTokens()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from appToken", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Token_COLUMN_token)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllMemberSettings()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from memberSettings", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Member_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllFreeSettings()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from freeSettings", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Member_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }



    public ArrayList<String> getAllUnreadNotificationsTitle()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification where visited = '0';", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_title)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllUnreadNotificationsID()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification where visited = '0';", null );
        res.moveToFirst();
//  Cursor res =  db.rawQuery( "select * from (select * from notification order by title ASC limit 5) order by title DESC;", null );

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Notification_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }
*/

}