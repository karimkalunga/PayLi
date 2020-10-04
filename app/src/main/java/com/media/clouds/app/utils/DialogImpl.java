package com.media.clouds.app.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Dialog.class
 * This class holds various dialogs generic implementation.
 */
public class DialogImpl {

    private static DialogImpl instance = null;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;

    /**
     * Constructor.
     * @param context application context.
     */
    private DialogImpl(Context context) {
        progressDialog = new ProgressDialog(context);
        alertDialog = new AlertDialog.Builder(context);
    }

    /**
     * Singleton.
     * @param context application context.
     * @return dialog instance.
     */
    public static synchronized DialogImpl getInstance(Context context) {
        if (instance == null) {
            instance = new DialogImpl(context);
        }
        return instance;
    }

    /**
     * Closes dialog.
     */
    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Sets dialog properties and make it visible.
     * @param message shown message.
     * @param isCancellable cancellable status of the dialog.
     */
    public void showProgressDialog(String message, boolean isCancellable) {
        dismissProgressDialog();
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancellable);
        progressDialog.setCanceledOnTouchOutside(isCancellable);
        progressDialog.show();
    }

    /**
     * Instantiates alert dialog shared properties.
     * @param message dialog shown message.
     */
    private void getAlertDialogSharedProps(String message) {
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
    }

    /**
     * Creates and show alert dialog (with no positive button implementation).
     * @param message alert dialog message.
     */
    public void showAlertDialog(String message) {
        getAlertDialogSharedProps(message);
        alertDialog.show();
    }

    /**
     * Creates and show alert dialog (with no positive button implementation).
     * Has dialog title.
     *
     * @param message alert dialog message.
     */
    public void showAlertDialog(String message, String title) {
        getAlertDialogSharedProps(message);
        alertDialog.setTitle(title);
        alertDialog.show();
    }

    /**
     * Creates and show alert dialog (with positive and negative button implementation).
     *
     * @param message dialog message.
     * @param positiveButtonTitle title string.
     * @param negativeButtonTitle title string.
     * @param callback IDialog interface.
     */
    public void showAlertDialog(String message, String positiveButtonTitle,
                                String negativeButtonTitle,
                                IDialog callback) {
        getAlertDialogSharedProps(message);
        alertDialog.setNegativeButton(negativeButtonTitle, callback::onClickNegativeButton);
        alertDialog.setPositiveButton(positiveButtonTitle, callback::onClickPositiveButton);
        alertDialog.show();
    }

    /**
     * Creates and show alert dialog (with positive and negative button implementation).
     * Has dialog title.
     *
     * @param message dialog message.
     * @param title dialog title.
     * @param positiveButtonTitle title string.
     * @param negativeButtonTitle title string.
     * @param callback IDialog interface.
     */
    public void showAlertDialog(String message, String title,
                                String positiveButtonTitle,
                                String negativeButtonTitle,
                                IDialog callback) {
        getAlertDialogSharedProps(message);
        alertDialog.setTitle(title);
        alertDialog.setNegativeButton(negativeButtonTitle, callback::onClickNegativeButton);
        alertDialog.setPositiveButton(positiveButtonTitle, callback::onClickPositiveButton);
        alertDialog.show();
    }
}
