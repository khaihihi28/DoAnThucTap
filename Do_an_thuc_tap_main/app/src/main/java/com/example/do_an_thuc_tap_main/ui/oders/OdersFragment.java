package com.example.do_an_thuc_tap_main.ui.oders;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_an_thuc_tap_main.Helper.FireBaseHelper;
import com.example.do_an_thuc_tap_main.Model.Requetst;
import com.example.do_an_thuc_tap_main.R;
import com.example.do_an_thuc_tap_main.ViewHolder.MenuViewHolder;
import com.example.do_an_thuc_tap_main.ViewHolder.OrderViewHolder;
import com.example.do_an_thuc_tap_main.databinding.FragmentOdersBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OdersFragment extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;


    FirebaseRecyclerAdapter<Requetst, OrderViewHolder> adapter;

    private FragmentOdersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OdersViewModel odersViewModel =
                new ViewModelProvider(this).get(OdersViewModel.class);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        binding = FragmentOdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Load data
        recyclerView = (RecyclerView) binding.listOrder;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(FireBaseHelper.getCurrentUserUid());


        return root;
    }

    private void loadOrders(String Uid){
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Requetst>()
                .setQuery(requests.orderByChild("uid").equalTo(Uid), Requetst.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Requetst, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Requetst model) {
                // Đặt dữ liệu vào ViewHolder
                holder.txtOrderId.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                holder.txtOrderStatus.setTextColor(Color.parseColor(convertColorToStatus(model.getStatus())));
                holder.txtOrderPhone.setText(model.getPhone());
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderName.setText(model.getName());
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(view);
            }
        };
        // Đặt adapter cho RecyclerView
        recyclerView.setAdapter(adapter);

        // Khi Activity hoặc Fragment được tạo, bắt đầu lắng nghe sự thay đổi trong Realtime Database
        adapter.startListening();
    }
    private String convertCodeToStatus(String status){
        if(status.equals("0")){
            return "Đợi đóng hàng...";
        }
        else if(status.equals("1")){
            return "Đang vận chuyển...";
        }
        else if(status.equals("2")){
            return "Đang giao hàng...";
        }
        else if(status.equals("3")){
            return "Đã giao hàng thành công!";
        }
        else{
            return "Đơn hàng thất bại!";
        }
    }
    private String convertColorToStatus(String status){
        if(status.equals("0")){
            return "#FF000000";
        }
        else if(status.equals("1")){
            return "#292929";
        }
        else if(status.equals("2")){
            return "#0000FF";
        }
        else if(status.equals("3")){
            return "#00FF00";
        }
        else{
            return "#FF0000";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}