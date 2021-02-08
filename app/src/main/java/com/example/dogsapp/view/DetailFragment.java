package com.example.dogsapp.view;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dogsapp.R;
import com.example.dogsapp.databinding.FragmentDetailBinding;
import com.example.dogsapp.databinding.SendSmsDialogBinding;
import com.example.dogsapp.model.DogBreed;
import com.example.dogsapp.model.DogPalette;
import com.example.dogsapp.model.SmsInfo;
import com.example.dogsapp.util.Util;
import com.example.dogsapp.viewmodel.DetailViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {


    private Boolean sendSmsStarted = false;
    private DogBreed currentDog;

    private DetailViewModel viewModel;
    private DogsListAdapter dogsListAdapter = new DogsListAdapter(new ArrayList<>());

   /* @BindView(R.id.dogImage)
    ImageView dogImage;
    @BindView(R.id.dogName)
    TextView dogName;
    @BindView(R.id.dogPurpose)
    TextView dogPurpose;
    @BindView(R.id.dogTemperament)
    TextView dogTemperament;
    @BindView(R.id.dogLifeSpan)
    TextView dogLifeSpan;*/

    private int dogUuid;
    private FragmentDetailBinding binding;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        this.binding = binding;
        //View view = inflater.inflate(R.layout.fragment_detail, container, false);
        //ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null){
            dogUuid = DetailFragmentArgs.fromBundle(getArguments()).getDogUuid();
        }
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.refresh(dogUuid);
        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.dogs.observe(getViewLifecycleOwner(), dogs -> {
            if(dogs != null && dogs instanceof DogBreed && getContext() !=null){
                currentDog = dogs;
                binding.setDogItem(dogs);

                if(dogs.imageUrl != null){
                    setUpBackgroundColor(dogs.imageUrl);
                }
                /*dogName.setText(dogs.dogBreed);
                dogPurpose.setText(dogs.bredFor);
                dogTemperament.setText(dogs.temperament);
                dogLifeSpan.setText(dogs.lifeSpan);

                if (dogImage != null){
                    Util.loadImage(dogImage, dogs.imageUrl, new CircularProgressDrawable(getContext()));
                }*/
            }
        });
    }
    private void setUpBackgroundColor(String url){
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource)
                                .generate(palette -> {
                                    int intColor = palette.getLightMutedSwatch().getRgb();
                                    DogPalette myPalette = new DogPalette(intColor);
                                    binding.setPalette(myPalette);
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_send_sms:{
                if(!sendSmsStarted){
                    sendSmsStarted = true;

                    ((MainActivity) getActivity()).checkSmsPermission();
                }
                break;
            }
            case R.id.action_share:{
               Intent intent = new Intent(Intent.ACTION_SEND);
               intent.setType("text/plain");
               intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed");
               intent.putExtra(Intent.EXTRA_TEXT, currentDog.dogBreed+ " bred for "+ currentDog.bredFor);
               intent.putExtra(Intent.EXTRA_STREAM, currentDog.imageUrl);

               startActivity(Intent.createChooser(intent, "Share with"));
                break;

            }
        }

        return super.onOptionsItemSelected(item);

    }
    public void onPermissionResult(Boolean permissionGranted){
        if(isAdded() && sendSmsStarted && permissionGranted){
            SmsInfo smsInfo = new SmsInfo("",  currentDog.dogBreed +"Bred for"+ currentDog.bredFor, currentDog.imageUrl);
            SendSmsDialogBinding dialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(getContext()), R.layout.send_sms_dialog, null, false
            );
            new AlertDialog.Builder(getContext())
                    .setView(dialogBinding.getRoot())
                    .setPositiveButton("Send sms", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!dialogBinding.smsDestination.getText().toString().isEmpty()){
                                smsInfo.to = dialogBinding.smsDestination.getText().toString();
                                sendSms(smsInfo);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            sendSmsStarted = false;
            dialogBinding.setSmsInfo(smsInfo);
        }
    }
    private void sendSms(SmsInfo smsInfo){
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0,intent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null);
    }
}