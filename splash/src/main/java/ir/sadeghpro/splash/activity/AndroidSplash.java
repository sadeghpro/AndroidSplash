package ir.sadeghpro.splash.activity;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.YoYo;
import com.github.jorgecastillo.FillableLoader;
import com.github.jorgecastillo.FillableLoaderBuilder;
import com.github.jorgecastillo.State;
import com.github.jorgecastillo.clippingtransforms.PlainClippingTransform;
import com.github.jorgecastillo.listener.OnStateChangeListener;
import ir.sadeghpro.splash.R;
import ir.sadeghpro.splash.cnst.Flags;
import ir.sadeghpro.splash.model.ConfigSplash;
import ir.sadeghpro.splash.utils.UIUtil;
import ir.sadeghpro.splash.utils.ValidationUtil;

import io.codetail.animation.ViewAnimationUtils;


abstract public class AndroidSplash extends AppCompatActivity {

    private RelativeLayout mRlReveal;
    private ImageView mImgLogo;
    private AppCompatTextView mTxtTitle;
    private FillableLoader mPathLogo;
    private FrameLayout mFl;


    private ConfigSplash mConfigSplash;
    private boolean hasAnimationStarted = false;
    private int pathOrLogo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConfigSplash = new ConfigSplash();
        initSplash(mConfigSplash);

        pathOrLogo = ValidationUtil.hasPath(mConfigSplash);
        initUI(pathOrLogo);


    }


    public void initUI(int flag) {
        setContentView(R.layout.activity_main_lib);

        mRlReveal = findViewById(R.id.rlColor);
        mTxtTitle = findViewById(R.id.txtTitle);


        switch (flag) {
            case Flags.WITH_PATH:
                mFl = findViewById(R.id.flCentral);
                initPathAnimation();
                break;
            case Flags.WITH_LOGO:
                mImgLogo = findViewById(R.id.imgLogo);
                mImgLogo.setImageResource(mConfigSplash.getLogoSplash());
                break;
            default:
                break;

        }

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !hasAnimationStarted) {
            startCircularReveal();
        }
    }


    public void initPathAnimation() {
        int viewSize = getResources().getDimensionPixelSize(R.dimen.fourthSampleViewSize);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(viewSize, viewSize);

        params.setMargins(0, 0, 0, 50);
        FillableLoaderBuilder loaderBuilder = new FillableLoaderBuilder();
        mPathLogo = loaderBuilder
                .parentView(mFl)
                .layoutParams(params)
                .svgPath(mConfigSplash.getPathSplash())
                .originalDimensions(mConfigSplash.getOriginalWidth(), mConfigSplash.getOriginalHeight())
                .strokeWidth(mConfigSplash.getPathSplashStrokeSize())
                .strokeColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & getResources().getColor(mConfigSplash.getPathSplashStrokeColor())))))
                .fillColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & getResources().getColor(mConfigSplash.getPathSplashFillColor())))))
                .strokeDrawingDuration(mConfigSplash.getAnimPathStrokeDrawingDuration())
                .fillDuration(mConfigSplash.getAnimPathFillingDuration())
                .clippingTransform(new PlainClippingTransform())
                .build();
        mPathLogo.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(int i) {
                if (i == State.FINISHED) {
                    startTextAnimation();
                }
            }
        });

    }


    public void startCircularReveal() {

        // get the final radius for the clipping circle
        int finalRadius = Math.max(mRlReveal.getWidth(), mRlReveal.getHeight()) + mRlReveal.getHeight() / 2;
        //bottom or top
        int y = UIUtil.getRevealDirection(mRlReveal, mConfigSplash.getRevealFlagY());
        //left or right
        int x = UIUtil.getRevealDirection(mRlReveal, mConfigSplash.getRevealFlagX());

        mRlReveal.setBackgroundColor(getResources().getColor(mConfigSplash.getBackgroundColor()));
        Animator animator = ViewAnimationUtils.createCircularReveal(mRlReveal, x, y, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(mConfigSplash.getAnimCircularRevealDuration());
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (pathOrLogo == Flags.WITH_PATH) {
                    mPathLogo.start();
                } else {
                    startLogoAnimation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        hasAnimationStarted = true;
    }


    public void startLogoAnimation() {
        mImgLogo.setVisibility(View.VISIBLE);
        mImgLogo.setImageResource(mConfigSplash.getLogoSplash());

        YoYo.with(mConfigSplash.getAnimLogoSplashTechnique()).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startTextAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).duration(mConfigSplash.getAnimLogoSplashDuration()).playOn(mImgLogo);
    }


    public void startTextAnimation() {

        mTxtTitle.setText(mConfigSplash.getTitleSplash());
        mTxtTitle.setTextSize(mConfigSplash.getTitleTextSize());
        mTxtTitle.setTextColor(getResources().getColor(mConfigSplash.getTitleTextColor()));
        if (!mConfigSplash.getTitleFont().isEmpty())
            setFont(mConfigSplash.getTitleFont());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.flCentral);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mTxtTitle.setLayoutParams(params);
        mTxtTitle.setVisibility(View.VISIBLE);

        YoYo.with(mConfigSplash.getAnimTitleTechnique()).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationsFinished();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).duration(mConfigSplash.getAnimTitleDuration()).playOn(mTxtTitle);
    }


    public void setFont(String font) {
        Typeface type = Typeface.createFromAsset(getAssets(), font);
        mTxtTitle.setTypeface(type);
    }

    public abstract void initSplash(ConfigSplash configSplash);

    public abstract void animationsFinished();
}
