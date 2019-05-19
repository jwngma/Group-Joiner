package com.joiner.groupjoiner.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joiner.groupjoiner.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class GroupsViewHolder extends RecyclerView.ViewHolder {


    private Context context;

    public GroupsViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    View mView;

    ImageView image;
    private TextView title;
    private TextView link;


    public GroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
        image=mView.findViewById(R.id.group_image);
        title=mView.findViewById(R.id.group_title);
        link=mView.findViewById(R.id.group_link);
    }

    public void setImage(Context ctx, final String imagee){
        Picasso.get().load(imagee).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.menu_icon).into(image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(imagee).into(image);

            }
        });
    }

    public void setTitle(String  titlee){
        title.setText(titlee);
    }

    public void setLink(String linkk){
        link.setText(linkk);
    }


}
