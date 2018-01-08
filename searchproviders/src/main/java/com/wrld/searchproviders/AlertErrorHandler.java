package com.wrld.searchproviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

public class AlertErrorHandler implements ErrorHandler {

    private Context m_context;

    public AlertErrorHandler(Context context){
        m_context = context;
    }

    @Override
    public void handleError(int titleResourceId, int descriptionResourceId) {
        handleError(m_context.getString(titleResourceId), m_context.getString(descriptionResourceId));
    }

    public void handleError(String title, String description) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(m_context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(m_context);
        }
        builder.setTitle(title)
                .setMessage(description)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .show();
    }
}
