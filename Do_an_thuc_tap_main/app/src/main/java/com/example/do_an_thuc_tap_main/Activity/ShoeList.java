package com.example.do_an_thuc_tap_main.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.do_an_thuc_tap_main.Interface.ItemClickListener;
import com.example.do_an_thuc_tap_main.Model.Shoe;
import com.example.do_an_thuc_tap_main.R;
import com.example.do_an_thuc_tap_main.ViewHolder.MenuViewHolder;
import com.example.do_an_thuc_tap_main.ViewHolder.ShoeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShoeList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference shoeList;

    String brandId = "";

    FirebaseRecyclerAdapter<Shoe, ShoeViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_list);

        database = FirebaseDatabase.getInstance();
        shoeList = database.getReference("Shoe");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_shoe);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent() != null){
            brandId = getIntent().getStringExtra("BrandId");
        }
        if(!brandId.isEmpty() && brandId != null){
            loadListShoe(brandId);
        }


    }
    private void loadListShoe(String brandId){
        FirebaseRecyclerOptions<Shoe> options =
                new FirebaseRecyclerOptions.Builder<Shoe>()
                        .setQuery(shoeList.orderByChild("brand").equalTo(brandId), Shoe.class)
                        .build();
        adapter =
                new FirebaseRecyclerAdapter<Shoe, ShoeViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ShoeViewHolder holder, int position, @NonNull Shoe model) {
                        holder.shoe_name.setText(model.getName());
                        Glide.with(holder.itemView.getContext()).load(model.getImage()).into(holder.shoe_img);

                        // Xử lý sự kiện khi người dùng click vào item giày
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent shoeDetail = new Intent(ShoeList.this, ShoeDetail.class);
                                shoeDetail.putExtra("ShoeId", adapter.getRef(position).getKey());
                                startActivity(shoeDetail);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ShoeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Tạo ViewHolder mới
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoe_item, parent, false);
                        return new ShoeViewHolder(view);
                    }
                };

        // Đặt adapter cho RecyclerView
        recyclerView.setAdapter(adapter);

        // Khi Activity hoặc Fragment được tạo, bắt đầu lắng nghe sự thay đổi trong Realtime Database
        adapter.startListening();
    }
}