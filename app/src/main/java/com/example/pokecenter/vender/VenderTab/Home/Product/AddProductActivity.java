package com.example.pokecenter.vender.VenderTab.Home.Product;

import static com.example.pokecenter.customer.lam.API.PokeApiFetcher.allPokeName;
import static com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity.productAdapter;
import static com.example.pokecenter.vender.VenderTab.Home.Product.VenderProductActivity.venderProduct;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.customer.lam.Model.option.Option;
import com.example.pokecenter.customer.lam.Model.product.Product;
import com.example.pokecenter.databinding.ActivityAddProductBinding;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;

public class AddProductActivity extends AppCompatActivity implements OptionRecyclerViewInterface {
    ActivityAddProductBinding binding;
    private List<Option> myOptions = new ArrayList<>();
    private VenderOptionAdapter optionAdapter;
    OptionRecyclerViewInterface optionRecyclerViewInterface;
    Uri ImageUri;
    private ShapeableImageView optionImage;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    FirebaseFirestore firestore;
    StorageReference storagereference;
    FirebaseStorage mStorage;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    ProgressDialog progressDialog;
    
    List<String > myCategories = new ArrayList<>();
    List<String > optionCategories = new ArrayList<>();
    String optionCategory;
    List<String > myPokemon = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.light_primary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setTitle("Add new Items");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mStorageRef = FirebaseStorage.getInstance().getReference("option");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait While Uploading Your data...");
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

        optionAdapter = new VenderOptionAdapter(this, myOptions, optionRecyclerViewInterface);
        binding.rcvGridOptions.setAdapter(optionAdapter);



