package com.example.pokecenter.vender.VenderTab.Home.Product;
import static com.example.pokecenter.customer.lam.API.PokeApiFetcher.allPokeName;
import static com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity.productAdapter;
import static com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity.venderProduct;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.customer.lam.Provider.ProductData;
import com.example.pokecenter.databinding.ActivityEditProductBinding;
import com.example.pokecenter.vender.API.FirebaseSupportVender;
import com.example.pokecenter.vender.Interface.OptionRecyclerViewInterface;
import com.example.pokecenter.vender.Model.Option.VenderOptionAdapter;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;

public class EditProductActivity extends AppCompatActivity implements OptionRecyclerViewInterface {
    private static final int IMAGE_PICKER_REQUEST = 1;
    ActivityEditProductBinding binding;
    Product receiveProduct;
    private List<String> additionalImagesUrls = new ArrayList<>();
    private ImageViewAdapter imageViewAdapter;
    private RecyclerView recyclerView;

    private List<Option> myOptions = new ArrayList<>();
    private VenderOptionAdapter optionAdapter;
    private ShapeableImageView optionImage;

    List<String > myCategories = new ArrayList<>();
    List<String > optionCategories = new ArrayList<>();
    List<String > myPokemon = new ArrayList<>();
    private Uri mImageUri;

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("option");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Edit Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        receiveProduct = (Product) getIntent().getSerializableExtra("product");
        setContentView(binding.getRoot());

        // Get the list of image URLs from your data source
        additionalImagesUrls = receiveProduct.copyListImage();

        // Set up the RecyclerView and adapter
        recyclerView = binding.rcvAdditionalImage;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        imageViewAdapter = new ImageViewAdapter();
        recyclerView.setAdapter(imageViewAdapter);
        SetUpData();
        fetchingAndSetupCategories();
        binding.optionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetDialog(null, myOptions.size());
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rcvGridOptions.setLayoutManager(linearLayoutManager);
        binding.rcvGridOptions.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        optionAdapter = new VenderOptionAdapter(this, myOptions, this);
        binding.rcvGridOptions.setAdapter(optionAdapter);
        // Load and display the images using Picasso
        for (String imageUrl : additionalImagesUrls) {
            Picasso.get().load(imageUrl).into(imageViewAdapter);
        }

