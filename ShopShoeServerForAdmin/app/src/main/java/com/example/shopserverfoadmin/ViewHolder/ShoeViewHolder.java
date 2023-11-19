package com.example.shopserverfoadmin.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopserverfoadmin.Common.Common;
import com.example.shopserverfoadmin.Interface.ItemClickListener;
import com.example.shopserverfoadmin.R;


public class ShoeViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener
        ,View.OnCreateContextMenuListener

{

    public TextView shoe_name;
    public ImageView shoe_img;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ShoeViewHolder(@NonNull View itemView) {
        super(itemView);
        shoe_name = (TextView) itemView.findViewById(R.id.shoe_name);
        shoe_img = (ImageView) itemView.findViewById(R.id.shoe_img);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }



    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Chọn option ?");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
