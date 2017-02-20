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

import static android.widget.Toast.makeText;

/**
 * Created by Thanggun99 on 09/10/2016.
 */

public class NewFolderDialog extends AlertDialog.Builder {
    private String c;



    public NewFolderDialog(final Context context, final String c) {
        super(context);
        this.c = c;
        setTitle("New Folder");
        LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.dialog_create, null);
        final EditText edt =(EditText) v.findViewById(R.id.edt_name);

        setView(v);
        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File f = new File(c +"/" + edt.getText().toString());
                if(f.exists()){
                    Toast.makeText(getContext(), "Thư mục đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    if (f.mkdirs()){
                        makeText(getContext(), "Tạo thư mục mới thành công", Toast.LENGTH_SHORT).show();
                        ((Refresh)context).onRefresh();
                    }
                    else
                        makeText(getContext(), "Không thể tạo thư mục mới", Toast.LENGTH_SHORT).show();
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