        // Set up the button click listener to add new images
        binding.addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICKER_REQUEST);
            }
        });

        binding.UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
    }
    private void Update(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressText.setVisibility(View.VISIBLE);
        Product updatedProduct = new Product(receiveProduct.getId(), binding.ItemName.getText().toString(),
                binding.ItemDesc.getText().toString(),
                additionalImagesUrls, myOptions,
                FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {

            boolean isSuccessful = true;

            try {
                new FirebaseSupportVender().updateProduct(updatedProduct);
            } catch (IOException e) {
                isSuccessful = false;
            }

            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                if (finalIsSuccessful) {
                    for (int i = 0; i < venderProduct.size(); i++) {
                        if (venderProduct.get(i).getId().equals(updatedProduct.getId())) {
                            venderProduct.set(i, updatedProduct);
                            break;
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(this, VenderProductDetailActivity.class);
                    intent.putExtra("product object", updatedProduct);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Update Product Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Something wrong because connection error", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.progressBar.setVisibility(View.GONE);
        binding.progressText.setVisibility(View.GONE);
    }
    private void SetUpData() {
        binding.ItemName.setText(receiveProduct.getName());
        binding.ItemDesc.setText(receiveProduct.getDesc());
        myOptions.addAll(receiveProduct.getOptions());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rcvGridOptions.setLayoutManager(linearLayoutManager);
        binding.rcvGridOptions.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        optionAdapter = new VenderOptionAdapter(this, myOptions, this);
        binding.rcvGridOptions.setAdapter(optionAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Get the selected image URI
                Uri imageUri = data.getData();

                // Upload the image to Firebase Storage
                uploadImageToFirebaseStorage(imageUri);
            }
        }
    }
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image upload successful
                // Get the download URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // Convert the URI to a string
                        String imageUrl = downloadUri.toString();

                        // Add the image URL to the list
                        additionalImagesUrls.add(imageUrl);

                        // Update the RecyclerView with the new image
                        imageViewAdapter.notifyItemInserted(additionalImagesUrls.size() - 1);
                        updateHeightRecyclerView();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error getting the download URL
                        // Handle the failure
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error uploading the image
                // Handle the failure
            }
        });
    }


    private void updateHeightRecyclerView() {
        int itemsPerRow = 3;
        int itemHeight = 250; // Set the item height in pixels
        int marginBetweenItems = 16; // Set the margin between items in pixels
        int recyclerViewHeight = ((additionalImagesUrls.size() - 1) / itemsPerRow + 1) * (itemHeight + marginBetweenItems);

        RecyclerView rcvAdditionalImage = binding.rcvAdditionalImage;
        ViewGroup.LayoutParams layoutParams = rcvAdditionalImage.getLayoutParams();
        layoutParams.height = recyclerViewHeight;
        rcvAdditionalImage.setLayoutParams(layoutParams);
    }

    public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder>
            implements Target {
        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lam_additional_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            String imageUrl = additionalImagesUrls.get(position);
            Picasso.get().load(imageUrl).into(holder.productImage);
        }

        @Override
        public int getItemCount() {
            return additionalImagesUrls.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {
            private ImageView productImage;
            private ImageButton removeButton;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);

                productImage = itemView.findViewById(R.id.product_image);
                removeButton = itemView.findViewById(R.id.remove_button);

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        additionalImagesUrls.remove(position);
                        notifyItemRemoved(position);
                        updateHeightRecyclerView();
                    }
                });
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // This method is called when the bitmap is successfully loaded
            // You can perform any additional operations on the loaded bitmap if needed

            // Set the loaded bitmap to the ImageView
            ImageView imageView = recyclerView.findViewWithTag(this);
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            // This method is called if the bitmap loading fails
            // You can handle the failure or fallback to a placeholder image
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            // This method is called when the image loading starts
            // You can set a placeholder image if needed
        }
    }
    private void openBottomSheetDialog(Option existingOption, int countOption) {
        optionCategories.size();
        View viewDialog = getLayoutInflater().inflate(R.layout.ninh_bottom_sheet_add_option, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(viewDialog);
        dialog.show();

        viewDialog.setOnClickListener(view -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        EditText optionName = viewDialog.findViewById(R.id.optionName);
        EditText optionQuantity = viewDialog.findViewById(R.id.optionQuantity);
        EditText optionPrice = viewDialog.findViewById(R.id.optionPrice);
        AutoCompleteTextView optionPokemon = viewDialog.findViewById(R.id.optionPokemon);
        AutoCompleteTextView optionCategory = viewDialog.findViewById(R.id.optionCategories);

        ImageButton uploadImageOption = viewDialog.findViewById(R.id.change_avatar_button);
        Button finishButton = viewDialog.findViewById(R.id.finishButton);
        optionImage = viewDialog.findViewById(R.id.option_Image);

        if (existingOption != null) {
            optionName.setText(existingOption.getOptionName());
            optionQuantity.setText(String.valueOf(existingOption.getInputQuantity()));
            optionPrice.setText(String.valueOf(existingOption.getPrice()));
            if(!existingOption.getOptionName().equals("null"))
            Picasso.get().load(existingOption.getOptionImage()).into(optionImage);
        }
        uploadImageOption.setOnClickListener(view -> {
            ImagePicker.with(EditProductActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .galleryOnly()
                    .createIntent(intent -> {
                        openSomeActivityForResult(intent);
                        return Unit.INSTANCE;
                    });
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        for (String poke : allPokeName) {
            adapter.add(poke);
        }
        adapter.add("No");
        adapter.add("Many");
        adapter.add("NULL");
        adapter.add("Don'tKnow");
        optionPokemon.setAdapter(adapter);
        optionPokemon.setThreshold(1);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        for (String poke : optionCategories) {
            categoriesAdapter.add(poke);
        }
        optionCategory.setAdapter(categoriesAdapter);
        optionCategory.setThreshold(1);



        finishButton.setOnClickListener(view -> {
            finishButton.setVisibility(View.INVISIBLE);

            if (validateDataInput(optionName, optionQuantity, optionPrice)) {

                Option newOption = new Option(
                        optionName.getText().toString(),String.valueOf(mImageUri),Integer.parseInt(optionQuantity.getText().toString()),Integer.parseInt(optionQuantity.getText().toString()),Integer.parseInt(optionPrice.getText().toString())
                );

                if (existingOption != null) {
                    /* update existing Option */
                    existingOption.setOptionName(newOption.getOptionName());
                    existingOption.setOptionImage(newOption.getOptionImage());
                    existingOption.setCurrentQuantity(newOption.getCurrentQuantity());
                    existingOption.setInputQuantity(newOption.getCurrentQuantity());
                    existingOption.setPrice(newOption.getPrice());
                }else{
                    myOptions.add(newOption);
                }
                if (!myCategories.contains(optionCategory.getText().toString())) {
                    myCategories.add(optionCategory.getText().toString());
                }
                if (!myPokemon.contains(optionPokemon.getText().toString())) {
                    myPokemon.add(optionPokemon.getText().toString());
                }
                optionAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            finishButton.setVisibility(View.VISIBLE);
        });
    }

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        mImageUri = data.getData();
                        Picasso.get().load(mImageUri).into(optionImage);
                        updateOptionImage();
                    }
                }
            });
    private void updateOptionImage() {
        if (mImageUri != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(Calendar.getInstance().getTime());
            StorageReference fileRef = mStorageRef.child( mImageUri.getLastPathSegment() +dateString);
            fileRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                            downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(EditProductActivity.this, "Update Option Image Successful", Toast.LENGTH_SHORT).show();
                                    mImageUri = uri;
                                    //currentAccount.setAvatar(String.valueOf(uri));
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProductActivity.this, "Update Option Image Failed", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private boolean validateDataInput(EditText name, EditText quantity, EditText price) {
        if (name.getText().toString().isEmpty()) {
            name.setError("You have not entered Full Name");
            return false;
        }

        if (quantity.getText().toString().isEmpty()) {
            quantity.setError("You have not entered Phone Number");
            return false;
        }

        if (price.getText().toString().isEmpty()) {
            price.setError("You have not entered Option 1");
            return false;
        }

        return true;
    }

    @Override
    public void onOptionItemClick(int position) {
        openBottomSheetDialog(myOptions.get(position), myOptions.size());
    }
    private void fetchingAndSetupCategories() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            List<String> FetchedCategories = new ArrayList<>();
            boolean isSuccess = true;

            try {
                FetchedCategories = new FirebaseSupportVender().fetchingAllCategoryTag();
            } catch (IOException e) {
                isSuccess = false;
            }

            boolean finalIsSuccess = isSuccess;
            List<String> finalFetchedCategories = FetchedCategories;
            handler.post(() -> {

                if (finalIsSuccess) {
                    if (finalFetchedCategories.size() > 0) {
                        optionCategories = finalFetchedCategories;
                    }
                }
            });
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
