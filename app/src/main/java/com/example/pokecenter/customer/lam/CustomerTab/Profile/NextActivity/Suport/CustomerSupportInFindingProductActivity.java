package com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.Suport;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.API.FirebaseSupportCustomer;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Model.cart.CartAdapter;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.databinding.ActivityCustomerSupportInFindingProductBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;

public class CustomerSupportInFindingProductActivity extends AppCompatActivity {

    private ActivityCustomerSupportInFindingProductBinding binding;
    private List<Uri> additionalImagesUri = new ArrayList<>();
    private ImageViewAdapter imageViewArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerSupportInFindingProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Submit Form");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        binding.supportInFinddingScreen.setOnClickListener(view -> {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        imageViewArrayAdapter = new ImageViewAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        binding.rcvAdditionalImage.setLayoutManager(gridLayoutManager);
        binding.rcvAdditionalImage.setAdapter(imageViewArrayAdapter);

        binding.addImageButton.setOnClickListener(view -> {
            ImagePicker.with(this)
                    .crop() // Crop image(Optional), Check Customization for more option
                    .galleryOnly()
                    .createIntent( intent -> {
                        someActivityResultLauncher.launch(intent);
                        return Unit.INSTANCE;
                    });
        });

        binding.submitButton.setOnClickListener(view -> {
            if (validateInfo()) {
                saveForm();
            }
        });

    }

    private List<String> stringImages = new ArrayList<>();
    private void saveForm() {

        binding.submitButton.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm");

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("findingProductImages");

        AtomicInteger count = new AtomicInteger(0);


        for(int i = 0; i < additionalImagesUri.size(); ++i) {

            Uri imageUri = additionalImagesUri.get(i);
            int finalI = i;

            mStorageRef.child(dateFormat.format(new Date())).child(String.valueOf(finalI)).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                            downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    stringImages.add(uri.toString());

                                    if (count.incrementAndGet() == additionalImagesUri.size()) {
                                        saveData();
                                    }
                                }
                            });

                            Toast.makeText(CustomerSupportInFindingProductActivity.this, "save image " + (finalI + 1) + " success", Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CustomerSupportInFindingProductActivity.this, "save image " + (finalI + 1) + " failed", Toast.LENGTH_SHORT)
                                    .show();

                            if (count.incrementAndGet() == additionalImagesUri.size()) {
                                saveData();
                            }
                        }
                    });

                }
            });
        }

    }

    public void saveData() {
        String name = binding.nameEditText.getText().toString();
        String desc = binding.descEditText.getText().toString();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isSuccess = true;
            String generatedId = "";

            try {
                generatedId = new FirebaseSupportCustomer().addNewFindingProductSupport(name, desc, stringImages);
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            String finalGeneratedId = generatedId;

            handler.post(() -> {
                if (finalIsSuccess) {

                    showDialogToInformSuccess(finalGeneratedId);

                } else {
                    Toast.makeText(CustomerSupportInFindingProductActivity.this, "Failed to connect server", Toast.LENGTH_SHORT)
                            .show();
                }

                binding.submitButton.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);

            });
        });
    }

    private void showDialogToInformSuccess(String generatedId) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lam_dialog_succesful_send_support_ticket);

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        /*
         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); khá quan trọng. THỬ BỎ ĐI SẼ HIỂU =)))
         nếu bỏ dòng này đi thì các thuộc tính của LinearLayout mẹ trong todo_dialog.xml sẽ mất hết
         thay vào đó sẽ là thuộc tính mặc định của dialog, còn nội dung vẫn giữ nguyên
         */
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView supportId = dialog.findViewById(R.id.support_id);
        supportId.setText(generatedId);

        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();

    }

    private boolean validateInfo() {
        boolean isValid = true;

        if (binding.nameEditText.getText().length() == 0) {
            binding.nameEditText.setError("You have not entered product's name.");
            isValid = false;
        }

        if (binding.descEditText.getText().length() == 0) {
            binding.descEditText.setError("You have not entered description about product.");
            isValid = false;
        }

        return isValid;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        additionalImagesUri.add(data.getData());

                        updateHeightRecyclerView();


                    }
                }
            });

    private void updateHeightRecyclerView() {

        imageViewArrayAdapter.notifyItemChanged(additionalImagesUri.size() - 1);
        ViewGroup.LayoutParams layout = binding.rcvAdditionalImage.getLayoutParams();
        layout.height = 316 * ((additionalImagesUri.size() + 2) / 3);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>{

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lam_additional_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.productImage.setImageURI(additionalImagesUri.get(position));
        }

        @Override
        public int getItemCount() {
            return additionalImagesUri.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            private ImageView productImage;
            private ImageButton removeButton;
            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);

                productImage = itemView.findViewById(R.id.product_image);
                removeButton = itemView.findViewById(R.id.remove_button);

                removeButton.setOnClickListener(view -> {
                    int position = getAbsoluteAdapterPosition();
                    additionalImagesUri.remove(position);

                    notifyItemRemoved(position);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateHeightRecyclerView();
                        }
                    }, 490); // Delay in milliseconds (adjust as needed)

                });
            }
        }

    }
}