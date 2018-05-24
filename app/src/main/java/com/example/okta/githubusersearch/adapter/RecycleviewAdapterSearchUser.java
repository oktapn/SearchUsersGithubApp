package com.example.okta.githubusersearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.okta.githubusersearch.R;
import com.example.okta.githubusersearch.model.Item;

import java.util.List;

public class RecycleviewAdapterSearchUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int VIEW_ITEM = 1;
    public final int VIEW_PROG = 0;

    private List<Item> items;

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_users, parent, false);
            vh = new TextViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecycleviewAdapterSearchUser.TextViewHolder) {
            Context context = holder.itemView.getContext();
            String imageurl = items.get(position).getAvatarUrl();
            String username = items.get(position).getLogin();

            Glide.with(context)
                    .load(imageurl)
                    .placeholder(android.R.color.darker_gray)
                    .into(((TextViewHolder) holder).imageView);
            ((TextViewHolder) holder).textView.setText(username);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void addItem(final Item item, final int position) {
        items.add(position, item);
        notifyItemChanged(position);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public TextViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.IVItem);
            textView = itemView.findViewById(R.id.TVDesc);
        }
    }
}
