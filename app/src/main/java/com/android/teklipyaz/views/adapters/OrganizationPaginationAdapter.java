package com.android.teklipyaz.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.teklipyaz.R;
import com.android.teklipyaz.models.entities.ImageModel;
import com.android.teklipyaz.models.entities.OrganizationModel;
import com.android.teklipyaz.utils.ImageHelper;
import com.android.teklipyaz.utils.PaginationAdapterCallback;
import com.android.teklipyaz.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class OrganizationPaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<OrganizationModel> organizationResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private PaginationAdapterCallback mCallback;
    private OnItemClickListener clickListener;

    private String errorMsg;


    public OrganizationPaginationAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        organizationResults = new ArrayList<>();


    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        //View v1 = inflater.inflate(R.layout.item_organization_list, parent, false);
        View v1 = inflater.inflate(R.layout.item_organization, parent, false);
        viewHolder = new OrganizationVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        OrganizationModel result = organizationResults.get(position); // Organization

        switch (getItemViewType(position)) {
            case ITEM:
                final OrganizationVH organizationVH = (OrganizationVH) holder;

                organizationVH.mTitle.setText(result.getTitle());
                organizationVH.mCityCategory.setText(result.getCityId() + " | " + result.getCategoryId());
                if(result.getAddress() != null && result.getAddress().isEmpty())
                {
                    organizationVH.mAddress.setVisibility(View.GONE);
                } else {
                    organizationVH.mAddress.setVisibility(View.VISIBLE);
                    organizationVH.mAddress.setText(result.getAddress());
                }

                if(result.getTelephone() != null && result.getTelephone().isEmpty())
                {
                    organizationVH.mPhone.setVisibility(View.GONE);
                } else {
                    organizationVH.mPhone.setVisibility(View.VISIBLE);
                    organizationVH.mPhone.setText(result.getTelephone());
                }

                List<ImageModel> images = result.getImages();
                /*if(images != null && images.size() > 0) {
                    Log.d("My image", "List item: " + images.get(0).getFilepath());
                    for(int i=0;i<images.size();i++)
                    {
                        Log.d("My image", "List item: " + images.get(i).getFilepath());
                    }

                }
                else
                    Log.d("My image is ","null");*/
                /**
                 * Using Glide to handle image loading.
                 * Learn more about Glide here:
                 * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
                 */
                ImageHelper imageUtil = new ImageHelper(context,organizationVH.mImg, organizationVH.mProgress);
                imageUtil.show(images.get(0).getFilepath());
                /*Glide
                        .with(context)
                        .load(ApiUtil.BASE_IMAGE_URL + images.get(0).getFilepath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                // TODO: 08/11/16 handle failure
                                organizationVH.mProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                // image ready, hide progress now
                                organizationVH.mProgress.setVisibility(View.GONE);
                                return false;   // return false if you want Glide to handle everything else.
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .centerCrop()
                        .crossFade()
                        .into(organizationVH.mImg);*/

                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);
                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return organizationResults == null ? 0 : organizationResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == organizationResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(OrganizationModel r) {
        organizationResults.add(r);
        notifyItemInserted(organizationResults.size() - 1);
    }

    public void addAll(List<OrganizationModel> moveResults) {
        for (OrganizationModel result : moveResults) {
            add(result);
        }
    }

    public void remove(OrganizationModel r) {
        int position = organizationResults.indexOf(r);
        if (position > -1) {
            organizationResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new OrganizationModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = organizationResults.size() - 1;
        OrganizationModel result = getItem(position);

        if (result != null) {
            organizationResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public OrganizationModel getItem(int position) {
        return organizationResults.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(organizationResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class OrganizationVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mAddress;
        private TextView mPhone;
        private TextView mCityCategory; // displays "year | language"
        private ImageView mImg;
        private ProgressBar mProgress;

        public OrganizationVH(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAddress = (TextView) itemView.findViewById(R.id.address);
            mPhone = (TextView) itemView.findViewById(R.id.phone);
            mCityCategory = (TextView) itemView.findViewById(R.id.organization_city_category);
            mImg = (ImageView) itemView.findViewById(R.id.image);
            mProgress = (ProgressBar) itemView.findViewById(R.id.organization_progress);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getLayoutPosition());
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    //Log.d("My tag", "There I try retryPageLoad");
                    break;
            }
        }
    }
}
