package com.thanggun99.btfilemanager.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.thanggun99.btfilemanager.interfaces.Refresh;
import com.thanggun99.btfilemanager.model.Item;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Thanggun99 on 13/10/2016.
 */

public class MultiDeleteDialog extends AlertDialog.Builder {
    boolean d = true;

    public MultiDeleteDialog(final Context context, final ArrayList<Item> items) {
        super(context);
        setTitle("Do you want delete " + items.size() + " files ?");
        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Item item : items) {
                    File f = new File(item.getPath());
                    deleteFile(f);
                }
                ((Refresh)context).onRefresh();
                Toast.makeText(getContext(), d == true ? "Xóa thành công" : "Không xóa được một số file", Toast.LENGTH_SHORT).show();

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
    }
}
