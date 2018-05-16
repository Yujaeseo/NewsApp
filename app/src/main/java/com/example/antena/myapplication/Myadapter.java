package com.example.antena.myapplication;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class Myadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> mDataset;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position, View view, String url);
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder{

        public TextView mTopicView;
        public TextView mTitleView;
        public TextView mDateView;
        public TextView mPressView;
        public ImageView mImageView;
        public Button mAddButton;
        public LinearLayout linearCardView;

        public ViewHolderImage (View v){
            super(v);

            mTopicView = v.findViewById(R.id.topicView);
            mTitleView = v.findViewById(R.id.titleView);
            mDateView = v.findViewById(R.id.dateView);
            mPressView = v.findViewById(R.id.pressView);
            mImageView = v.findViewById(R.id.imageView);
            mAddButton = v.findViewById(R.id.addButton);

            linearCardView = v.findViewById(R.id.linearCardLayout);

            linearCardView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (onRecyclerViewItemClickListener != null){
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v,mDataset.get(getAdapterPosition()).getNewsLink());
                    }
                }
            });
        }
    }

    public class ViewHolderText extends RecyclerView.ViewHolder{

        public TextView mTopicView;
        public TextView mTitleView;
        public TextView mDateView;
        public TextView mPressView;
        public ImageView mImageView;
        public Button mAddButton;

        public ViewHolderText(View v){
            super(v);

            mTopicView = v.findViewById(R.id.topicView);
            mTitleView = v.findViewById(R.id.titleView);
            mDateView = v.findViewById(R.id.dateView);
            mPressView = v.findViewById(R.id.pressView);
            mImageView = v.findViewById(R.id.imageView);
            mAddButton = v.findViewById(R.id.addButton);

        }
    }

    public Myadapter(List<Item> myDataset,OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        this.mDataset = myDataset;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public int getItemViewType(int position){

        switch (mDataset.get(position).getViewType()){

            case 1:
                return Item.IMAGE_TYPE;
            case 2:
                return Item.TEXT_TYPE;
            default:
                Log.w("null",Integer.toString(mDataset.get(position).getViewType()));
                return -1;
        }

    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v;

        switch (viewType){

            case Item.IMAGE_TYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
                return new ViewHolderImage(v);
            case Item.TEXT_TYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewtext,parent,false);
                return new ViewHolderText(v);
            default:
                //Log.d("null",Integer.toString(viewType));
                return null;
        }
    }

    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position){

        Item obj = mDataset.get(position);

        switch ( obj.getViewType() ){

            case Item.IMAGE_TYPE:

                ((ViewHolderImage) holder).mTopicView.setText(obj.getTopic());
                ((ViewHolderImage) holder).mTitleView.setText(obj.getTitle());
                ((ViewHolderImage) holder).mDateView.setText(obj.getPubdate());
                ((ViewHolderImage) holder).mPressView.setText(obj.getPress());

                String url = mDataset.get(position).getThumbnail();

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.mipmap.ic_launcher);

                Glide.with( ((ViewHolderImage)holder).mImageView).load(url).apply(options).into(((ViewHolderImage)holder).mImageView);

                break;

            case Item.TEXT_TYPE:

                ((ViewHolderText) holder).mTopicView.setText(obj.getTopic());
                ((ViewHolderText) holder).mTitleView.setText(obj.getTitle());
                ((ViewHolderText) holder).mDateView.setText(obj.getPubdate());
                ((ViewHolderText) holder).mPressView.setText(obj.getPress());

                break;
        }
    }

    public int getItemCount(){
        return mDataset.size();
    }

    public String getUrl (int position) { return mDataset.get(position).getNewsLink();}

    public void setOnRecyclerViewItemClickListener (OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }
}
