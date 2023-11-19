package com.example.do_an_thuc_tap_main.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an_thuc_tap_main.Interface.ItemClickListener;
import com.example.do_an_thuc_tap_main.R;

public class ShoeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
