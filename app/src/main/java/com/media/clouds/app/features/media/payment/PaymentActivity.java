package com.media.clouds.app.features.media.payment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.PaymentLayoutBinding;
import com.media.clouds.app.features.entry.CountryCodePickerImpl;
import com.media.clouds.app.network.INetworkStatus;
import com.media.clouds.app.network.NetworkRequest;
import com.media.clouds.app.utils.DialogImpl;
import com.media.clouds.app.utils.KeyConstants;
import com.media.clouds.app.utils.URLConstants;
import com.media.clouds.app.utils.Validator;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements INetworkStatus {

    private String contentTitle, contentPrice, bannerLink, authorName, contentId;
    private static final String MODE_MPESA = "01";
    private static final String MODE_TIGO_PESA = "02";
    private PaymentLayoutBinding binding;
    private String mode = MODE_MPESA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PaymentLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null) {
            contentTitle = getIntent().getStringExtra(KeyConstants.TITLE);
            contentPrice = getIntent().getStringExtra(KeyConstants.CONTENT_PRICE);
            bannerLink = getIntent().getStringExtra(KeyConstants.BANNER_LINK);
            authorName = getIntent().getStringExtra(KeyConstants.CREATOR_NAME);
            contentId = getIntent().getStringExtra(KeyConstants.CONTENT_ID);
        }

        initUI();
    }

    private void initUI() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        activateMPesa();
        binding.modeMpesa.setOnClickListener(v -> {
            setMode(MODE_MPESA);
            activateMPesa();
        });
        binding.modeTigopesa.setOnClickListener(v -> {
            setMode(MODE_TIGO_PESA);
            activateTigoPesa();
        });
        binding.purchaseButton.setOnClickListener(v -> initPurchase());
        binding.countryCodePicker.registerPhoneNumberTextView(binding.phoneNumber);

        bindDataToUI();
    }

    private void bindDataToUI() {
        try {
            String price = getString(R.string.currency).concat(" ").concat(contentPrice);
            binding.contentPrice.setText(price);
            binding.contentName.setText(contentTitle);
            binding.artistName.setText(authorName);
            Picasso.get()
                    .load(bannerLink)
                    .into(binding.contentImg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPurchase() {
        try {
            String phoneNumber = CountryCodePickerImpl.getInstance()
                    .getPhoneNumberFromCPP(binding.countryCodePicker.getPhoneNumber());
            JSONObject params = new JSONObject();
            params.put(KeyConstants.MNO, mode);
            params.put(KeyConstants.MSISDN, phoneNumber);
            params.put(KeyConstants.AMOUNT, contentPrice);
            params.put(KeyConstants.CONTENT_ID, contentId);
            params.put(KeyConstants.USER_ID, Preferences.getInstance(this).getUserId());

            Validator validator = Validator.getInstance();
            boolean isValid = validator.isContentPurchaseDataValid(params);

            if (isValid) {
                DialogImpl.getInstance(this)
                        .showProgressDialog(getString(R.string.please_wait), false);
                purchase(params);
            } else {
                DialogImpl.getInstance(this)
                        .showAlertDialog(getString(R.string.invalid_payment_params));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void purchase(JSONObject params) {
        NetworkRequest request = new NetworkRequest(this, getBaseContext());
        request.call("purchase_content", URLConstants.PAYMENT_URL, params);
    }

    private void activateMPesa() {
        binding.modeTigopesa.setSelected(false);
        binding.modeTigopesa.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        binding.modeMpesa.setSelected(true);
        binding.modeMpesa.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
    }

    private void activateTigoPesa() {
        binding.modeMpesa.setSelected(false);
        binding.modeMpesa.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        binding.modeTigopesa.setSelected(true);
        binding.modeTigopesa.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
    }

    private void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void notifySuccess(String tag, String response) {
        Log.d("XXX_PAYMENT_INFO", response);
    }

    @Override
    public void notifyError(String tag, VolleyError error) {
        DialogImpl dialog = DialogImpl.getInstance(this);
        dialog.dismissProgressDialog();
        dialog.showAlertDialog(getString(R.string.network_error));
    }
}
