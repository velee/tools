package com.leec.tools.send;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Ve on 2014/8/11.
 */
public class SaveAs extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        //Uri.fromFile()

        Bundle extras = intent.getExtras();

        if (extras.containsKey(Intent.EXTRA_STREAM)) {
            Uri file = (Uri)intent.getExtras().get(Intent.EXTRA_STREAM);

            Toast.makeText(this, file.getScheme() + "--" + file.getPath(), Toast.LENGTH_LONG).show();

            //file.getLastPathSegment()

            Intent choose = new Intent("android.intent.action.GET_CONTENT");
            choose.setType("application/*");
            Intent save = Intent.createChooser(choose, "保存地址");
            //this.getResources().getString()
            save.putExtra(Intent.EXTRA_STREAM, file);
            startActivityForResult(save, 2);
        } else if (extras.containsKey(Intent.EXTRA_TEXT)) {
            Toast.makeText(this, extras.getString(Intent.EXTRA_TEXT), Toast.LENGTH_LONG).show();
        }

        //.getContentResolver().openInputStream(file);

    }
}
