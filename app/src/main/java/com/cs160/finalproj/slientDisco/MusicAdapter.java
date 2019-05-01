package com.cs160.finalproj.slientDisco;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder>  {

    private ArrayList<MusicContainer> mMusicList;

    public onItemClickedListener mListener;
    public interface onItemClickedListener {
        void onItemClick(int position);
    }
    public void setOnItemClickedListener(onItemClickedListener listener) {
        mListener = listener;
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        // declare instance vars
        //public ImageView mImageView;
        public TextView mMusicName;

        public MusicViewHolder(View itemView, final onItemClickedListener listener) {
            super(itemView);
            // set instance vars from view
            mMusicName = itemView.findViewById(R.id.music_item_music_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public MusicAdapter(ArrayList<MusicContainer> musicList) {
        mMusicList = musicList;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        MusicViewHolder pvh = new MusicViewHolder(v, mListener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        MusicContainer currentMusic = mMusicList.get(position);
        // attach instance vars to holder
        holder.mMusicName.setText(currentMusic.getMusicName());
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }
}
