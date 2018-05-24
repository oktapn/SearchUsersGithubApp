package com.example.okta.githubusersearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.okta.githubusersearch.R;
import com.example.okta.githubusersearch.model.Item;

import java.util.List;

public class RecycleviewAdapterSearch extends RecyclerView.Adapter<RecycleviewAdapterSearch.ViewHolder> {

    private List<Item> items;

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_users, parent, false);
        RecycleviewAdapterSearch.ViewHolder viewHolder = new RecycleviewAdapterSearch.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        String imageurl = items.get(position).getAvatarUrl();
        String username = items.get(position).getLogin();

        Glide.with(context)
                .load(imageurl)
                .placeholder(android.R.color.darker_gray)
                .into(holder.imageView);
        holder.textView.setText(username);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.IVItem);
            textView = itemView.findViewById(R.id.TVDesc);
        }
    }
}
