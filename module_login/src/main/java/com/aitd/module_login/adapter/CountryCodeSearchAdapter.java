package com.aitd.module_login.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitd.module_login.R;
import com.aitd.module_login.bean.CountryCodeBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 国家区号搜索
 *
 * @author Jack
 */

public class CountryCodeSearchAdapter extends RecyclerView.Adapter<CountryCodeSearchAdapter.ViewHolder> {
    private Activity mContext;
    private List<CountryCodeBean> mList = null;
    private LayoutInflater mInflater;
    private boolean showMobCountryCode = true;

    public void setShowMobCountryCode(boolean showMobCountryCode) {
        this.showMobCountryCode = showMobCountryCode;
    }

    public CountryCodeSearchAdapter(Activity activity) {
        this.mContext = activity;
        mInflater = LayoutInflater.from(activity);
    }

    public void notifyDataChanged(List<CountryCodeBean> list) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.clear();
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.login_layout_country_code_content_item, viewGroup, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setViewData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvContryCode;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvContryCode = itemView.findViewById(R.id.tv_country_code);
        }

        public void setViewData(CountryCodeBean bean) {
            mTvContryCode.setText(showMobCountryCode?bean.getCountry() + " +" + bean.getMobCountryCode():bean.getCountry());
            mTvContryCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick != null) {
                        onItemClick.onItemClick(bean);
                    }
                }
            });
        }
    }

    private OnItemClickListen onItemClick;

    public void setOnItemClickListen(OnItemClickListen clickItemListen) {
        onItemClick = clickItemListen;
    }

    public interface OnItemClickListen {
        void onItemClick(CountryCodeBean bean);
    }
}
