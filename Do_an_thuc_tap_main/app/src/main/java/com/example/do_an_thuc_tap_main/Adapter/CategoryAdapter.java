package com.example.do_an_thuc_tap_main.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.do_an_thuc_tap_main.Interface.ItemClickListener;
import com.example.do_an_thuc_tap_main.Model.Category;
import com.example.do_an_thuc_tap_main.R;
import com.example.do_an_thuc_tap_main.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CategoryAdapter extends FirebaseRecyclerAdapter<Category, MenuViewHolder> {
    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
        // Đặt dữ liệu vào ViewHolder
        holder.txtMenuName.setText(model.getName());
        Glide.with(holder.itemView.getContext())
                .load(model.getImage())
                .into(holder.imageView);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Toast.makeText(holder.itemView.getContext(), ""+ model.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo ViewHolder mới
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }
}
