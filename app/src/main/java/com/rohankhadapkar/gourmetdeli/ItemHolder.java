package com.rohankhadapkar.gourmetdeli;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView itemName,itemCost;
    public ImageView itemImage;

    private ItemClickListener itemClickListener;

    public ItemHolder(View itemView) {
        super(itemView);

        itemName = itemView.findViewById(R.id.itemName);
        itemCost = itemView.findViewById(R.id.itemCost);
        itemImage = itemView.findViewById(R.id.itemImage);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition());
    }
}
