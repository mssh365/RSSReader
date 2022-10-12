package com.myrss;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private ListView listview;
    private ListView collect_list;
    int item_id;//经过idlist变换的id，应当是正确的主键id
    int id;//bundle传入的id，和position_id一样需要转换
    SimpleAdapter adapter;
    SimpleAdapter collect_adapter;
    ArrayList<HashMap<String,Object>> itemList;
    ArrayList id_List = new ArrayList();//保存正确的主键id，用于将真实位置id（position）变换为主键id，防止删除某一条后表的主键id与position不匹配导致bug
    int position_id;//真实排序位置
    ArrayList<HashMap<String, Object>> data;
    int datalength;
    String search;
    Intent intent;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      intent=getIntent();
     bundle=intent.getExtras();
        if(!bundle.isEmpty()) {
            String mode = bundle.getString("mode");
            if(!mode.equals("search")) {
                id = bundle.getInt("id");
                //第一次构建id对照表
                ArrayList pre_id_list = new ArrayList();
                data = DBHelper.getInstance(this).getLampList();
                datalength = DBHelper.getInstance(this).getLampCount();
                for (int i = 0; i < datalength; i++) {
                    pre_id_list.add(data.get(i).get("id"));
                }
                if (id != -1) id = (int) pre_id_list.get(id);//id-1为添加新人
                //构建结束
//            Toast.makeText(this, "mode="+mode, Toast.LENGTH_SHORT).show();
                if (mode.equals("edit")) {
//                Toast.makeText(ActivityMain.this, "修改id=" + id, Toast.LENGTH_SHORT).show();
                    RSS product2 = new RSS(bundle.getString("name"), bundle.getString("url"), bundle.getInt("collect"));
                    int sucess2 = DBHelper.getInstance(SearchActivity.this).updateLamp(product2, id);
//                    if (sucess2 > 0) {
//                        Toast.makeText(SearchActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(SearchActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
//                    }
                }
                if (mode.equals("add")) {
                    RSS product1 = new RSS(bundle.getString("name"), bundle.getString("url"), bundle.getInt("collect"));
                    long sucess1 = DBHelper.getInstance(SearchActivity.this).saveLamp(product1);
//                    if (sucess1 > 0) {
//                        Toast.makeText(SearchActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(SearchActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
            else{
                search=bundle.getString("search");
                Toast.makeText(SearchActivity.this, search, Toast.LENGTH_SHORT).show();
            }
        }
        //第二次构建id对照表
        setContentView(R.layout.activity_search);
        listview=findViewById(R.id.listView_search);
        collect_list=findViewById(R.id.collect_list_search);
        itemList = new ArrayList<>();
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
                String url=null;
                for(int i=0;i< datalength;i++){
                    if (id == i) {
                        url = String.valueOf(data.get(i).get("url"));
                    }
                }
                    startActivity(new Intent(SearchActivity.this, RSSFeedActivity.class).putExtra("rssLink", url));

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

                name = String.valueOf(data.get((int)id).get("name"));
                url = String.valueOf(data.get((int)id).get("url"));
                collect = Integer.valueOf(data.get((int)id).get("collect").toString());
                //反向收藏
                collect=collect==1 ? 0 :1;

                int item_id=(int)id_List.get((int)id);
                RSS product = new RSS(name, url,collect);
//                Toast.makeText(SearchActivity.this, "修改"+item_id, Toast.LENGTH_SHORT).show();
                int success = DBHelper.getInstance(SearchActivity.this).updateLamp(product, item_id);
                if (success > 0) {
//                    Toast.makeText(SearchActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                }
                    else {
//                    Toast.makeText(SearchActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                }

                    refresh();
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
                new AlertDialog.Builder(SearchActivity.this)
                        .setTitle("警告")
                        .setMessage("确定要删除吗")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyDataSetChanged();
//                                Toast.makeText(SearchActivity.this, "删除id=" + item_id , Toast.LENGTH_SHORT).show();
//                                Toast.makeText(SearchActivity.this, "删除第" + position_id +"条", Toast.LENGTH_SHORT).show();
                                int flag = DBHelper.getInstance(SearchActivity.this).deleteLamp(item_id);
                                if(flag==1){
//                                    Toast.makeText(SearchActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    itemList.remove((int) position_id);//从itemlist里去除该条
                                    id_List.remove((int) position_id);//id对照表更新
                                }
                                else{
//                                    Toast.makeText(SearchActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("否", null).show();
                break;
            case R.id.menu_edit:
                Intent intent = new Intent(SearchActivity.this, ModifyActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("id", position_id);
                bundle.putString("mode","edit");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void refresh(){
        for (int i = itemList.size() - 1; i >= 0; i--) {
            itemList.remove(i);
        }
        for (int i = id_List.size() - 1; i >= 0; i--) {
            id_List.remove(i);
        }
        data = DBHelper.getInstance(SearchActivity.this).getLampList();
        datalength=DBHelper.getInstance(SearchActivity.this).getLampCount();
        if(!bundle.getString("mode").equals("search")) {
            for(int i=0;i< datalength;i++){
                HashMap<String,Object> map=new HashMap<>();
//                Toast.makeText(SearchActivity.this, i+"="+id_List.get(i), Toast.LENGTH_SHORT).show();
                id_List.add(data.get(i).get("id"));
                map.put("name",data.get(i).get("name"));
                map.put("url",data.get(i).get("url"));
                if((int)data.get(i).get("collect")==1) {
                    map.put("collect", R.drawable.collect);
                }
                else{
                    map.put("collect", R.drawable.uncollect);
                }
                itemList.add(map);
            }
        }
        else{
            ArrayList<HashMap<String, Object>> data2=new ArrayList<>();
            for(int i=0;i< datalength;i++){
                HashMap<String,Object> map=new HashMap<>();
//                Toast.makeText(SearchActivity.this, i+"="+id_List.get(i), Toast.LENGTH_SHORT).show();
                if(search_found(data.get(i).get("name").toString(),bundle.getString("search"))) {
                    id_List.add(data.get(i).get("id"));
                    map.put("name", data.get(i).get("name"));
                    map.put("url", data.get(i).get("url"));
                    if ((int) data.get(i).get("collect") == 1) {
                        map.put("collect", R.drawable.collect);
                    } else {
                        map.put("collect", R.drawable.uncollect);
                    }
                    itemList.add(map);
                    data2.add(map);
                }
            }
            data=data2;
        }
        adapter=new SimpleAdapter(SearchActivity.this,itemList,R.layout.item,new String[]
                {"name","url"},new int[]
                {R.id.text_view1,R.id.text_view2,R.id.collect_view});
        listview.setAdapter(adapter);

        collect_adapter=new SimpleAdapter(SearchActivity.this,itemList,R.layout.collect_item,new String[]
                {"collect"},new int[]
                {R.id.collect_view});
        collect_list.setAdapter(collect_adapter);
    }

    public void return_back(View view){
        Intent intent = new Intent(SearchActivity.this, ListActivity.class);
        Bundle bundle=new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void my_search(View view){
        EditText editText=(EditText)findViewById(R.id.searchText_search);
        Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("search",editText.getText().toString());
        bundle.putString("mode","search");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static boolean search_found(String name,String search){
        return name.indexOf(search)!=-1;

    }
}