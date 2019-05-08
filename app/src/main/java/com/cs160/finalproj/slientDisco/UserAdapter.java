package com.cs160.finalproj.slientDisco;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>  {

    private ArrayList<String> mUserList;

    public onItemClickedListener mListener;
    public interface onItemClickedListener {
        void onItemClick(int position);
    }
    public void setOnItemClickedListener(onItemClickedListener listener) {
        mListener = listener;
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        // declare instance vars
        //public ImageView mImageView;
        public TextView mUserName;
        //public TextView mPartyInfo;

        public UserViewHolder(View itemView, final onItemClickedListener listener) {
            super(itemView);
            // set instance vars from view
            mUserName = itemView.findViewById(R.id.name_item_name);
            //mPartyInfo = itemView.findViewById(R.id.party_item_party_info);
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

    public UserAdapter(ArrayList<String> userList) {
        mUserList = userList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_item, parent, false);
        UserViewHolder uvh = new UserViewHolder(v, mListener);
        return uvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        String currentUser = mUserList.get(position);
        // attach instance vars to holder
        holder.mUserName.setText(currentUser);
        //String numPeople = Integer.toString(currentParty.getNumPeople());
        //String genre = currentParty.getGenre();
        //String info = numPeople + " peeps, " + genre;
        //holder.mPartyInfo.setText(info);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void update(ArrayList<String> newUsers) {
        mUserList.clear();
        mUserList.addAll(newUsers);
        this.notifyDataSetChanged();
    }

}


