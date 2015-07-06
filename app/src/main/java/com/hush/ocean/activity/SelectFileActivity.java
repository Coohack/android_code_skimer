/**
 * Created by hack on 7/5/15.
 * Updated by hack on 7/5/15.
 * <p/>
 * Package com.hush.ocean.activity
 */
package com.hush.ocean.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hush.ocean.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectFileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_select_file);

        this.fileListView = (ListView) findViewById(R.id.selectFileList);
        this.fileListAdapter = new FileListAdapter(getApplicationContext());
        this.fileListView.setAdapter(this.fileListAdapter);
        this.fileListView.setOnItemClickListener(new FileListItemClick());

        List<File> files = new ArrayList<>(Arrays.asList(Environment.getExternalStorageDirectory().listFiles()));
        fileListAdapter.setFileList(files);
    }

    private FileListAdapter fileListAdapter = null;
    private ListView fileListView = null;

    class FileListItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            File file = (File) fileListAdapter.getItem(position);
            if (file == null) {
                return;
            }
            if (file.isDirectory()) {
                List<File> files = new ArrayList<>(Arrays.asList(file.listFiles()));
                fileListAdapter.setFileList(files);
            } else {
                Intent intent = getIntent();
                intent.putExtra("path", file.getAbsolutePath());
                setResult(0, intent);
                finish();
            }
        }
    }

    class FileListAdapter extends BaseAdapter {
        public FileListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        public void setFileList(List<File> files) {
            this.files = files;
            Collections.sort(this.files, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return files.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.select_file_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.selectFileItemImage);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.selectFileItemText);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (files.get(position).isDirectory()) {
                viewHolder.imageView.setImageResource(R.mipmap.folder);
            } else {
                viewHolder.imageView.setImageResource(R.mipmap.file);
            }
            viewHolder.textView.setText(files.get(position).getName());
            return convertView;
        }

        private List<File> files = new ArrayList<>();
        private LayoutInflater inflater = null;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
