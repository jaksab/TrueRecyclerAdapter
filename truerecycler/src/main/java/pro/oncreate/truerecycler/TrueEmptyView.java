package pro.oncreate.truerecycler;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

@SuppressWarnings("unused,WeakerAccess")
public class TrueEmptyView {


    //
    // EmptyView params
    //


    private Context context;
    private ViewGroup parent;
    private EmptyViewOption emptyViewOption;
    private EmptyViewOption customViewOption;
    private EmptyViewOption connectionViewOption;
    private OnClickEmptyViewListener onClickListener;


    //
    // EmptyView states and data
    //


    private LayoutInflater inflater;
    private States state;
    private boolean visible;
    private int textResId;
    private int buttonResId;
    private Animation animation;


    //
    // Constructor
    //


    private TrueEmptyView(Context context, ViewGroup parent,
                          EmptyViewOption emptyViewOption,
                          EmptyViewOption customViewOption,
                          EmptyViewOption connectionViewOption,
                          int textResId, int buttonResId,
                          OnClickEmptyViewListener onClickListener) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.parent = parent;
        this.emptyViewOption = emptyViewOption;
        this.customViewOption = customViewOption;
        this.connectionViewOption = connectionViewOption;
        this.textResId = textResId;
        this.buttonResId = buttonResId;
        this.onClickListener = onClickListener;

