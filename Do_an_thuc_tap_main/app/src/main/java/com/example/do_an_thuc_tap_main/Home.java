package com.example.do_an_thuc_tap_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.do_an_thuc_tap_main.Activity.Cart;
import com.example.do_an_thuc_tap_main.Adapter.CategoryAdapter;
import com.example.do_an_thuc_tap_main.Helper.AuthHelper;
import com.example.do_an_thuc_tap_main.Helper.FireBaseHelper;
import com.example.do_an_thuc_tap_main.Model.Category;
import com.example.do_an_thuc_tap_main.Model.User;
import com.example.do_an_thuc_tap_main.ViewHolder.MenuViewHolder;
import com.example.do_an_thuc_tap_main.databinding.FragmentOdersBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an_thuc_tap_main.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;




    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBarHome.toolbar.setTitle("Menu");
        setSupportActionBar(binding.appBarHome.toolbar);

        //init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");




        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, Cart.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_oders, R.id.nav_info, R.id.nav_log_out)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //logout
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_log_out) {
                showLogoutConfirmationDialog();
                return true;
            }

            // Xử lý các mục menu khác
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        //set Name User
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("User").child(FireBaseHelper.getCurrentUserUid());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Đối tượng User có thể được chuyển đổi từ dataSnapshot
                    User user = dataSnapshot.getValue(User.class);

                    if (user != null) {
                        // Lấy tên của người dùng
                        txtFullName.setText(user.getName());
                    }
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

    }


    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng xuất ?");
        builder.setMessage("Bạn có chắc muốn đăng xuất không ?");
        builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
            AuthHelper.signOutHelper();
            Intent intent = new Intent(Home.this, MainActivity.class);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            // Đóng dialog nếu người dùng chọn "No"
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

        @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}