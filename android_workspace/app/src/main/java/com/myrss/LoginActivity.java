package com.myrss;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class LoginActivity extends Activity {
    ProgressBar circleP;

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.UsernameLogin);   //获取输入的账号
        password = findViewById(R.id.Password);     //获取输入的密码

    }

    //添加按钮事件
    public void Login(View v){
        //这是能够登录的账号密码
        final String Usename = "1";
        final String Upwd = "1";

        //创建两个String类，储存从输入文本框获取到的内容
        final String user = username.getText().toString().trim();
        final String pwd = password.getText().toString().trim();

        circleP = (ProgressBar) findViewById(R.id.progress_cir);
        circleP.setVisibility(View.VISIBLE);
        final Intent intent=new Intent(LoginActivity.this,ListActivity.class);
        final Bundle bundle=new Bundle();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user.equals(Usename) & pwd.equals(Upwd)){
                    Toast.makeText(LoginActivity.this, "欢迎", Toast.LENGTH_SHORT).show();
                    intent.putExtras(bundle);
                    startActivity(intent);
                    circleP.setVisibility(View.GONE);
                }
                else{
                    circleP.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        },/*3000*/0);
    }
}