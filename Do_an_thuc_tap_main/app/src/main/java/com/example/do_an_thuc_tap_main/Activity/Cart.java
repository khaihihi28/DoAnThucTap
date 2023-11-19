package com.example.do_an_thuc_tap_main.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.do_an_thuc_tap_main.Database.Database;
import com.example.do_an_thuc_tap_main.Helper.FireBaseHelper;
import com.example.do_an_thuc_tap_main.Model.Order;
import com.example.do_an_thuc_tap_main.Model.Requetst;
import com.example.do_an_thuc_tap_main.Model.User;
import com.example.do_an_thuc_tap_main.R;
import com.example.do_an_thuc_tap_main.ViewHolder.CartAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button  btnPlace;

    List<Order> carts = new ArrayList<>();

    CartAdapter adapter;

    User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (Button) findViewById(R.id.btnPlaceOrder);

        loadListShoe(FireBaseHelper.getCurrentUserUid());

        adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showOptionsDialog(position);
            }
        });



        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAltertDialog();

            }
        });

    }

    private void showAltertDialog(){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("User").child(FireBaseHelper.getCurrentUserUid());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Đối tượng User có thể được chuyển đổi từ dataSnapshot
                    user = dataSnapshot.getValue(User.class);
                } else {
                    // Nếu nút với UID không tồn tại
                    System.out.println("User does not exist.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Bước cuối cùng!!!");
        alertDialog.setMessage("Nhập địa chỉ của bạn để shop có thể gưi sản phẩm đến cho bạn nhé");

        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Requetst requetst = new Requetst(user.getPhone(), user.getName(),edtAddress.getText().toString(), txtTotalPrice.getText().toString(),carts, FireBaseHelper.getCurrentUserUid(), "0");
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(requetst);
                //Delete cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Cảm ơn bạn đã tin tưởng, đơn hàng của bạn đã được gửi!!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();


    }

    private void loadListShoe(String uid){
        carts = new Database(this).getCarts(uid);
        if(carts.size() > 0){
            adapter = new CartAdapter(carts, this);
            recyclerView.setAdapter(adapter);
            totalCarts();
        }

    }
    private void totalCarts(){
        int total = 0;
        for (Order order : carts){
            total+=(Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }
    private void showOptionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn tùy chọn");
        builder.setItems(new CharSequence[]{"Xóa"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        carts.remove(position);
                        adapter.notifyItemRemoved(position);
                        totalCarts();
                        break;
                }
            }
        });
        builder.create().show();
    }
}