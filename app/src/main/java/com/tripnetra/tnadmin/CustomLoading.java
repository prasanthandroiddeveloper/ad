package com.tripnetra.tnadmin;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class CustomLoading extends Dialog {

    public CustomLoading (Context context) {
        super (context);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.custom_loading_dilg);

        Glide.with(context).load(R.drawable.loading).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into((ImageView)findViewById(R.id.loadWV));
    }

}
