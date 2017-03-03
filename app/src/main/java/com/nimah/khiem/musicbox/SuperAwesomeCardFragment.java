package com.nimah.khiem.musicbox;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Khiem on 8/13/2016.
 */
public class SuperAwesomeCardFragment extends Fragment{


    private static ArrayList<Album> AlbumList;
    Context context;
    private static AlbumItemAdapter albumItemAdapter;
    private static GridView gvAlbum;
    String categoryLink;

    public void setParams(Context context, String categoryLink){
        this.context = context;
        this.categoryLink = categoryLink;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card,container,false);
        gvAlbum = (GridView) rootView.findViewById(R.id.gvAlbum);
        new GetAlbumTask(context).execute(categoryLink);
        return rootView;
    }

    public static void setUpGridView(Context context, ArrayList<Album> albumList){
        AlbumList = albumList;
        albumItemAdapter = new AlbumItemAdapter(context, R.layout.album_item_layout, AlbumList);
        gvAlbum.setAdapter(albumItemAdapter);
    }
}
