package com.media.clouds.app.features.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.ProfileLayoutBinding;
import com.media.clouds.app.features.entry.LoginActivity;
import com.media.clouds.app.features.entry.RegistrationActivity;
import com.media.clouds.app.network.INetworkStatus;
import com.media.clouds.app.security.EncoDecode;
import com.media.clouds.app.utils.DialogImpl;
import com.media.clouds.app.utils.IDialog;
import com.media.clouds.app.utils.KeyConstants;
import com.squareup.picasso.Picasso;

import java.nio.charset.StandardCharsets;

public class ProfileActivity extends AppCompatActivity implements INetworkStatus {

    private ProfileLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        initUI();
    }

    /**
     * Binds data to UI components.
     */
    private void bindDataToUI() {
        String title = getString(R.string.my_profile);
        Preferences prefs = Preferences.getInstance(this);
        String phoneNumber = "+".concat(prefs.getPhoneNumber());

        binding.title.setText(title);
        binding.phoneNumberView.setText(phoneNumber);

        if (getIntent() != null && !getIntent().getBooleanExtra(KeyConstants.PROFILE_INFO_COUNT, false)) {
            byte[] profilePic = EncoDecode.getInstance(this).decodeBase64(prefs.getProfilePhotoUrl());
            String profileImgUrl = new String(profilePic, StandardCharsets.UTF_8);
            String fullName = prefs.getProfileName();
            String emailAddress =  prefs.getProfileEmail();
            String physicalAddress = prefs.getProfileAddress();

            binding.emailEdt.setText(fullName);
            binding.emailEdt.setText(emailAddress);
            binding.physicalAddressEdt.setText(physicalAddress);
            Picasso.get().load(profileImgUrl).into(binding.profileImg);
        }
    }

    /**
     * Handles update password button clicks.
     */
    private void handleUpdatePassword() {
        Intent intent = new Intent(getBaseContext(), RegistrationActivity.class);
        intent.putExtra(LoginActivity.RESET_PWD_FLAG, true);
        startActivity(intent);
    }

    /**
     * Handles logout button clicks. Sets login status to false,
     * and switch to login activity.
     */
    private void handleLogout() {
        IDialog iDialog = new IDialog() {
            @Override
            public void onClickPositiveButton(DialogInterface di, int which) {
                Preferences.getInstance(getBaseContext()).setLoginStatus(false);
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }

            @Override
            public void onClickNegativeButton(DialogInterface di, int which) {
                di.dismiss();
            }
        };
        DialogImpl.getInstance(this).showAlertDialog(
                getString(R.string.confirm_logout),
                getString(R.string.confirm),
                getString(R.string.close),
                iDialog);
    }

    /**
     * Initializes UI components such as setting listeners,
     * decorating view etc.
     */
    private void initUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        bindDataToUI();
        binding.updatePassword.setOnClickListener(v -> handleUpdatePassword());
        binding.logout.setOnClickListener(v -> handleLogout());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_close) {
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_stay);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void notifySuccess(String tag, String response) {

    }

    @Override
    public void notifyError(String tag, VolleyError error) {
        DialogImpl dialog = DialogImpl.getInstance(this);
        dialog.dismissProgressDialog();
        dialog.showAlertDialog(getString(R.string.network_error));
    }
}
