package com.myrss;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyActivity extends AppCompatActivity {
    String name;
    String url;
    int collect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从库中获取该id的数据
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        int id=bundle.getInt("id");
//        Toast.makeText(ModifyActivity.this, ""+id,Toast.LENGTH_SHORT).show();
        ArrayList<HashMap<String, Object>> data;
        data = DBHelper.getInstance(this).getLampList();
        int datalength=DBHelper.getInstance(this).getLampCount();
        for(int i=0;i< datalength;i++){
            if (id == i) {
                name = String.valueOf(data.get(i).get("name"));
                url = String.valueOf(data.get(i).get("url"));
                collect = Integer.valueOf(data.get(i).get("collect").toString());
            }
        }

        //获取完成
        setContentView(R.layout.activity_modify);
        ((EditText)findViewById(R.id.editText_name)).setText(name);
        ((EditText)findViewById(R.id.editText_URL)).setText(url);
        ((CheckBox)findViewById(R.id.collect)).setChecked(collect==1);
    }


    public void submit(View view){
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        int id=bundle.getInt("id");
        String mode=bundle.getString("mode");
        String name=((EditText)findViewById(R.id.editText_name)).getText().toString();
        String url=((EditText)findViewById(R.id.editText_URL)).getText().toString();
        int collect =((CheckBox)findViewById(R.id.collect)).isChecked() ? 1 : 0;//bool转int
        if(urlLegal(url)) {
            Intent intent_2 = new Intent(ModifyActivity.this, ListActivity.class);
            Bundle bundle_2 = new Bundle();
            bundle_2.putInt("id", id);
            bundle_2.putString("mode", mode);
            bundle_2.putCharSequence("name", name);
            bundle_2.putCharSequence("url", url);
            bundle_2.putInt("collect", collect);
            intent_2.putExtras(bundle_2);
            startActivity(intent_2);
        }
        else
        {
            Toast.makeText(ModifyActivity.this, "非法url",Toast.LENGTH_SHORT).show();

        }
    }

    public static boolean urlLegal(String url){
        Pattern p = Pattern.compile("(http|https|ftp)://((((25[0-5])|(2[0-4]\\d)|(1\\d{2})|([1-9]?\\d)\\.){3}((25[0-5])|(2[0-4]\\d)|(1\\d{2})|([1-9]?\\d)))|(([\\w-]+\\.)+(net|com|org|gov|edu|mil|info|travel|pro|museum|biz|[a-z]{2})))(/[\\w\\-~#]+)*(/[\\w-]+\\.[\\w]{2,4})?([\\?=&%_]?[\\w-]+)*");
        Matcher m = p.matcher(url);
        return m.matches();
    }
}
