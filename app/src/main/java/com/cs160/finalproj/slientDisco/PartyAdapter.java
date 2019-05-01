package com.cs160.finalproj.slientDisco;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PartyAdapter extends RecyclerView.Adapter<PartyAdapter.PartyViewHolder>  {

    private ArrayList<PartyContainer> mPartyList;

    public onItemClickedListener mListener;
    public interface onItemClickedListener {
        void onItemClick(int position);
    }
    public void setOnItemClickedListener(onItemClickedListener listener) {
        mListener = listener;
    }


    public static class PartyViewHolder extends RecyclerView.ViewHolder {
        // declare instance vars
        //public ImageView mImageView;
        public TextView mPartyName;
        public TextView mPartyInfo;

        public PartyViewHolder(View itemView, final onItemClickedListener listener) {
            super(itemView);
            // set instance vars from view
            mPartyName = itemView.findViewById(R.id.party_item_party_name);
            mPartyInfo = itemView.findViewById(R.id.party_item_party_info);
            // mImageView = itemView.findViewById(R.id.imageView);
            // mTextView1 = itemView.findViewById(R.id.textView);
            // mTextView2 = itemView.findViewById(R.id.textView2);

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

    public PartyAdapter(ArrayList<PartyContainer> partyList) {
        mPartyList = partyList;
    }

    @Override
    public PartyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.party_item, parent, false);
        PartyViewHolder pvh = new PartyViewHolder(v, mListener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PartyViewHolder holder, int position) {
        PartyContainer currentParty = mPartyList.get(position);
        // attach instance vars to holder
        holder.mPartyName.setText(currentParty.getPartyName());
        String numPeople = Integer.toString(currentParty.getNumPeople());
        String genre = currentParty.getGenre();
        String info = numPeople + " peeps, " + genre;
        holder.mPartyInfo.setText(info);
    }

    @Override
    public int getItemCount() {
        return mPartyList.size();
    }
}


