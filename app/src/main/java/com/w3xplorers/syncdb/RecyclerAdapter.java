package com.w3xplorers.syncdb;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DELL on 5/25/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.myViewHolder> {

    private ArrayList<Contacts> arrayList = new ArrayList<>();

    public  RecyclerAdapter(ArrayList<Contacts> arrayList){
        this.arrayList = arrayList;
    }
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view,viewGroup,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder viewHolder, int i) {
        viewHolder.name.setText(arrayList.get(i).getName());
        int sync_status = arrayList.get(i).getSync_status();
        if(sync_status==DBContact.sync_status_ok){
            myViewHolder.sync_Status.setImageResource(R.drawable.ok);
        }else{
            myViewHolder.sync_Status.setImageResource(R.drawable.sync);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{


        static ImageView sync_Status;
        TextView name;

        public myViewHolder(View itemView) {
            super(itemView);
            sync_Status = (ImageView) itemView.findViewById(R.id.imgsync);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
