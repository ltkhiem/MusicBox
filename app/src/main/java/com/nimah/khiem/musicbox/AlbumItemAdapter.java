package com.nimah.khiem.musicbox;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidviewhover.BlurLayout;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.pedrovgs.DraggableView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.nimah.khiem.musicbox.MainActivity.*;

/**
 * Created by Khiem on 8/12/2016.
 */
public class AlbumItemAdapter extends ArrayAdapter<Album> {
    private Context context;
    private int rcId;
    private ArrayList<Album> AlbumList;

    public AlbumItemAdapter(Context context, int resource, ArrayList<Album> data) {
        super(context, resource, data);
        this.context = context;
        this.rcId = resource;
        AlbumList = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) this.context).getLayoutInflater();
            convertView = layoutInflater.inflate(this.rcId, parent, false);
            holder = new ViewHolder(convertView, this.context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Album album = getItem(position);
        Picasso.with(context).load(album.getImageUrl()).into(holder.ivAlbumImg);
        holder.tvTitle.setText(album.getTitle());
        holder.tvSinger.setText(album.getSinger());

        holder.btnCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setSeletecId(position);
                new GetSongListTask(context).execute(album.getDetailUrl());
                holder.draggableView.setVisibility(View.VISIBLE);
                holder.draggableView.maximize();
                MainActivity.ivCover.performClick();
            }
        });


        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(album.getDetailUrl()))
                        .build();
                ShareDialog.show((Activity) context, shareLinkContent);
            }
        });

        return convertView;
    }

    static class ViewHolder extends Activity{
        Context mContext;
        private ImageView ivAlbumImg;
        private TextView tvTitle;
        private TextView tvSinger;
        private BlurLayout blItem;
        private ImageView btnCat;
        private ImageView ivShare;
        DraggableView draggableView = MainActivity.ShareVar.getDraggableView();
        private int seletecId;

        public ViewHolder(View convertView, Context context) {
            mContext = context;
            ivAlbumImg = (ImageView) convertView.findViewById(R.id.ivAlbumImg);

            BlurLayout.setGlobalDefaultDuration(450);
            blItem = (BlurLayout) convertView.findViewById(R.id.bl_Image);
            View hover = LayoutInflater.from(mContext).inflate(R.layout.album_item_info, null);
            if (hover != null) {
                tvTitle = (TextView) hover.findViewById(R.id.tvTitle);
                tvSinger = (TextView) hover.findViewById(R.id.tvSinger);
                btnCat = (ImageView) hover.findViewById(R.id.cat);
                ivShare = (ImageView) hover.findViewById(R.id.mail);
                tvTitle.setMovementMethod(new ScrollingMovementMethod());
                tvSinger.setMovementMethod(new ScrollingMovementMethod());

                blItem.setHoverView(hover);
                blItem.addChildAppearAnimator(hover, R.id.cat, Techniques.SlideInLeft);
                blItem.addChildAppearAnimator(hover, R.id.mail, Techniques.SlideInRight);

                blItem.addChildDisappearAnimator(hover, R.id.cat, Techniques.SlideOutLeft);
                blItem.addChildDisappearAnimator(hover, R.id.mail, Techniques.SlideOutRight);

                blItem.addChildAppearAnimator(hover, R.id.tvTitle, Techniques.BounceIn);
                blItem.addChildDisappearAnimator(hover, R.id.tvTitle, Techniques.FadeOutUp);

                blItem.addChildAppearAnimator(hover, R.id.tvSinger, Techniques.RollIn);
                blItem.addChildDisappearAnimator(hover, R.id.tvSinger, Techniques.FadeOutUp);
            }
        }

        public void setSeletecId(int seletecId) {
            MainActivity.ShareVar.setSelectedAlbumId(seletecId);
        }
    }
}
