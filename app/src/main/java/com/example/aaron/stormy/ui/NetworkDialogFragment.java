package com.example.aaron.stormy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.aaron.stormy.R;

public class NetworkDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.network_error_title)
                .setMessage(R.string.network_unavailable_message)
                .setPositiveButton(R.string.error_ok_button_text, null);

        AlertDialog dialog = builder.create();

        return dialog;
    }
}
