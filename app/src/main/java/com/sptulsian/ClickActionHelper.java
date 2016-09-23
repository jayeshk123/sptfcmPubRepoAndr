package com.sptulsian;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by earth on 20/9/16.
 */
public class ClickActionHelper {
    public static void startActivity(String className, Bundle extras, Context context){
        Class cls = null;
        try {
            cls = Class.forName(className);
        }catch(ClassNotFoundException e){
            //means you made a wrong input in firebase console
        }
        Intent i = new Intent(context, cls);
        i.putExtras(extras);
        context.startActivity(i);
    }
}
