package com.example.shopserverfoadmin.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopserverfoadmin.Common.Common;
import com.example.shopserverfoadmin.Interface.ItemClickListener;
import com.example.shopserverfoadmin.R;


public class AccountViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener
        ,View.OnCreateContextMenuListener
{

    public TextView txtUserName, txtUserPhone, txtUserEmail, txtUserAdmin;

    private ItemClickListener itemClickListener;

    public AccountViewHolder(@NonNull View itemView) {
        super(itemView);

        txtUserName = (TextView) itemView.findViewById(R.id.user_name);
        txtUserPhone = (TextView) itemView.findViewById(R.id.user_phone);
        txtUserEmail = (TextView) itemView.findViewById(R.id.user_email);
        txtUserAdmin = (TextView) itemView.findViewById(R.id.user_admin);


        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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
