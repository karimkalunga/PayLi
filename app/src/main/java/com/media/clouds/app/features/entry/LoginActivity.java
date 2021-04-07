package com.media.clouds.app.features.entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.LoginLayoutBinding;
import com.media.clouds.app.network.INetworkStatus;
import com.media.clouds.app.network.NetworkRequest;
import com.media.clouds.app.utils.DialogImpl;
import com.media.clouds.app.utils.KeyConstants;
import com.media.clouds.app.utils.URLConstants;
import com.media.clouds.app.utils.Validator;

import org.json.JSONObject;

public class LoginActivity extends Activity implements INetworkStatus {

    private Preferences prefs;
    private LoginLayoutBinding binding;
    private String userPhoneNumber = "";
    public static final String RESET_PWD_FLAG = "reset_password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Preferences.getInstance(this);
        boolean isLoggedIn = prefs.getLoginStatus();
        boolean hasRememberedLogin = prefs.getRememberMeStatus();

        if (isLoggedIn && hasRememberedLogin) {
            goToHome();
        }
        binding = LoginLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initUI();
    }

    /**
     * Initializes UI components such as setting listeners,
     * decorating view etc.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        SwitchMaterial rememberMeSwitch = binding.persistEntryButton;
        rememberMeSwitch.setChecked(prefs.getRememberMeStatus());
        rememberMeSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> prefs.setRememberMeStatus(isChecked));

        binding.countryCodePicker
                .registerPhoneNumberTextView(binding.phoneNumber);

        TextView signUp = binding.signUpButton;
        signUp.setPaintFlags(signUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signUp.setOnClickListener(v -> goToRegistration(false));

        TextView forgotPwd = binding.resetPwdButton;
        forgotPwd.setPaintFlags(forgotPwd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPwd.setOnClickListener(v -> goToRegistration(true));

        binding.password.setOnTouchListener(new EdtDrawableTouchListener());
        binding.loginButton.setOnClickListener(v -> handleSignIn());
    }

    /**
     * Finish current activity, switch to registration activity
     * with reset password flag added.
     */
    private void goToRegistration(boolean hasResetPwdFlag) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra(RESET_PWD_FLAG, hasResetPwdFlag);
        startActivity(intent);
    }

    /**
     * Finish current activity, then switch to home activity.
     */
    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Handles Sign In network request.
     * @param params Sign In parameters.
     */
    private void signIn(JSONObject params) {
        NetworkRequest request = new NetworkRequest(this, getBaseContext());
        request.call("login", URLConstants.LOGIN_URL, params);
    }

    /**
     * Handles login button clicks.
     */
    private void handleSignIn() {
        try {
            userPhoneNumber = CountryCodePickerImpl.getInstance()
                    .getPhoneNumberFromCPP(binding.countryCodePicker.getPhoneNumber());
            JSONObject params = new JSONObject();
            params.put(KeyConstants.PHONE, userPhoneNumber);
            params.put(KeyConstants.PASSWORD, binding.password.getText().toString());
            params.put(KeyConstants.FCMTOKEN, prefs.getFCMTokenId());

            Validator validator = Validator.getInstance();
            boolean isValid = validator.isEntryLoginValid(params);

            if (isValid) {
                DialogImpl.getInstance(this)
                        .showProgressDialog(getString(R.string.please_wait), false);
                signIn(params);
            } else {
                DialogImpl.getInstance(this)
                        .showAlertDialog(getString(R.string.invalid_login_params));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifySuccess(String tag, String response) {
        DialogImpl dialog = DialogImpl.getInstance(this);
        dialog.dismissProgressDialog();
        try {
            JSONObject rsp = new JSONObject(response);
            String statusOrId = rsp.getString(KeyConstants.STATUS);

            if (!statusOrId.equalsIgnoreCase("false")) {
                prefs.setUserId(statusOrId);
                prefs.setLoginStatus(true);
                prefs.setPhoneNumber(userPhoneNumber);
                goToHome();
            } else {
                dialog.showAlertDialog(getString(R.string.sig_in_failed));
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
}
