package com.aitd.module_login.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitd.module_login.R;
import com.aitd.module_login.bean.CountryCodeBean;

import androidx.recyclerview.widget.RecyclerView;
import me.yokeyword.indexablerv.IndexableAdapter;

public class CountryCodeAdpater extends IndexableAdapter<CountryCodeBean> {

    private LayoutInflater mInflater;
    private boolean showMobCountryCode = true;

    public void setShowMobCountryCode(boolean showMobCountryCode){
        this.showMobCountryCode = showMobCountryCode;
    }
    public CountryCodeAdpater (Activity activity){
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.login_layout_country_code_head_item, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.login_layout_country_code_content_item, parent, false);
        return new ContentVH(view);
    }

    private class IndexVH extends RecyclerView.ViewHolder {
        TextView tv;

        public IndexVH(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_index);
        }
    }

    private class ContentVH extends RecyclerView.ViewHolder {
        TextView mTvContryCode;

        public ContentVH(View itemView) {
            super(itemView);
            mTvContryCode = itemView.findViewById(R.id.tv_country_code);
        }
    }
    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        IndexVH vh = (IndexVH) holder;
        vh.tv.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, CountryCodeBean entity) {
        ContentVH vh = (ContentVH) holder;
        vh.mTvContryCode.setText(showMobCountryCode?entity.getCountry()+" +"+entity.getMobCountryCode():entity.getCountry());
    }
}