        // firebase Instance
        firestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storagereference = mStorage.getReference();

        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();
        binding.ChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });
        binding.UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadIMages();
            }
        });
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton1:
                        binding.svNoOption.setVisibility(View.INVISIBLE);
                        binding.svListOptions.setVisibility(View.VISIBLE);
                        binding.llOptions.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioButton2:
                        binding.svNoOption.setVisibility(View.VISIBLE);
                        binding.svListOptions.setVisibility(View.INVISIBLE);
                        binding.llOptions.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        for (String poke : allPokeName) {
            adapter.add(poke);
        }
        adapter.add("No");
        adapter.add("Many");
        adapter.add("NULL");
        adapter.add("Don'tKnow");
        binding.optionPokemon.setAdapter(adapter);
        binding.optionPokemon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionCategories);
        binding.optionCategories.setAdapter(categoriesAdapter);

        binding.optionCategories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().toLowerCase();
                List<String> suggestions = new ArrayList<>();

                // Iterate through optionCategories to find matching suggestions
                for (String category : optionCategories) {
                    if (category.toLowerCase().startsWith(input)) {
                        suggestions.add(category);
                    }
                }

                // Update the adapter with the new suggestions
                ArrayAdapter<String> updatedAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_item, suggestions);
                binding.optionCategories.setAdapter(updatedAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setContentView(binding.getRoot());

    }

    public void AddProduct() {
        if(binding.radioGroup.getCheckedRadioButtonId()==-1)
        {
            Toast.makeText(this, "Please choose your options", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        else if(binding.radioGroup.getCheckedRadioButtonId()==R.id.radioButton2) {
            myOptions.clear();
            Option o = new Option("null", "", Integer.parseInt(binding.ItemQuantity.getText().toString()), Integer.parseInt(binding.ItemQuantity.getText().toString()), Integer.parseInt(binding.ItemPrice.getText().toString()));
            myOptions.add(o);
            if (!myCategories.contains(binding.optionCategories.getText().toString())) {
                myCategories.add(binding.optionCategories.getText().toString());
            }
            if (!myPokemon.contains(binding.optionPokemon.getText().toString())) {
                myPokemon.add(binding.optionPokemon.getText().toString());
            }
        }
        Product newProduct = new Product(null, binding.ItemName.getText().toString(),
                binding.ItemDesc.getText().toString(),
                UrlsList, myOptions,
                FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ","));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {

            boolean isSuccessful = true;

            try {
                new FirebaseSupportVender().addNewProduct(newProduct);
                new FirebaseSupportVender().updateTotalProduct(venderProduct.size()+1);
                new FirebaseSupportVender().updatePokemonAfterAddProduct(newProduct.getId(),myPokemon);
                new FirebaseSupportVender().updateCategoryAfterAddProduct(newProduct.getId(),myCategories);

            } catch (IOException e) {
                isSuccessful = false;
            }

            boolean finalIsSuccessful = isSuccessful;
            handler.post(() -> {
                if (finalIsSuccessful) {
                    venderProduct.add(newProduct);
                    productAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Added new Product", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Something wrong because connection error", Toast.LENGTH_SHORT).show();
                }
            });
        });
        progressDialog.dismiss();
        finish();
    }
    private void UploadIMages() {

        // we need list that images urls
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri IndividualImage = ChooseImageList.get(i);
            if (IndividualImage != null) {
                progressDialog.show();
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageProducts");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = dateFormat.format(Calendar.getInstance().getTime());
                final StorageReference ImageName = ImageFolder.child("Image" + i + ": " + IndividualImage.getLastPathSegment() + dateString);
                ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UrlsList.add(String.valueOf(uri));
                                if (UrlsList.size() == ChooseImageList.size()) {
                                    AddProduct();
                                }
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(this, "Please fill All Field", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void StoreLinks(ArrayList<String> urlsList) {
        // now we need get text from EditText
        String Name = binding.ItemName.getText().toString();
        String Description = binding.ItemDesc.getText().toString();
        if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Description) && ImageUri != null) {
            // now we need a model class
            ItemModel model = new ItemModel(Name, Description, "", UrlsList);
            firestore.collection("Items").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // now here we need Item id and set into model
                    model.setItemId(documentReference.getId());
                    firestore.collection("Items").document(model.getItemId())
                            .set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    // if data uploaded successfully then show ntoast
                                    Toast.makeText(AddProductActivity.this, "Your data Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            });

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill All field", Toast.LENGTH_SHORT).show();
        }
        // if you want to clear viewpager after uploading Images
        ChooseImageList.clear();


    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddProductActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddProductActivity.this, new
                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                PickImageFromgallry();
            }

        } else {
            PickImageFromgallry();
        }
    }

    private void PickImageFromgallry() {
        // here we go to gallery and select Image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                ImageUri = data.getClipData().getItemAt(i).getUri();
                ChooseImageList.add(ImageUri);
// now we need Adapter to show Images in viewpager

            }
            setAdapter();

        }
    }

    private void setAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, ChooseImageList);
        binding.viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void openBottomSheetDialog(Option existingOption, int countOption) {

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
        AutoCompleteTextView optionCategory = viewDialog.findViewById(R.id.option_categories);

        ImageButton uploadImageOption = viewDialog.findViewById(R.id.change_avatar_button);
        Button finishButton = viewDialog.findViewById(R.id.finishButton);
        optionImage = viewDialog.findViewById(R.id.option_Image);

        if (existingOption != null) {
            optionName.setText(existingOption.getOptionName());
            optionQuantity.setText(existingOption.getInputQuantity());
            optionPrice.setText(existingOption.getPrice());
            Picasso.get().load(existingOption.getOptionImage()).into(optionImage);
        }
        uploadImageOption.setOnClickListener(view -> {
            ImagePicker.with(AddProductActivity.this)
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
        optionPokemon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionCategories);
        optionCategory.setAdapter(categoriesAdapter);

        optionCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().toLowerCase();
                List<String> suggestions = new ArrayList<>();

                // Iterate through optionCategories to find matching suggestions
                for (String category : optionCategories) {
                    if (category.toLowerCase().startsWith(input)) {
                        suggestions.add(category);
                    }
                }

                // Update the adapter with the new suggestions
                ArrayAdapter<String> updatedAdapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_item, suggestions);
                optionCategory.setAdapter(updatedAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


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
                                    Toast.makeText(AddProductActivity.this, "Update Option Image Successful", Toast.LENGTH_SHORT).show();
                                    mImageUri = uri;
                                    //currentAccount.setAvatar(String.valueOf(uri));
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductActivity.this, "Update Option Image Failed", Toast.LENGTH_SHORT).show();

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

}