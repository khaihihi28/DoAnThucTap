package com.example.shopserverfoadmin.ui.account;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopserverfoadmin.Activity.ShoeList;
import com.example.shopserverfoadmin.Common.Common;
import com.example.shopserverfoadmin.Helper.AuthHelper;
import com.example.shopserverfoadmin.Helper.FireBaseHelper;
import com.example.shopserverfoadmin.R;
import com.example.shopserverfoadmin.ViewHolder.AccountViewHolder;
import com.example.shopserverfoadmin.ViewHolder.OrderViewHolder;
import com.example.shopserverfoadmin.databinding.FragmentAccountBinding;
import com.example.shopserverfoadmin.model.Requetst;
import com.example.shopserverfoadmin.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference users;

    FirebaseRecyclerAdapter<User, AccountViewHolder> adapter;

    Spinner spinner;

    EditText edtEmail, edtPass,edtUserName, edtPhone;
    Button  btnSave;

    String localKey = "";

    AlertDialog alertDialog;

    FloatingActionButton fab;



    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        if(getActivity().getIntent() != null){
            localKey = getActivity().getIntent().getStringExtra("localKey");
        }

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Load data
        recyclerView = (RecyclerView) binding.recyclerAccount;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        fab = (FloatingActionButton) binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddAccount();
                adapter.notifyDataSetChanged();
            }
        });

        loadAccount();

        return root;
    }
    private void loadAccount(){
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(users, User.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<User, AccountViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AccountViewHolder holder, int position, @NonNull User model) {
                // Đặt dữ liệu vào ViewHolder
                holder.txtUserName.setText(model.getName());
                holder.txtUserPhone.setText(model.getPhone());
                holder.txtUserEmail.setText(model.getEmail());
                holder.txtUserAdmin.setText(model.getEmail());
                holder.txtUserAdmin.setText(convertCodeAdmin(model.isStaff()));
                holder.txtUserAdmin.setTextColor(Color.parseColor(convertColorToAdmin(model.isStaff())));
            }

            @NonNull
            @Override
            public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_layout, parent, false);
                return new AccountViewHolder(view);
            }
        };
        // Đặt adapter cho RecyclerView
        recyclerView.setAdapter(adapter);

        // Khi Activity hoặc Fragment được tạo, bắt đầu lắng nghe sự thay đổi trong Realtime Database
        adapter.startListening();
    }

    private String convertCodeAdmin(boolean isAdmin){
        if(isAdmin == true){
            return "Amin";
        }
        else{
            return "User";
        }
    }
    private String convertCodeNameAdmin(String uidNow, User model){
        if(uidNow.equals(FireBaseHelper.getCurrentUserUid().toString())){
            return model.getName() + " (You)";
        }
        else{
            return model.getName();
        }
    }
    private String convertColorToAdmin(boolean isAdmin){
        if(isAdmin == true){
            return "#FFFF0000";
        }
        else{
            return "#FF0022FF";
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

    private void showUpdateDialog(String key, User item){
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Update acount");
        alertDialog.setMessage("Điền đủ thông tin: ");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_account_layout, null);

        edtUserName = view.findViewById(R.id.edtUserName);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnSave = view.findViewById(R.id.btnSaveAccount);

        //set default values
        edtUserName.setText(item.getName());
        edtPhone.setText(item.getPhone());

        spinner = view.findViewById(R.id.adminSpinner);
        List<String> listStatus = new ArrayList<>();
        listStatus.add(Common.ADMIN);
        listStatus.add(Common.USER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listStatus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        alertDialog.setView(view);
        final String oldKey = key;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setName(edtUserName.getText().toString());
                item.setPhone(edtPhone.getText().toString());
                if(oldKey.equals(localKey)){
                    Toast.makeText(getContext(), "Bạn không thể tự sử quyền cho chính mình!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    item.setStaff(checkAdmin(spinner.getSelectedItemPosition()));
                }
                users.child(oldKey).setValue(item);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private boolean checkAdmin(int index){
        if(index == 0){
            return true;
        }
        else return false;
    }

    private void showDialogDelete(String key){


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa User ?");
        builder.setMessage("Bạn có chắc muốn xóa không ?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            if(localKey.equals(key)){
                Toast.makeText(getContext(), "Bạn không thể xóa chính mình!!!", Toast.LENGTH_SHORT).show();
            }
            else {
                deleteAuthUser(key);
                users.child(key).removeValue();
                Toast.makeText(getContext(), "Xoá user thành công!", Toast.LENGTH_SHORT).show();
                AuthHelper.loginAo(localKey, new AuthHelper.OnLoginCompleteListener() {
                    @Override
                    public void onLoginSuccess(String uid) {
                        
                    }

                    @Override
                    public void onLoginFailure(String errorMessage) {

                    }
                });
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            // Đóng dialog nếu người dùng chọn "No"
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteAuthUser(String key){
        AuthHelper.loginAo(key, new AuthHelper.OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(String uid) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();
            }
            @Override
            public void onLoginFailure(String errorMessage) {

            }
        });
    }

    private void showDialogAddAccount(){
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Thêm user mới");
        alertDialog.setMessage("Hãy điền đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_account_layout, null);

        edtEmail = view.findViewById(R.id.edtAddEmail);
        edtPass = view.findViewById(R.id.edtAddPass);
        edtUserName = view.findViewById(R.id.edtUserName);
        edtPhone = view.findViewById(R.id.edtPhone);
        btnSave = view.findViewById(R.id.btnSaveAccount);

        spinner = view.findViewById(R.id.adminSpinner);
        List<String> listStatus = new ArrayList<>();
        listStatus.add(Common.ADMIN);
        listStatus.add(Common.USER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listStatus);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        alertDialog.setView(view);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });



        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_info);
        alertDialog.show();
    }

    private void addUser(){
        AuthHelper.signUpHelper(edtEmail.getText().toString()
                , edtPass.getText().toString(), edtUserName.getText().toString()
                , edtPhone.getText().toString(), edtPass.getText().toString(), new AuthHelper.OnRegistrationCompleteListener() {
                    @Override
                    public void onRegistrationSuccess(String uid) {
                        Toast.makeText(getContext(), "Tạo user mới thành công !!!", Toast.LENGTH_SHORT).show();
                        AuthHelper.loginAo(localKey, new AuthHelper.OnLoginCompleteListener() {
                            @Override
                            public void onLoginSuccess(String uid) {
                                //login lại tk admin
                            }

                            @Override
                            public void onLoginFailure(String errorMessage) {
                                Toast.makeText(getContext(), "Tài khoản đã tồn tại!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onRegistrationFailure(String errorMessage) {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}