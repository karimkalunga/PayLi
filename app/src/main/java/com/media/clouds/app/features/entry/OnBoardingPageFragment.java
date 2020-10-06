package com.media.clouds.app.features.entry;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.media.clouds.app.R;
import com.media.clouds.app.databinding.OnBoardingTaglineLayoutBinding;
import com.media.clouds.app.utils.DataPasser;
import com.media.clouds.app.utils.KeyConstants;

/**
 * Creates fragment views and attach to main UI.
 */
public class OnBoardingPageFragment extends Fragment {

    private int[] ICONS;
    private String[] TITLES;
    private DataPasser passer;
    private String[] SUBTITLES;
    public static final String SIGN_UP_CLICKED = "sign_up_clicked";
    public static final String GET_STARTED_CLICKED = "get_started_clicked";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initializeData(context);
        passer = (DataPasser) context;
    }

    /**
     * Initialize fragment data.
     * @param context application context.
     */
    private void initializeData(Context context) {
        ICONS = new int[] {
                R.mipmap.ic_logo_icon,
                R.drawable.ic_baseline_play_download,
                R.drawable.ic_baseline_sign_up
        };
        TITLES = new String[] {
                context.getString(R.string.title_onboarding_welcome),
                context.getString(R.string.title_onboarding_buy),
                context.getString(R.string.title_onboarding_account)
        };
        SUBTITLES = new String[] {
                context.getString(R.string.subtitle_onboarding_welcome),
                context.getString(R.string.subtitle_onboarding_buy),
                context.getString(R.string.subtitle_onboarding_account)
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        OnBoardingTaglineLayoutBinding binding =
                OnBoardingTaglineLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int position = 0;
        if (getArguments() != null) {
            position = getArguments().getInt(KeyConstants.POSITION);
        }
        OnBoardingTaglineLayoutBinding tagLineView = OnBoardingTaglineLayoutBinding.bind(view);
        bindDataToUI(tagLineView, position);
    }

    /**
     * Sets data to tag line view.
     * @param view tagLine view binding.
     * @param position view pager adapter position.
     */
    private void bindDataToUI(OnBoardingTaglineLayoutBinding view, int position) {
        if (position == (OnBoard.NUM_PAGES - 1)) {
            showButtons(view);
        } else {
            hideButtons(view);
        }
        view.icon.setImageResource(ICONS[position]);
        view.title.setText(TITLES[position]);
        view.subtitle.setText(SUBTITLES[position]);
    }

    /**
     * Hides sign up and and get-started button from main UI.
     * @param view on-boarding layout view.
     */
    private void hideButtons(OnBoardingTaglineLayoutBinding view) {
        LinearLayout buttonContainer = view.buttonContainer;
        if (buttonContainer.getVisibility() == View.VISIBLE) {
            buttonContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Shows sign up and get-started button from main UI.
     * @param view on-boarding layout view.
     */
    private void showButtons(OnBoardingTaglineLayoutBinding view) {
        LinearLayout buttonContainer = view.buttonContainer;

        if (buttonContainer.getVisibility() == View.GONE) {
            buttonContainer.setVisibility(View.VISIBLE);
            view.signUpButton.setOnClickListener(v -> {
                try {
                    passer.notifyDataPassed(SIGN_UP_CLICKED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            view.signInButton.setOnClickListener(v -> {
                try {
                    passer.notifyDataPassed(GET_STARTED_CLICKED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}