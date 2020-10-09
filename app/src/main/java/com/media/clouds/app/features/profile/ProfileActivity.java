package com.media.clouds.app.features.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.ProfileLayoutBinding;
import com.media.clouds.app.features.entry.LoginActivity;
import com.media.clouds.app.features.entry.RegistrationActivity;
import com.media.clouds.app.features.media.utils.ContentDataLayer;
import com.media.clouds.app.network.INetworkStatus;
import com.media.clouds.app.network.NetworkRequest;
import com.media.clouds.app.security.EncoDecode;
import com.media.clouds.app.utils.DialogImpl;
import com.media.clouds.app.utils.IDialog;
import com.media.clouds.app.utils.KeyConstants;
import com.media.clouds.app.utils.URLConstants;
import com.media.clouds.app.utils.Validator;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class ProfileActivity extends AppCompatActivity implements INetworkStatus {

    private ProfileLayoutBinding binding;
    private ContentDataLayer dataHolder;
    private Preferences prefs;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

        prefs = Preferences.getInstance(this);
        userId = prefs.getUserId();
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
            String profileImgUrl = prefs.getProfilePhotoUrl();
            if (profileImgUrl != null) {
                byte[] profilePic = EncoDecode.getInstance().decodeBase64(prefs.getProfilePhotoUrl());
                profileImgUrl = new String(profilePic, StandardCharsets.UTF_8);
                Picasso.get().load(profileImgUrl).into(binding.profileImg);
            }
            binding.nameEdt.setText(prefs.getProfileName());
            binding.emailEdt.setText(prefs.getProfileEmail());
            binding.physicalAddressEdt.setText(prefs.getProfileAddress());
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
        binding.profileImg.setOnClickListener(v -> handleUploadProfileImg());
        binding.nameEdt.addTextChangedListener(new TextChangeListener(KeyConstants.NAME));
        binding.emailEdt.addTextChangedListener(new TextChangeListener(KeyConstants.EMAIL));
        binding.physicalAddressEdt.addTextChangedListener(new TextChangeListener(KeyConstants.ADDRESS));
    }

    private void handleUploadProfileImg() {
        ImagePicker.Companion.with(this)
                .crop()
                .galleryOnly()
                .galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"})
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data != null) {
            File profileImg = ImagePicker.Companion.getFile(data);
            if (profileImg != null) {
                Bitmap bitmap = getBitmapImage(profileImg);
                binding.profileImg.setImageBitmap(bitmap);
                updateProfileImg(bitmap);
            } else {
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.error_failed_pick_gallery_image),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private void updateProfileImg(Bitmap bitmap) {
        byte[] imageBytes = getBytesFromBitmap(bitmap);
        String profileImgBase64 = EncoDecode.getInstance().encodeToBase64(imageBytes);
        try {
            JSONObject param = new JSONObject();
            param.put(KeyConstants.USER_ID, userId);
            param.put(KeyConstants.PHOTO, profileImgBase64);

            Validator validator = Validator.getInstance();
            boolean isValid = validator.isProfileImgContentValid(param);

            if (isValid) {
                showImgUploadLoader();
                updateProfileInfo(KeyConstants.PHOTO, param);
            } else {
                DialogImpl.getInstance(this)
                        .showAlertDialog(getString(R.string.network_error));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showImgUploadLoader() {
        FrameLayout loaderContainer = binding.uploadLoader;
        if (loaderContainer.getVisibility() == View.GONE) {
            loaderContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideImgUploadLoader() {
        FrameLayout loaderContainer = binding.uploadLoader;
        if (loaderContainer.getVisibility() == View.VISIBLE) {
            loaderContainer.setVisibility(View.GONE);
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private Bitmap getBitmapImage(File profileImg) {
        return BitmapFactory.decodeFile(profileImg.getAbsolutePath());
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
        hideImgUploadLoader();
        try {
            switch (tag) {
                case KeyConstants.PHOTO:
                    // todo: Save photo url from response data.
                    break;
                case KeyConstants.NAME:
                    prefs.setProfileName(dataHolder.getProfileName());
                    break;
                case KeyConstants.EMAIL:
                    prefs.setProfileEmail(dataHolder.getProfileEmail());
                    break;
                case KeyConstants.ADDRESS:
                    prefs.setProfileAddress(dataHolder.getProfilePhysicalAddress());
                    break;
            }
            prefs.setProfileInfoCount(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyError(String tag, VolleyError error) {
        hideImgUploadLoader();
        DialogImpl.getInstance(this).showAlertDialog(getString(R.string.network_error));
    }

    private class TextChangeListener implements TextWatcher {
        private String changedText;

        public TextChangeListener(String changedText) {
            this.changedText = changedText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String info = s.toString();
            if (info.length() >= 3) {
                try {
                    Validator validator = Validator.getInstance();
                    JSONObject param = new JSONObject();
                    param.put(KeyConstants.USER_ID, userId);

                    switch (changedText) {
                        case KeyConstants.NAME:
                            if (info.contains(" ")) {
                                param.put(KeyConstants.NAME, info);

                                if (validator.isUpdateNameContentValid(param)) {
                                    updateProfileInfo(changedText, param);
                                }
                            }
                            break;
                        case KeyConstants.EMAIL:
                            param.put(KeyConstants.EMAIL, info);

                            if (validator.isUpdateEmailContentValid(param)) {
                                updateProfileInfo(changedText, param);
                            }
                            break;
                        case KeyConstants.ADDRESS:

                            param.put(KeyConstants.ADDRESS, info);

                            if (validator.isUpdateAddressContentValid(param)) {
                                updateProfileInfo(changedText, param);
                            }
                            break;
                    }
                    dataHolder = ContentDataLayer.init(param.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void updateProfileInfo(String changedText, JSONObject param) {
        NetworkRequest request = new NetworkRequest(this, this);
        request.call(changedText, URLConstants.UPDATE_PROFILE_URL, param);
    }
}
