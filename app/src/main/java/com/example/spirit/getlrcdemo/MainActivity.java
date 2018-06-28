package com.example.spirit.getlrcdemo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    private String fileName;
    private MyAdapter adapter;
    private GridView gvView;
    private File[] files;
    private File temFile;
    private String rootName;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        rootName = Environment.getExternalStorageDirectory() + "/";
        fileName = Environment.getExternalStorageDirectory() + "/";

        File file = new File(fileName);
        fileScan(file);

        gvView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (files != null) {
                    temFile = files[position];

                    fileScan(temFile);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
//      super.onBackPressed();
        System.out.println("onBackPressed...");
        if (rootName.equals(temFile.getAbsolutePath())) {
            System.out.println("已经到了根目录...");
            return;
        }

        try {
            String parentPath = temFile.getParent();
            temFile = new File(parentPath);
            fileScan(temFile);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "已经到底了", Toast.LENGTH_SHORT).show();
        }
    }

    private void fileScan(File file) {
        files = file.listFiles();
        new Thread() {
            @Override
            public void run() {
                if (files != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter == null) {
                                adapter = new MyAdapter();
                                adapter.setFileList(files);
                                gvView.setAdapter(adapter);
                            } else {
                                adapter.setFileList(files);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    });
                }
            }
        }.start();
    }

//    private void fileScan(final File file) {
//        files = file.listFiles();
//        new Thread() {
//            @Override
//            public void run() {
//                if (files != null) {
//                    for (File item : files) {
//                        //item.isHidden()判断文件是否为隐藏的文件
//                        if (item.isDirectory() && !item.isHidden()) {
//                            //如果是文件夹进行再次扫描
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (adapter == null) {
//                                        adapter = new MyAdapter();
//                                        adapter.setFileList(files);
//                                        gvView.setAdapter(adapter);
//                                    }else {
//                                        adapter.setFileList(files);
//                                        adapter.notifyDataSetChanged();
//
//                                    }
//                                }
//                            });
//                            fileScan(item);
//                        } else if (!item.isHidden() && item.getName().endsWith(".txt")) {
//                            //判断是否以.txt后缀的文件
//                            //以下写上自己的处理，可以存到一个文件集合中List<File>
//                        }
//                    }
//                }
//            }
//        }.start();
//    }

    private void initView() {
        gvView = findViewById(R.id.gv_view);
    }

    class MyAdapter extends BaseAdapter {
        private File[] fileList;

        @Override
        public int getCount() {
            return fileList.length;
        }

        @Override
        public Object getItem(int position) {
            return fileList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .folder_item_layout, parent, false);
                holder.textView = convertView.findViewById(R.id.tv_folder);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(fileList[position].getName());
            if (!fileList[position].isDirectory()) {
                Drawable drawable = getResources().getDrawable(R.mipmap.file);
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

            } else {
                Drawable drawable = getResources().getDrawable(R.mipmap.file_folder);
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

            }

            return convertView;
        }

        public void setFileList(File[] fileList) {
            this.fileList = fileList;
        }

        class ViewHolder {
            TextView textView;
        }
    }
}

