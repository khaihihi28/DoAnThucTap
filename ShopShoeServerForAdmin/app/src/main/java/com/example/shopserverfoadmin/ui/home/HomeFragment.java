package com.example.shopserverfoadmin.ui.home;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopserverfoadmin.Activity.ShoeList;
import com.example.shopserverfoadmin.Common.Common;
import com.example.shopserverfoadmin.Helper.AuthHelper;
import com.example.shopserverfoadmin.Home;
import com.example.shopserverfoadmin.Interface.ItemClickListener;
import com.example.shopserverfoadmin.MainActivity;
import com.example.shopserverfoadmin.R;
import com.example.shopserverfoadmin.ViewHolder.MenuViewHolder;
import com.example.shopserverfoadmin.databinding.FragmentHomeBinding;
import com.example.shopserverfoadmin.model.Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {

    EditText edtNameCategory;
    Button btnSelect, btnAddOrUpdate;

    AlertDialog alertDialog;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    List<Category> mlist;


    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;

    MenuViewHolder viewHolder;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    Category newCategory;

    Uri saveUri;

    private FragmentHomeBinding binding;
    FloatingActionButton fab;

    private final int PICK_IMAGE_REQUEST = 71;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        //init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Load menu
        recycler_menu = (RecyclerView) binding.recyclerMenu;
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recycler_menu.setLayoutManager(layoutManager);

        fab = (FloatingActionButton) binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddCategory();
            }
        });

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

    private void showUpdateDialog(String key, Category item){
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Update brand");
        alertDialog.setMessage("Hãy điền đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_brand_layout = inflater.inflate(R.layout.add_new_category, null);


        edtNameCategory = add_brand_layout.findViewById(R.id.edtNameCategory);
        btnSelect = add_brand_layout.findViewById(R.id.btnSelect);
        btnAddOrUpdate = add_brand_layout.findViewById(R.id.btnAdd);
        btnAddOrUpdate.setText("Update");

        //set default values
        edtNameCategory.setText(item.getName());


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnAddOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(item, key);

            }
        });



        alertDialog.setView(add_brand_layout);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.show();
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            saveUri = data.getData();
            btnSelect.setText("Đã thêm !");
        }
    }

    private void updateData(final Category itemCategory, String key){
        if(saveUri != null){
            ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Đang tải ảnh lên...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "Tải lên thành công !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    itemCategory.setImage(uri.toString());
                                    itemCategory.setName(edtNameCategory.getText().toString());
                                    category.child(key).setValue(itemCategory);
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }

    private void showDialogDelete(String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa brand ?");
        builder.setMessage("Bạn có chắc muốn xóa không ?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            category.child(key).removeValue();
            Toast.makeText(getContext(), "Xoá brand thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            // Đóng dialog nếu người dùng chọn "No"
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialogAddCategory(){
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Thêm brand mới");
        alertDialog.setMessage("Hãy điền đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_brand_layout = inflater.inflate(R.layout.add_new_category, null);

        edtNameCategory = add_brand_layout.findViewById(R.id.edtNameCategory);
        btnSelect = add_brand_layout.findViewById(R.id.btnSelect);
        btnAddOrUpdate = add_brand_layout.findViewById(R.id.btnAdd);
        btnAddOrUpdate.setText("Thêm mới");

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnAddOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });



        alertDialog.setView(add_brand_layout);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.show();
    }

    private void addImage(){
        if(saveUri != null){
            ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Đang tải ảnh lên...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "Tải lên thành công !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newCategory = new Category(edtNameCategory.getText().toString(), uri.toString());
                                    if(newCategory != null){
                                        category.push().setValue(newCategory);
                                        Toast.makeText(getContext(), "Đã thêm brand mới !", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }
}