package com.example.do_an_thuc_tap_main.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.do_an_thuc_tap_main.Database.Database;
import com.example.do_an_thuc_tap_main.Helper.FireBaseHelper;
import com.example.do_an_thuc_tap_main.Model.Order;
import com.example.do_an_thuc_tap_main.Model.Shoe;
import com.example.do_an_thuc_tap_main.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShoeDetail extends AppCompatActivity {

    private TextView quantityTextView;
    private int quantity = 1;
    private final int MAX_QUANTITY = 99;

    TextView shoe_name, shoe_price, shoe_description;
    ImageView shoe_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;

    String shoeId = "";

    FirebaseDatabase database;
    DatabaseReference shoe;

    Shoe currentShoe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_detail);

        quantityTextView = findViewById(R.id.quantityTextView);
        updateQuantityText();

        //firebase
        database = FirebaseDatabase.getInstance();
        shoe = database.getReference("Shoe");

        //init view
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        shoeId,
                        currentShoe.getName(),
                        quantityTextView.getText().toString(),
                        currentShoe.getPrice(),
                        currentShoe.getDiscount(),
                        FireBaseHelper.getCurrentUserUid().toString()
                ));

                Toast.makeText(ShoeDetail.this, "Đã thêm vào giỏ hàng!!!", Toast.LENGTH_SHORT).show();
            }
        });

        shoe_description = (TextView) findViewById(R.id.shoe_decription);
        shoe_name = (TextView) findViewById(R.id.shoe_name);
        shoe_price = (TextView) findViewById(R.id.shoe_price);
        shoe_img = (ImageView) findViewById(R.id.img_shoe);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        if(getIntent() != null){
            shoeId = getIntent().getStringExtra("ShoeId");
        }
        if(!shoeId.isEmpty()){
            getDetailShoe(shoeId);
        }

    }

    private void getDetailShoe(String shoeId){
        shoe.child(shoeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentShoe = snapshot.getValue(Shoe.class);

                Glide.with(getBaseContext()).load(currentShoe.getImage()).into(shoe_img);
                collapsingToolbarLayout.setTitle(currentShoe.getName());
                shoe_price.setText(currentShoe.getPrice());
                shoe_name.setText(currentShoe.getName());
                shoe_description.setText(currentShoe.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void increaseQuantity(View view) {
        if (quantity < MAX_QUANTITY) {
            quantity++;
        } else {
            quantity = 1;
        }
        updateQuantityText();
    }

    public void decreaseQuantity(View view) {
        if (quantity > 1) {
            quantity--;
            updateQuantityText();
        }
    }

    private void updateQuantityText() {
        quantityTextView.setText(String.valueOf(quantity));
    }
}