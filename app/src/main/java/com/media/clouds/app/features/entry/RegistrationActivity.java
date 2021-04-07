package com.media.clouds.app.features.entry;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.VolleyError;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.RegistrationLayoutBinding;
import com.media.clouds.app.network.INetworkStatus;
import com.media.clouds.app.network.NetworkRequest;
import com.media.clouds.app.utils.DialogImpl;
import com.media.clouds.app.utils.IDialog;
import com.media.clouds.app.utils.KeyConstants;
import com.media.clouds.app.utils.URLConstants;
import com.media.clouds.app.utils.Validator;
import com.mukesh.OtpView;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity implements INetworkStatus {

    private RegistrationLayoutBinding binding;
    private boolean hasResetPwdFlag;
    private String phoneNumberGlobal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegistrationLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (getIntent() != null) {
            hasResetPwdFlag = getIntent().getBooleanExtra(
                    LoginActivity.RESET_PWD_FLAG, false);
        }
        initUI();
    }

    /**
     * Sets view title.
     * @param title view title text.
     */
    private void setPageTitle(String title) {
        binding.regTitle.setText(title);
    }

    /**
     * Sets registration button text.
     * @param buttonText button text.
     */
    private void setRegButtonText(String buttonText) {
        binding.regButton.setText(buttonText);
    }

    /**
     * Hides card view parent/container.
     */
    private void hideCardContainerView() {
        CardView cv = binding.cardContainer;
        if (cv.getVisibility() == View.VISIBLE) {
            cv.setVisibility(View.GONE);
        }
    }

    /**
     * Shows card view parent/container.
     */
    private void showCardContainerView() {
        CardView cv = binding.cardContainer;
        if (cv.getVisibility() == View.GONE) {
            cv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides country code picker field.
     */
    private void hideCountryCodePickerView() {
        CountryCodePicker picker = binding.countryCodePicker;
        if (picker.getVisibility() == View.VISIBLE) {
            picker.setVisibility(View.GONE);
        }
    }

    /**
     * Hides phone number EditText field.
     */
    private void hidePhoneNumberView() {
        EditText phoneNumberEdt = binding.phoneNumber;
        if (phoneNumberEdt.getVisibility() == View.VISIBLE) {
            phoneNumberEdt.setVisibility(View.GONE);
        }
    }

    /**
     * Show password EditText field.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showPasswordView() {
        EditText passwordEdt = binding.password;
        if (passwordEdt.getVisibility() == View.GONE) {
            passwordEdt.setVisibility(View.VISIBLE);
            passwordEdt.setOnTouchListener(new EdtDrawableTouchListener());
        }
    }

    /**
     * Hides phone number EditText field.
     */
    private void hideLegalView() {
        TextView legalView = binding.legal;
        if (legalView.getVisibility() == View.VISIBLE) {
            legalView.setVisibility(View.GONE);
        }
    }

    /**
     * Hides OTP view.
     */
    private void hideOtpView() {
        OtpView otpView  = binding.otpView;
        if (otpView.getVisibility() == View.VISIBLE) {
            otpView.setVisibility(View.GONE);
        }
    }

    /**
     * Shows OTP view.
     */
    private void showOtpView() {
        OtpView otpView  = binding.otpView;
        if (otpView.getVisibility() == View.GONE) {
            otpView.setVisibility(View.VISIBLE);
            otpView.requestFocusFromTouch();
        }
    }

    /**
     * Shows sub-title view.
     * @param prefix subtitle text.
     */
    private void showSubtitleView(String prefix) {
        TextView subtitleView  = binding.regSubTitle;
        if (subtitleView.getVisibility() == View.GONE) {
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setText(prefix.concat(" +").concat(phoneNumberGlobal));
        }
    }

    /**
     * Hides sub-title view.
     */
    private void hideSubtitleView() {
        TextView subtitleView  = binding.regSubTitle;
        if (subtitleView.getVisibility() == View.VISIBLE) {
            subtitleView.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes UI components such as setting listeners,
     * decorating view etc.
     */
    private void initUI() {
        if (hasResetPwdFlag) {
            setPageTitle(getString(R.string.title_reset_pwd));
            setRegButtonText(getString(R.string.verify));
        } else {
            setPageTitle(getString(R.string.title_create_account));
            setRegButtonText(getString(R.string.sign_up));
        }
        binding.countryCodePicker.registerPhoneNumberTextView(binding.phoneNumber);
        binding.regButton.setOnClickListener(v -> handleSignUp());
    }

    /**
     * Handles register button clicks.
     */
    private void handleSignUp() {
        try {
            String phoneNumber = CountryCodePickerImpl.getInstance()
                    .getPhoneNumberFromCPP(binding.countryCodePicker.getPhoneNumber());
            JSONObject params = new JSONObject();
            params.put(KeyConstants.PHONE, phoneNumber);

            Validator validator = Validator.getInstance();
            boolean isValid = validator.isEntryRegisterPhoneNumberValid(params);

            if (isValid) {
                phoneNumberGlobal = params.getString(KeyConstants.PHONE);
                DialogImpl.getInstance(this)
                        .showProgressDialog(getString(R.string.please_wait), false);
                signUp("sign_up_send_phone", params, 0);
            } else {
                DialogImpl.getInstance(this)
                        .showAlertDialog(getString(R.string.invalid_phone_number_param));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles Sign Up network request.
     * @param tag request type.
     * @param params Sign Up parameters.
     * @param step service number (step number in sign up process).
     */
    private void signUp(String tag, JSONObject params, int step) {
        String baseUrl = hasResetPwdFlag
                ? URLConstants.SIGN_UP_RESET_PWD_URL
                : URLConstants.SIGN_UP_URL;
        String url = baseUrl.concat(String.valueOf(step));
        NetworkRequest request = new NetworkRequest(this, getBaseContext());
        request.call(tag, url, params);
    }

    /**
     * Displays view for OTP verification.
     */
    private void displayOTPVerificationView() {
        setPageTitle(getString(R.string.verify));
        showSubtitleView(getString(R.string.verify_subtitle_prefix));
        hideCardContainerView();
        showOtpView();
        setRegButtonText(getString(R.string.verify));
        hideLegalView();
        binding.regButton.setOnClickListener(v -> handleOTPVerification());
    }

    /**
     * Handles verify button clicks.
     */
    private void handleOTPVerification() {
        try {
            JSONObject params = new JSONObject();
            params.put(KeyConstants.PHONE, phoneNumberGlobal);
            params.put(KeyConstants.CODE,
                    Objects.requireNonNull(binding.otpView.getText()).toString());

            Validator validator = Validator.getInstance();
            boolean isValid = validator.isEntryRegisterOTPValid(params);

            if (isValid) {
                DialogImpl.getInstance(this)
                        .showProgressDialog(getString(R.string.please_wait), false);
                signUp("sign_up_verify_otp", params, 1);
            } else {
                DialogImpl.getInstance(this)
                        .showAlertDialog(getString(R.string.invalid_phone_number_param));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show password view and hide all other views.
     */
    private void displayPasswordView() {
        if (hasResetPwdFlag) {
            setRegButtonText(getString(R.string.save_changes));
            setPageTitle(getString(R.string.title_reset_pwd));
        } else {
            setRegButtonText(getString(R.string.sign_up));
            setPageTitle(getString(R.string.finalize_sign_up));
        }
        hideSubtitleView();
        hideOtpView();
        showCardContainerView();
        hideCountryCodePickerView();
        hidePhoneNumberView();
        showPasswordView();
        binding.regButton.setOnClickListener(v -> finalizeRegistration());
    }

    /**
     * Handles finalize registration button clicks.
     */
    private void finalizeRegistration() {
        try {
            JSONObject params = new JSONObject();
            params.put(KeyConstants.PHONE, phoneNumberGlobal);
            params.put(KeyConstants.PASSWORD, binding.password.getText().toString());
            params.put(KeyConstants.FCMTOKEN, Preferences.getInstance(this).getFCMTokenId());

            Validator validator = Validator.getInstance();
            boolean isValid = validator.isEntryRegisterPasswordValid(params);

            if (isValid) {
                DialogImpl.getInstance(this)
                        .showProgressDialog(getString(R.string.please_wait), false);
                signUp("finalize_sign_up", params, 2);
            } else {
                DialogImpl.getInstance(this)
                        .showAlertDialog(getString(R.string.invalid_password_param));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Finish this activity and switch to home activity.
     */
    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles Sign Up (send phone to verify) response.
     * @param isSuccess true/false.
     */
    private void handleSignUpResponse(boolean isSuccess) {
        if (isSuccess) {
            displayOTPVerificationView();
        } else {
            DialogImpl.getInstance(this)
                    .showAlertDialog(getString(R.string.otp_send_fail));
        }
    }

    /**
     * Handles OTP verification response.
     * @param isSuccess true/false.
     */
    private void handleOTPVerificationResponse(boolean isSuccess) {
        if (isSuccess) {
            displayPasswordView();
        } else {
            DialogImpl.getInstance(this)
                    .showAlertDialog(getString(R.string.otp_verification_fail));
        }
    }

    /**
     * Handles finalize registration response.
     * @param statusOrId can either be false or userId.
     */
    private void handleFinalizeRegistrationResponse(String statusOrId) {
        DialogImpl dialog = DialogImpl.getInstance(this);

        if (statusOrId.equalsIgnoreCase("false")) {
            dialog.showAlertDialog(getString(R.string.network_error));
        } else {
            IDialog iDialog = new IDialog() {
                @Override
                public void onClickPositiveButton(DialogInterface di, int which) {
                    Preferences prefs = Preferences.getInstance(getBaseContext());
                    prefs.setUserId(statusOrId);
                    prefs.setLoginStatus(true);
                    prefs.setPhoneNumber(phoneNumberGlobal);
                    goToHome();
                }

                @Override
                public void onClickNegativeButton(DialogInterface di, int which) {
                    finishAffinity();
                }
            };
            String message = hasResetPwdFlag
                    ? getString(R.string.password_reset_success_feedback)
                    : getString(R.string.sign_up_success_feedback).concat(" ").concat(getString(R.string.app_name)).concat(".");
            dialog.showAlertDialog(message, getString(R.string.close), getString(R.string.go_to_home), iDialog);
        }
    }

    @Override
    public void notifySuccess(String tag, String response) {
        DialogImpl dialog = DialogImpl.getInstance(this);
        dialog.dismissProgressDialog();

        try {
            JSONObject rsp = new JSONObject(response);
            switch (tag) {
                case "sign_up_send_phone":
                    boolean isSuccess = rsp.getBoolean(KeyConstants.STATUS);
                    handleSignUpResponse(isSuccess);
                    break;
                case "sign_up_verify_otp":
                    isSuccess = rsp.getBoolean(KeyConstants.STATUS);
                    handleOTPVerificationResponse(isSuccess);
                    break;
                case "finalize_sign_up":
                    String statusOrId = rsp.getString(KeyConstants.STATUS);
                    handleFinalizeRegistrationResponse(statusOrId);
                    break;
            }
        } catch (Exception e) {
            dialog.showAlertDialog(getString(R.string.network_error));
        }
    }

    @Override
    public void notifyError(String tag, VolleyError error) {
        DialogImpl dialog = DialogImpl.getInstance(this);
        dialog.dismissProgressDialog();
        dialog.showAlertDialog(getString(R.string.network_error));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return true;
    }
}
