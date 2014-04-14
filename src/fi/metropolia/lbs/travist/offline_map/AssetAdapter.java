package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import travist.pack.R;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class AssetAdapter {
	private String TAG;
	private Context mContext;
	
	public AssetAdapter(Context context) {
		mContext = context;
		
		// logger tag
		TAG = context.getResources().getString(R.string.debug_tag);
	}
	
	/**
	 * TODO... apache commons has IOUtils to use for file conversion
	 * 
	 * Handles a problem found with using AssetManager and 
	 * MapView (from mapsforge), that as MapView requires
	 * a File and AssetManager only provides a FileInputerStream,
	 * this will copy that file to the sd card for to use it
	 * as a File.
	 */
	public File getFileFromAssetName(String fileName) {
		Log.d(TAG, getClass().getSimpleName() + ": convertAsset()");
		/*
		 import sun.misc.IOUtils;
		 new FileOutputStream("path/to/file").write(
		 IOUtils.readFully(inputStream, -1, false));		 
		 */
		AssetManager assetManager = mContext.getAssets();
		InputStream inputStream;
		File mapFile = null;
		try {
			inputStream = assetManager.open(fileName);
			mapFile = createFileFromInputStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, getClass().getSimpleName() + ": Returning map file");
		return mapFile;	
	}
	
	/**
	 * Creates a tempfile from an inputStream.
	 * 
	 * @param inputStream
	 * @return File
	 */
	private File createFileFromInputStream(InputStream inputStream) {
		Log.d(TAG, getClass().getSimpleName() + ": creating file..");
		File outputFile = null;
		try {
			// temp file
			File outputDir = mContext.getCacheDir();
			outputFile = File.createTempFile("istanbul", "map", outputDir);
			OutputStream outputStream = new FileOutputStream(outputFile);
			
			byte buffer[] = new byte[1024];
			int length = 0;
			
			while ( (length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
			
			outputStream.close();
			inputStream.close();
			
			
		} catch (IOException e) {
			// TODO file not found Toast-msg
			Log.d(TAG, getClass().getSimpleName() + ": File creation failed");
			e.printStackTrace();
		}
		
		Log.d(TAG, getClass().getSimpleName() + ": File creation succeeded");
		return outputFile;
	}
}
