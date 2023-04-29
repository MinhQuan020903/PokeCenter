package com.example.pokecenter.customer.quan.CustomerProfileTab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokecenter.databinding.FragmentCustomerProfileAccountInfoBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerProfileAccountInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerProfileAccountInfoFragment extends Fragment {

    private FragmentCustomerProfileAccountInfoBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerProfileAccountInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment customerProfileAccountInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerProfileAccountInfoFragment newInstance(String param1, String param2) {
        CustomerProfileAccountInfoFragment fragment = new CustomerProfileAccountInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCustomerProfileAccountInfoBinding.inflate(inflater, container, false);

        binding.backButton.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        binding.UserProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SIUUU", "Mucha gracias aficion, esra es da vosotros, SIUUUUUUUUUUUUU");
                ImagePicker.with(requireActivity())
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(150)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(150, 150)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .galleryOnly()
                        .createIntent( intent -> {
                            openSomeActivityForResult(intent);
                            return Unit.INSTANCE;
                        });

            }
        });

        return binding.getRoot();
    }

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri uri = data.getData();
                        binding.UserProfileImage.setImageURI(uri);
                        binding.UserProfileImage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                }
            });

}