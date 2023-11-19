package com.example.do_an_thuc_tap_main.ui.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.do_an_thuc_tap_main.Helper.FireBaseHelper;
import com.example.do_an_thuc_tap_main.Model.User;
import com.example.do_an_thuc_tap_main.databinding.FragmentInfoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoFragment extends Fragment {

    EditText edtName, edtEmail, edtPhone;

    ImageView editName, editEmail, editPhone;
    Button btnSaveInfo;

    private FragmentInfoBinding binding;
    User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InfoViewModel infoViewModel =
                new ViewModelProvider(this).get(InfoViewModel.class);

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        edtName = binding.edtName;
        edtEmail = binding.edtEmail;
        edtPhone = binding.edtPhone;
        editName = binding.editName;
        editEmail = binding.editEmail;
        editPhone = binding.editPhone;
        btnSaveInfo = binding.btnSaveInfo;

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(FireBaseHelper.getCurrentUserUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy dữ liệu từ snapshot và cập nhật giao diện
                    user = snapshot.getValue(User.class);

                    // Hiển thị thông tin trên giao diện
                    displayUserInfo(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtName.setEnabled(true);
            }
        });
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtEmail.setEnabled(true);
            }
        });

        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPhone.setEnabled(true);
            }
        });

        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the updated values from EditText fields
                String updatedName = edtName.getText().toString().trim();
                String updatedEmail = edtEmail.getText().toString().trim();
                String updatedPhone = edtPhone.getText().toString().trim();

                // Update the user object with new values
                user.setName(updatedName);
                user.setEmail(updatedEmail);
                user.setPhone(updatedPhone);

                // Save the updated user information to the database
                userRef.setValue(user);
                Toast.makeText(getContext(), "Lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                // Disable EditText fields after saving
                edtName.setEnabled(false);
                edtPhone.setEnabled(false);
                editEmail.setEnabled(false);
            }
        });

        return root;
    }
    private void displayUserInfo(User user) {
        // Kiểm tra xem user có tồn tại không
        if (user != null) {
            // Hiển thị thông tin trên giao diện
            edtName.setText(user.getName());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
        } else {
            // Xử lý trường hợp user không tồn tại
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}