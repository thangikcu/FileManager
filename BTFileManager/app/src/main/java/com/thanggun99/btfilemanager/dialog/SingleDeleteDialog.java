package com.thanggun99.btfilemanager.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.thanggun99.btfilemanager.interfaces.Refresh;
import com.thanggun99.btfilemanager.model.Item;

import java.io.File;

/**
 * Created by Thanggun99 on 12/10/2016.
 */

public class SingleDeleteDialog extends AlertDialog.Builder {
    private boolean d = true;

    public SingleDeleteDialog(final Context context, final Item i) {
        super(context);
        setIcon(i.getImage());
        setTitle("Do you want delete " + i.getName() + " ?");
        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File f = new File(i.getPath());
                deleteFile(f);
                ((Refresh)context).onRefresh();
            }
        });
        setNegativeButton("Cancel", null);

    }



    public void deleteFile(File f) {
        File[] files = f.listFiles();
        if (files != null && files.length != 0) {
            for (File chiFile : files) {
                if (chiFile.isDirectory()) {
                    deleteFile(chiFile);
                } else d = chiFile.delete() ? true : false;
            }
        }
        d = f.delete() ? true : false;
        Toast.makeText(getContext(), d == true ? "Xóa thành công": "Không xóa được", Toast.LENGTH_SHORT).show();
    }

}
