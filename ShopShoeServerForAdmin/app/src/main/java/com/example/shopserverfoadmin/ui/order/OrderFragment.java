package com.example.shopserverfoadmin.ui.order;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopserverfoadmin.Common.Common;
import com.example.shopserverfoadmin.Helper.FireBaseHelper;
import com.example.shopserverfoadmin.R;
import com.example.shopserverfoadmin.ViewHolder.OrderViewHolder;
import com.example.shopserverfoadmin.databinding.FragmentOrderBinding;
import com.example.shopserverfoadmin.model.Requetst;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Requetst, OrderViewHolder> adapter;

    Spinner spinner;

    private FragmentOrderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrderViewModel orderViewModel =
                new ViewModelProvider(this).get(OrderViewModel.class);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        binding = FragmentOrderBinding.inflate(inflater, container, false);
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
                        .setQuery(requests, Requetst.class)
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
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            showDialogDelete(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(String key, Requetst item){
        final AlertDialog.Builder alterDialog = new AlertDialog.Builder(getContext());
        alterDialog.setTitle("Update order");
        alterDialog.setMessage("Chọn trạng thái cho đơn hàng: ");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = (Spinner) view.findViewById(R.id.statusSpinner);
        List<String> listStatus = new ArrayList<>();
        listStatus.add(Common.WAITING);
        listStatus.add(Common.TRANSPORTING);
        listStatus.add(Common.SHIPPING);
        listStatus.add(Common.SHIPPED);
        listStatus.add(Common.SHIP_FAILD);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listStatus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        alterDialog.setView(view);
        final String localKey = key;
        alterDialog.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedItemPosition()));

                requests.child(localKey).setValue(item);
            }
        });
        alterDialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alterDialog.show();

    }
    private void showDialogDelete(String key){
        requests.child(key).removeValue();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}