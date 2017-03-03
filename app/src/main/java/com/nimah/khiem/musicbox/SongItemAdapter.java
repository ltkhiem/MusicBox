package com.nimah.khiem.musicbox;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;

/**
 * Created by Khiem on 8/12/2016.
 */
public class SongItemAdapter extends ArrayAdapter<Song> {
    Context context;
    int rcId;
    ArrayList<Song> SongList;

    public SongItemAdapter(Context context, int resource, ArrayList<Song> data) {
        super(context, resource, data);
        this.context = context;
        this.rcId = resource;
        this.SongList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView==null)
        {
            LayoutInflater layoutInflater = ((Activity) this.context).getLayoutInflater();
            convertView = layoutInflater.inflate(this.rcId, parent, false);
            holder = new ViewHolder(convertView,this.context);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Song song = getItem(position);
        holder.tvSongTitle.setText(song.getTitle());
        holder.tvSongSinger.setText(song.getSinger());

        holder.ivShareSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ShowDialog(song.getDetailUrl());
            }
        });

        return convertView;
    }

    static class ViewHolder{
        private Context mContext;
        private TextView tvSongTitle;
        private TextView tvSongSinger;
        public static ImageView ivShareSong;

        public ViewHolder(View convertView, Context context) {
            mContext = context;
            tvSongTitle = (TextView) convertView.findViewById(R.id.tvSongTitle);
            tvSongSinger = (TextView) convertView.findViewById(R.id.tvSongSinger);
            ivShareSong = (ImageView) convertView.findViewById(R.id.iv_ShareSong);

        }

        public void ShowDialog(String musicLink){
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(musicLink))
                    .build();
            ShareDialog.show((Activity) mContext, shareLinkContent);
        }
    }
}
