package com.example.dogsapp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogsapp.R;
import com.example.dogsapp.databinding.ItemDogBinding;
import com.example.dogsapp.model.DogBreed;
import com.example.dogsapp.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DogsListAdapter extends RecyclerView.Adapter<DogsListAdapter.DogViewHolder> implements DogClickListener {

    private ArrayList<DogBreed> dogsList;

    public DogsListAdapter(ArrayList<DogBreed> dogsList){
        this.dogsList = dogsList;
    }

    public void updateDogsList(List<DogBreed> newDogsList){
        dogsList.clear();
        dogsList.addAll(newDogsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDogBinding view = DataBindingUtil.inflate(inflater, R.layout.item_dog, parent, false);
        return new DogViewHolder(view);

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog, parent, false);
//        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {

        holder.itemView.setDog(dogsList.get(position));
        holder.itemView.setListener(this);
//        ImageView image = holder.itemView.findViewById(R.id.imageView);
//        TextView name = holder.itemView.findViewById(R.id.name);
//        TextView lifespan = holder.itemView.findViewById(R.id.lifespan);
//        CardView dogCard = holder.itemView.findViewById(R.id.cardViewDog);
//
//        name.setText(dogsList.get(position).dogBreed);
//        lifespan.setText(dogsList.get(position).lifeSpan);
//
//        Util.loadImage(image, dogsList.get(position).imageUrl, Util.getProgressDrawable(image.getContext()));
//
//        dogCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                 ListFragmentDirections.DetailFragment action = ListFragmentDirections.detailFragment();
//                 action.setDogUuid(dogsList.get(position).uuid);
//                 Navigation.findNavController(dogCard).navigate(action);
//            }
//        });
        //Glide.with(image.getContext()).load(dogsList.get(position).imageUrl).into(image);

    }

    @Override
    public int getItemCount() {
        return dogsList.size();
    }

    class DogViewHolder extends RecyclerView.ViewHolder{

        public ItemDogBinding itemView;

        public DogViewHolder(@NonNull ItemDogBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }

    @Override
    public void onDogClicked(View v) {
        String uuidString = ((TextView)v.findViewById(R.id.dogId)).getText().toString();
        int uuid = Integer.valueOf(uuidString);

         ListFragmentDirections.DetailFragment action = ListFragmentDirections.detailFragment();
         action.setDogUuid(uuid);
         Navigation.findNavController(v).navigate(action);
    }
}
