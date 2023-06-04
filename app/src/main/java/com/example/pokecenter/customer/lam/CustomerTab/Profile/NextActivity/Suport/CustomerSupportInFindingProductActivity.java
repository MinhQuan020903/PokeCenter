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
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.CustomerTab.Profile.NextActivity.CustomerAccountInfoActivity;
import com.example.pokecenter.customer.lam.Model.cart.CartAdapter;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.databinding.ActivityCustomerSupportInFindingProductBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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