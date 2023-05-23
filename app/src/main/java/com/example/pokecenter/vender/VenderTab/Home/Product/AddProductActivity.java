package com.example.pokecenter.vender.VenderTab.Home.Product;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.pokecenter.R;
import com.example.pokecenter.databinding.ActivityAddProductBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {
    ActivityAddProductBinding binding;

    AppCompatButton Uploadbutton;
    TextInputEditText ItemName, ItemDesc;
    RelativeLayout PickImagebutton;
    ViewPager viewPager;
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    FirebaseFirestore firestore;
    StorageReference storagereference;
    FirebaseStorage mStorage;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PickImagebutton = findViewById(R.id.ChooseImage);
        viewPager = findViewById(R.id.viewPager);
        ItemDesc = findViewById(R.id.ItemDesc);
        ItemName = findViewById(R.id.ItemName);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait While Uploading Your data...");


        // firebase Instance
        firestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storagereference = mStorage.getReference();
        Uploadbutton = findViewById(R.id.UploadBtn);

        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();
        PickImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckPermission();

            }
        });
        Uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadIMages();
            }
        });

    }

    private void UploadIMages() {

        // we need list that images urls
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri IndividualImage = ChooseImageList.get(i);
            if (IndividualImage != null) {
                progressDialog.show();
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ItemImages");
                final StorageReference ImageName = ImageFolder.child("Image" + i + ": " + IndividualImage.getLastPathSegment());
                ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UrlsList.add(String.valueOf(uri));
                                if (UrlsList.size() == ChooseImageList.size()) {
                                    StoreLinks(UrlsList);
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
        String Name = ItemName.getText().toString();
        String Description = ItemDesc.getText().toString();
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
        viewPager.setAdapter(adapter);
    }
}