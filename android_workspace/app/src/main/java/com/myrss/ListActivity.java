package com.myrss;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {
    Boolean collect_mode=false;
    private ListView listview;
    private ListView collect_list;
    int item_id;//经过idlist变换的id，应当是正确的主键id
    int id;//bundle传入的id，和position_id一样需要转换
    SimpleAdapter adapter;
    SimpleAdapter collect_adapter;
    ArrayList<HashMap<String,Object>> itemList;
    ArrayList id_List = new ArrayList();//保存正确的主键id，用于将真实位置id（position）变换为主键id，防止删除某一条后表的主键id与position不匹配导致bug
    ArrayList id_List_collect =new ArrayList();
    int position_id;//真实排序位置
    ArrayList<HashMap<String, Object>> data;
    int datalength;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(!bundle.isEmpty()) {
            id = bundle.getInt("id");
            //第一次构建id对照表
            ArrayList pre_id_list=new ArrayList();
            data = DBHelper.getInstance(this).getLampList();
            datalength=DBHelper.getInstance(this).getLampCount();
            for(int i=0;i< datalength;i++){
                pre_id_list.add(data.get(i).get("id"));
            }
            if(id!=-1)id=(int)pre_id_list.get(id);//id-1为添加新人
            //构建结束
            String mode=bundle.getString("mode");
//            Toast.makeText(this, "mode="+mode, Toast.LENGTH_SHORT).show();
            if(mode.equals("edit")) {
//                Toast.makeText(ActivityMain.this, "修改id=" + id, Toast.LENGTH_SHORT).show();
                RSS product2 = new RSS(bundle.getString("name"), bundle.getString("url"), bundle.getInt("collect"));
                int sucess2 = DBHelper.getInstance(ListActivity.this).updateLamp(product2, id);
//                if (sucess2 > 0) {
//                    Toast.makeText(ListActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ListActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
//                }
            }
            if(mode.equals("add")) {
                RSS product1 = new RSS(bundle.getString("name"), bundle.getString("url"), bundle.getInt("collect"));
                long sucess1 = DBHelper.getInstance(ListActivity.this).saveLamp(product1);
//                if (sucess1 > 0) {
//                    Toast.makeText(ListActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ListActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
//                }
            }
        }
        //第二次构建id对照表
        setContentView(R.layout.activity_list);
        listview=findViewById(R.id.listView);
        collect_list=findViewById(R.id.collect_list);

        itemList=new ArrayList<>();
        refresh();


        registerForContextMenu(listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /*
                parent:被点击的Adapter对象
                view： 被点击的Item（可用于获取该item内的组件）
                position：被点击的是第几个item(从0开始，0算第一个，类似数组)
                id：当前点击的item在listview 里的第几行的位置，通常id与position的值相同
             */
            public void onItemClick(AdapterView<?> parent, View view, int position,long  id) {
//                Toast.makeText(ListActivity.this, "选中第"+position+"行",Toast.LENGTH_SHORT).show();
//                Intent intent1=new Intent(ListActivity.this,TestActivity.class);
//                startActivity(intent1);
                int positionForData;
                if(collect_mode){
                    positionForData=positionCollectToOrigin(position);
                }
                else{
                    positionForData=position;
                }
                String url= String.valueOf(data.get(positionForData).get("url"));
                    startActivity(new Intent(ListActivity.this, RSSFeedActivity.class).putExtra("rssLink", url));

            }
        });
        collect_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /*
                parent:被点击的Adapter对象
                view： 被点击的Item（可用于获取该item内的组件）
                position：被点击的是第几个item(从0开始，0算第一个，类似数组)
                id：当前点击的item在listview 里的第几行的位置，通常id与position的值相同
             */
            public void onItemClick(AdapterView<?> parent, View view, int position,long  id) {
                String name=null;
                String url=null;
                Integer collect=null;
                int positionForData;
                if(collect_mode){
                    positionForData=positionCollectToOrigin(position);
                }
                else{
                    positionForData=position;
                }
                name = String.valueOf(data.get((int)positionForData).get("name"));
                url = String.valueOf(data.get((int)positionForData).get("url"));
                collect = Integer.valueOf(data.get((int)positionForData).get("collect").toString());
                //反向收藏
                collect=collect==1 ? 0 :1;

                int item_id;
                if(collect_mode)
                    item_id=(int)id_List_collect.get((int)id);
                else
                    item_id=(int)id_List.get((int)id);
                RSS product = new RSS(name, url,collect);
//                Toast.makeText(ListActivity.this, position+"修改"+positionForData, Toast.LENGTH_SHORT).show();
                int success = DBHelper.getInstance(ListActivity.this).updateLamp(product, item_id);
                if (success > 0) {
//                    Toast.makeText(ListActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    if(collect_mode) {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("name",data.get(positionForData).get("name"));
                        map.put("url",data.get(positionForData).get("url"));
                        map.put("collect",collect);
                        data.set(positionForData,map);
                        HashMap<String,Object> map2=new HashMap<>();
                        map2.put("name",data.get(positionForData).get("name"));
                        map2.put("url",data.get(positionForData).get("url"));
                        if(collect==1) {
                            map2.put("collect", R.drawable.collect);
                        }
                        else{
                            map2.put("collect", R.drawable.uncollect);
                        }
                        itemList.set(position,map2);
                        adapter=new SimpleAdapter(ListActivity.this,itemList,R.layout.item,new String[]
                                {"name","url"},new int[]
                                {R.id.text_view1,R.id.text_view2,R.id.collect_view});
                        listview.setAdapter(adapter);
                        collect_adapter=new SimpleAdapter(ListActivity.this,itemList,R.layout.collect_item,new String[]
                                {"collect"},new int[]
                                {R.id.collect_view});
                        collect_list.setAdapter(collect_adapter);
//                        Toast.makeText(ListActivity.this, "修改第" + position + "行", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(ListActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                }
                if(!collect_mode)refresh();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        position_id = info.position;
        item_id=(int)id_List.get(position_id);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_list, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                new AlertDialog.Builder(ListActivity.this)
                        .setTitle("警告")
                        .setMessage("确定要删除吗")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyDataSetChanged();
//                                Toast.makeText(ListActivity.this, "删除id=" + item_id , Toast.LENGTH_SHORT).show();
//                                Toast.makeText(ListActivity.this, "删除第" + position_id +"条", Toast.LENGTH_SHORT).show();
                                int flag = DBHelper.getInstance(ListActivity.this).deleteLamp(item_id);
                                if(flag==1){
//                                    Toast.makeText(ListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    itemList.remove((int) position_id);//从itemlist里去除该条
                                    if(collect_mode) {
                                        id_List_collect.remove((int) position_id);
                                        id_List.remove((int) position_id);//id对照表更新
                                    }
                                    else
                                        id_List.remove((int) position_id);//id对照表更新
                                }
                                else{
//                                    Toast.makeText(ListActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("否", null).show();
                break;
            case R.id.menu_edit:
                Intent intent = new Intent(ListActivity.this, ModifyActivity.class);
                Bundle bundle=new Bundle();
                int positionForData;
                if(collect_mode){
                    positionForData=positionCollectToOrigin(position_id);
                }
                else{
                    positionForData=position_id;
                }
                bundle.putInt("id", positionForData);
                bundle.putString("mode","edit");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
        return true;
    }


    public void create_student(View view){
        Intent intent = new Intent(ListActivity.this, ModifyActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("id", -1);
        bundle.putString("mode","add");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void collect_mode(View view){
        collect_mode =!collect_mode;
         Button button=findViewById(R.id.button3);
         if(collect_mode) {
             button.setText("仅限收藏");
         }
         else{
             button.setText("显示全部");
         }
         refresh();
    }
    public void refresh(){
        for (int i = itemList.size() - 1; i >= 0; i--) {
            itemList.remove(i);
        }
        for (int i = id_List.size() - 1; i >= 0; i--) {
            id_List.remove(i);
        }
        for (int i = id_List_collect.size() - 1; i >= 0; i--) {
            id_List_collect.remove(i);
        }
        data = DBHelper.getInstance(ListActivity.this).getLampList();
        datalength=DBHelper.getInstance(ListActivity.this).getLampCount();
        for(int i=0;i< datalength;i++){
            if(collect_mode){
                id_List.add(data.get(i).get("id"));
                if((int)data.get(i).get("collect")==1){
                    HashMap<String,Object> map=new HashMap<>();
//                    Toast.makeText(ListActivity.this, i+"="+id_List.get(i), Toast.LENGTH_SHORT).show();
                    map.put("name",data.get(i).get("name"));
                    map.put("url",data.get(i).get("url"));
                    if((int)data.get(i).get("collect")==1) {
                        map.put("collect", R.drawable.collect);
                        id_List_collect.add(data.get(i).get("id"));
                    }
                    else{
                        map.put("collect", R.drawable.uncollect);
                    }
                    itemList.add(map);
                }
            }
            else{
                HashMap<String,Object> map=new HashMap<>();
//                Toast.makeText(ListActivity.this, i+"="+id_List.get(i), Toast.LENGTH_SHORT).show();
                id_List.add(data.get(i).get("id"));
                map.put("name",data.get(i).get("name"));
                map.put("url",data.get(i).get("url"));
                if((int)data.get(i).get("collect")==1) {
                    map.put("collect", R.drawable.collect);
                    id_List_collect.add(data.get(i).get("id"));
                }
                else{
                    map.put("collect", R.drawable.uncollect);
                }
                itemList.add(map);
            }
        }
        adapter=new SimpleAdapter(ListActivity.this,itemList,R.layout.item,new String[]
                {"name","url"},new int[]
                {R.id.text_view1,R.id.text_view2,R.id.collect_view});
        listview.setAdapter(adapter);

        collect_adapter=new SimpleAdapter(ListActivity.this,itemList,R.layout.collect_item,new String[]
                {"collect"},new int[]
                {R.id.collect_view});
        collect_list.setAdapter(collect_adapter);
    }

    public void my_search(View view){
        EditText editText=(EditText)findViewById(R.id.searchText);
        Intent intent = new Intent(ListActivity.this, SearchActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("search",editText.getText().toString());
        bundle.putString("mode","search");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //将id_List_collect的position转换为id_List的position以便于正确的从data中取值
    public int positionCollectToOrigin(int position){
        int id=(int)id_List_collect.get(position);
        for (int i = id_List.size() - 1; i >= 0; i--) {
            if((int)id_List.get(i)==id) {
                return i;
            }
        }
        Toast.makeText(ListActivity.this, "转换失败", Toast.LENGTH_SHORT).show();
        return -1;//转换失败，返回原值防止程序直接崩溃
    }
}