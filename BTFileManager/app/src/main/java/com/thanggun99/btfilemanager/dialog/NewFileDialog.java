package com.thanggun99.btfilemanager.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.thanggun99.btfilemanager.R;
import com.thanggun99.btfilemanager.interfaces.Refresh;

import java.io.File;
import java.io.IOException;

import static android.widget.Toast.makeText;

/**
 * Created by Thanggun99 on 09/10/2016.
 */

public class NewFileDialog extends AlertDialog.Builder{
    private String c;
/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New File");
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        setCancelable(true);

    }*/

    public NewFileDialog(final Context context, final String c) {
        super(context);
        this.c = c;
        setTitle("New File");
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.dialog_create, null);
        final EditText edt =(EditText) v.findViewById(R.id.edt_name);

        setView(v);
        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File f = new File(c +"/" + edt.getText().toString());
                try {
                    if (f.createNewFile()) {
                        makeText(getContext(), "Tạo file mới thành công", Toast.LENGTH_SHORT).show();
                        ((Refresh)context).onRefresh();
                    }

                    else
                        makeText(getContext(), "Không thể tạo file mới", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

    }
}