        this.state = States.NONE;
        this.visible = false;
        animation = AnimationUtils.loadAnimation(context, R.anim.flip_anim);
    }


    //
    // Base logic
    //


    private View getEmptyView() {
        if (parent != null)
            return parent.findViewById(R.id.empty_view_layout);
        else return null;
    }

    private View createOrGetEmptyView() {
        try {
            View v = getEmptyView();
            if (v == null) {
                v = inflater.inflate(R.layout.empty_view, parent, false);
                parent.addView(v);
            }
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    private void fillEmptyView(View v, EmptyViewOption emptyViewOption) {
        try {
            stopAndRemoveAnimation();

            TextView textView = (TextView) v.findViewById(R.id.empty_view_text);
            ImageView img = (ImageView) v.findViewById(R.id.empty_view_img);
            Button button = (Button) v.findViewById(R.id.empty_view_button);

            if (emptyViewOption.text != null) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(emptyViewOption.text);

                int resId = emptyViewOption.textStyleId != EmptyViewOption.NO_RESOURCE ?
                        emptyViewOption.textStyleId : textResId != EmptyViewOption.NO_RESOURCE ?
                        textResId : EmptyViewOption.NO_RESOURCE;
                if (resId != EmptyViewOption.NO_RESOURCE)
                    applyTextStyle(resId, textView);

            } else textView.setVisibility(View.GONE);

            if (emptyViewOption.btnText != null) {
                button.setVisibility(View.VISIBLE);
                button.setText(emptyViewOption.btnText);

//                if (buttonResId != EmptyViewOption.NO_RESOURCE)
//                    applyTextStyle(buttonResId, button);

                if (onClickListener != null) {
                    button.setTag(emptyViewOption.state);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickListener.onEmptyViewClick((States) v.getTag());
                        }
                    });
                }
            } else button.setVisibility(View.GONE);

            if (emptyViewOption.imgResId != EmptyViewOption.NO_RESOURCE) {
                img.setVisibility(View.VISIBLE);
                img.setImageResource(emptyViewOption.imgResId);
                img.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        TrueEmptyView.this.animation = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else img.setVisibility(View.GONE);

        } catch (Exception ignored) {
        }
    }

    private void applyTextStyle(int resId, TextView textView) {
        if (Build.VERSION.SDK_INT < 23)
            textView.setTextAppearance(context, resId);
        else
            textView.setTextAppearance(resId);
    }

    private void stopAndRemoveAnimation() {
        try {
            if (animation != null)
                animation.cancel();
        } catch (Exception ignored) {
        }
    }


    //
    // Change visibility
    //


    public TrueEmptyView show() {
        if (getEmptyView() != null) {
            try {
                getEmptyView().setVisibility(View.VISIBLE);
                visible = true;
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    public TrueEmptyView hide() {
        if (getEmptyView() != null) {
            try {
                getEmptyView().setVisibility(View.GONE);
                visible = false;
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    public TrueEmptyView hideContent() {
        if (parent != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                try {
                    View v = parent.getChildAt(i);
                    if (v.getId() != R.id.empty_view_layout
                            && !(v instanceof AppBarLayout)
                            && !(v instanceof Toolbar)
                            && !(v instanceof CollapsingToolbarLayout))
                        v.setVisibility(View.INVISIBLE);
                } catch (Exception ignored) {
                }
            }
        }
        return this;
    }

    public TrueEmptyView showContent() {
        if (parent != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                try {
                    View v = parent.getChildAt(i);
                    if (v.getId() != R.id.empty_view_layout)
                        v.setVisibility(View.VISIBLE);
                } catch (Exception ignored) {
                }
            }
        }
        return this;
    }


    //
    // Change change states
    //


    public TrueEmptyView progress() {
        // TODO:
        this.state = States.PROGRESS;
        return this;
    }

    public TrueEmptyView empty() {
        if (emptyViewOption != null) {
            View v = createOrGetEmptyView();
            if (v != null) {
                fillEmptyView(v, emptyViewOption);
                this.state = States.EMPTY;
                visible = true;
            }
        }
        return this;
    }

    public TrueEmptyView custom() {
        if (customViewOption != null) {
            View v = createOrGetEmptyView();
            if (v != null) {
                fillEmptyView(v, customViewOption);
                this.state = States.CUSTOM;
                visible = true;
            }
        }
        return this;
    }

    public TrueEmptyView connection() {
        if (connectionViewOption != null) {
            View v = createOrGetEmptyView();
            if (v != null) {
                fillEmptyView(v, connectionViewOption);
                this.state = States.CONNECTION;
                visible = true;
            }
        }
        return this;
    }

    public TrueEmptyView reset() {
        View v = getEmptyView();
        if (v != null) {
            try {
                stopAndRemoveAnimation();
                parent.removeView(v);
                this.state = States.NONE;
                this.visible = false;
            } catch (Exception ignored) {
            }
        }
        return this;
    }


    //
    // Empty View getters and setters
    //


    public boolean isVisible() {
        return visible;
    }


    //
    // Empty View builder
    //


    public static class Builder {
        private Context context;
        private ViewGroup parent;
        private EmptyViewOption emptyViewOption;
        private EmptyViewOption customViewOption;
        private EmptyViewOption connectionViewOption;
        private OnClickEmptyViewListener onClickListener;
        private int textStyleId = EmptyViewOption.NO_RESOURCE;
        private int buttonStyleId = EmptyViewOption.NO_RESOURCE;

        private Builder(Context context) {
            this.context = context;
        }

        public static Builder create(Context context) {
            return new Builder(context);
        }

        public Builder where(ViewGroup parent) {
            this.parent = parent;
            return this;
        }

        public Builder empty(EmptyViewOption emptyViewOption) {
            this.emptyViewOption = emptyViewOption;
            if (this.emptyViewOption != null)
                this.emptyViewOption.state = States.EMPTY;
            return this;
        }

        public Builder custom(EmptyViewOption emptyViewOption) {
            this.customViewOption = emptyViewOption;
            if (this.customViewOption != null)
                this.customViewOption.state = States.CUSTOM;
            return this;
        }

        public Builder connection(EmptyViewOption emptyViewOption) {
            this.connectionViewOption = emptyViewOption;
            if (this.connectionViewOption != null)
                this.connectionViewOption.state = States.CONNECTION;
            return this;
        }

        public Builder setOnClickListener(OnClickEmptyViewListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public Builder setTextStyle(int styleId) {
            this.textStyleId = styleId;
            return this;
        }

        public Builder setButtonStyle(int styleId) {
            this.buttonStyleId = styleId;
            return this;
        }

        public TrueEmptyView build() {
            return new TrueEmptyView(context, parent, emptyViewOption, customViewOption,
                    connectionViewOption, textStyleId, buttonStyleId, onClickListener);
        }
    }


    //
    // Empty view option
    //


    public static class EmptyViewOption {
        public static final int NO_RESOURCE = -1;

        String text;
        String btnText;
        int imgResId = NO_RESOURCE;
        int textStyleId = NO_RESOURCE;
        States state;


        public EmptyViewOption(String text) {
            this.text = text;
        }

        public EmptyViewOption(int imgResId) {
            this.imgResId = imgResId;
        }

        public EmptyViewOption(String text, int imgResId) {
            this(text);
            this.imgResId = imgResId;
        }

        public EmptyViewOption(String text, String btnText) {
            this(text);
            this.btnText = btnText;
        }

        public EmptyViewOption(int imgResId, String btnText) {
            this(imgResId);
            this.btnText = btnText;
        }

        public EmptyViewOption(String text, int imgResId, String btnText) {
            this(text, imgResId);
            this.btnText = btnText;
        }

        public void setImgResId(int imgResId) {
            this.imgResId = imgResId;
        }
    }


    //
    // Empty View states
    //


    public enum States {
        NONE,
        PROGRESS, EMPTY,
        CUSTOM, CONNECTION
    }


    //
    // Listeners
    //


    public interface EmptyViewAdapterListener {
        void prepareEmptyView(TrueEmptyView trueEmptyView);
    }

    public interface OnClickEmptyViewListener {
        void onEmptyViewClick(States state);
    }

}
