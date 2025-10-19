package com.pattlebass.godotdirectoryaccessgranter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class AndroidDirectoryAccessGranter extends org.godotengine.godot.plugin.GodotPlugin {
    private static final String TAG = "godot";
    private Activity activity;

    private static final int OPEN_FILE = 0;

    public AndroidDirectoryAccessGranter(Godot godot) {
        super(godot);
        activity = godot.getActivity();
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "AndroidDirectoryAccessGranter";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("directory_access_granted", String.class));
        return signals;
    }

    @UsedByGodot
    public void openDirectory(String pathToLoad) {
        Intent chooseDirectory = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //chooseFile.setType(type.isEmpty() ? "*/*" : type);
        //chooseDirectory = Intent.createChooser(chooseDirectory, "Choose a project");
        if(pathToLoad != null && !pathToLoad.isEmpty() && Uri.parse(pathToLoad) != null) {
            chooseDirectory.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pathToLoad);
        }
        activity.startActivityForResult(chooseDirectory, OPEN_FILE); // TODO
    }

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_FILE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the directory that
            // the user selected.
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    Log.d(TAG, "Picked folder with URI: " + uri.getPath());
                    emitSignal("directory_access_granted", uri.getPath());
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // From https://stackoverflow.com/questions/65447194/how-to-convert-uri-to-file-android-10

    /*
    public static File getFile(Context context, Uri uri) throws IOException {
        String directoryName = context.getFilesDir().getPath() + File.separatorChar + "_temp";
        File directory = new File(directoryName);
        if (! directory.exists()){
            directory.mkdir();
        }
        File destinationFilename = new File(directoryName + File.separatorChar + queryName(context, uri));
        try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
        return destinationFilename;
    }

    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String queryName(Context context, Uri uri) {
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
    */
}
