package com.example.shopserverfoadmin.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shopserverfoadmin.Common.Common;
import com.example.shopserverfoadmin.Home;
import com.example.shopserverfoadmin.Interface.ItemClickListener;
import com.example.shopserverfoadmin.R;
import com.example.shopserverfoadmin.ViewHolder.ShoeViewHolder;
import com.example.shopserverfoadmin.model.Category;
import com.example.shopserverfoadmin.model.Shoe;
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

import java.util.UUID;

public class ShoeList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference shoeList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String brandId = "";

    FirebaseRecyclerAdapter<Shoe, ShoeViewHolder> adapter;

    FloatingActionButton fab;

    EditText edtNameShoe, edtDescription, edtPrice, edtDiscount;
    Button btnSelect, btnAdd;
    Shoe newShoe;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_list);

        database = FirebaseDatabase.getInstance();
        shoeList = database.getReference("Shoe");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fab = (FloatingActionButton) findViewById(R.id.fab);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddShoe();
            }
        });
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
//                                Intent shoeDetail = new Intent(ShoeList.this, ShoeDetail.class);
//                                shoeDetail.putExtra("ShoeId", adapter.getRef(position).getKey());
//                                startActivity(shoeDetail);
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

    private void showDialogAddShoe(){
        alertDialog = new AlertDialog.Builder(ShoeList.this).create();
        alertDialog.setTitle("Thêm giày mới");
        alertDialog.setMessage("Hãy điền đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_brand_layout = inflater.inflate(R.layout.add_new_shoe, null);
        //edtNameShoe, edtDescription, edtPrice, edtDiscount;
        edtNameShoe = add_brand_layout.findViewById(R.id.edtNameShoe);
        edtDescription = add_brand_layout.findViewById(R.id.edtDescription);
        edtPrice = add_brand_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_brand_layout.findViewById(R.id.edtDiscount);
        btnSelect = add_brand_layout.findViewById(R.id.btnSelect);
        btnAdd = add_brand_layout.findViewById(R.id.btnAdd);
        btnAdd.setText("Thêm mới");

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });



        alertDialog.setView(add_brand_layout);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            saveUri = data.getData();
            btnSelect.setText("Đã thêm !");
        }
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void addImage(){
        if(saveUri != null){
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang tải ảnh lên...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(ShoeList.this, "Tải lên thành công !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //String name, String image, String description, String price, String discount, String brand
                                    newShoe = new Shoe(edtNameShoe.getText().toString(), uri.toString(), edtDescription.getText().toString(),
                                            edtPrice.getText().toString(), edtDiscount.getText().toString(), brandId);
                                    if(newShoe != null){
                                        shoeList.push().setValue(newShoe);
                                        Toast.makeText(ShoeList.this, "Đã thêm giày mới !", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ShoeList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showUpdateDialog(String key, Shoe item){
        alertDialog = new AlertDialog.Builder(ShoeList.this).create();
        alertDialog.setTitle("Update giày");
        alertDialog.setMessage("Hãy điền đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_brand_layout = inflater.inflate(R.layout.add_new_shoe, null);
        //edtNameShoe, edtDescription, edtPrice, edtDiscount;
        edtNameShoe = add_brand_layout.findViewById(R.id.edtNameShoe);
        edtDescription = add_brand_layout.findViewById(R.id.edtDescription);
        edtPrice = add_brand_layout.findViewById(R.id.edtPrice);
        edtDiscount = add_brand_layout.findViewById(R.id.edtDiscount);
        btnSelect = add_brand_layout.findViewById(R.id.btnSelect);
        btnAdd = add_brand_layout.findViewById(R.id.btnAdd);
        btnAdd.setText("Update");

        //set default values
        edtNameShoe.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(item, key);
            }
        });
        alertDialog.setView(add_brand_layout);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.show();
    }
    private void updateData(final Shoe itemShoe, String key){
        if(saveUri != null){
            ProgressDialog mDialog = new ProgressDialog(ShoeList.this);
            mDialog.setMessage("Đang tải ảnh lên...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("imagesShoe/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(ShoeList.this, "Tải lên thành công !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    itemShoe.setImage(uri.toString());
                                    itemShoe.setName(edtNameShoe.getText().toString());
                                    itemShoe.setDescription(edtDescription.getText().toString());
                                    itemShoe.setPrice(edtPrice.getText().toString());
                                    itemShoe.setDiscount(edtDiscount.getText().toString());
                                    shoeList.child(key).setValue(itemShoe);
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ShoeList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ShoeList.this);
        builder.setTitle("Xóa giày ?");
        builder.setMessage("Bạn có chắc muốn xóa không ?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            shoeList.child(key).removeValue();
            Toast.makeText(ShoeList.this, "Xoá brand thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            // Đóng dialog nếu người dùng chọn "No"
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}