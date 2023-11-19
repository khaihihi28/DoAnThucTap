package com.example.do_an_thuc_tap_main.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_an_thuc_tap_main.Activity.ShoeList;
import com.example.do_an_thuc_tap_main.Interface.ItemClickListener;
import com.example.do_an_thuc_tap_main.Model.Category;
import com.example.do_an_thuc_tap_main.R;
import com.example.do_an_thuc_tap_main.ViewHolder.MenuViewHolder;
import com.example.do_an_thuc_tap_main.databinding.FragmentHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    List<Category> mlist;

    FirebaseDatabase database;
    DatabaseReference category;

    MenuViewHolder viewHolder;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        //init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Load menu
        recycler_menu = (RecyclerView) binding.recyclerMenu;
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();

        return root;
    }

    private void loadMenu(){
        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(category, Category.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                        // Đặt dữ liệu vào ViewHolder
                        holder.txtMenuName.setText(model.getName());
                        Glide.with(holder.itemView.getContext()).load(model.getImage()).into(holder.imageView);
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getActivity(), ShoeList.class);
                                intent.putExtra("BrandId", adapter.getRef(position).getKey());
                                startActivity(intent);
                            }
                        });
                        // Load hình ảnh (sử dụng thư viện Glide, Picasso, ...)
                        // Glide.with(holder.itemView.getContext()).load(model.getImage()).into(holder.imageView);
                    }

                    @NonNull
                    @Override
                    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Tạo ViewHolder mới
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
                        return new MenuViewHolder(view);
                    }
                };

        // Đặt adapter cho RecyclerView
        recycler_menu.setAdapter(adapter);

        // Khi Activity hoặc Fragment được tạo, bắt đầu lắng nghe sự thay đổi trong Realtime Database
        adapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}