package io.happie.cordovaCamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.File;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

// import main.java.com.mindscapehq.android.raygun4android.RaygunClient;


class HappieCameraThumb {

    boolean createThumbAtPathWithName(String name, String user, String jnid, Context context) throws java.io.IOException {
        FileOutputStream fosThumb = null;
        try {
            File fullFile;
            File thumbFile;
            if (user.length() > 0 && jnid.length() > 0) {
                fullFile = new File(context.getFilesDir() + "/media/" + user + "/" + jnid, name);
                thumbFile = new File(context.getFilesDir() + "/media/" + user + "/" + jnid + "/thumb", name);
            } else {
                fullFile = new File(context.getFilesDir() + "/mainCache", name);
                thumbFile = new File(context.getFilesDir() + "/mainCache", name + "_thumb");
            }

            byte[] imageBytes = org.apache.commons.io.FileUtils.readFileToByteArray(fullFile);
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length), 400, 400);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ThumbImage.compress(Bitmap.CompressFormat.JPEG, 85, stream);
            byte[] thumbnailByteArray = stream.toByteArray();

            fosThumb = new FileOutputStream(thumbFile);
            fosThumb.write(thumbnailByteArray);

            ExifInterface exif = new ExifInterface(fullFile.getAbsolutePath());
            String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

            ExifInterface exifThumb = new ExifInterface(thumbFile.getAbsolutePath());
            exifThumb.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
            exifThumb.saveAttributes();
        } catch (Exception e) {
            // RaygunClient.send(e);
            return false;
        } finally {
            try {
                fosThumb.close();
            } catch (NullPointerException e) {
                // RaygunClient.send(e);
            }
        }
        return true;
    }

    Bitmap createThumbOfImage(File file, byte[] imageData, int degrees) {
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeByteArray(imageData, 0, imageData.length), 400, 400);

        Matrix matrix = new Matrix();
        if(degrees == 0 || degrees == 180){
            matrix.postRotate(degrees+90);
        }else {
            matrix.postRotate(degrees-90);
        }

        Bitmap orientedBmp = Bitmap.createBitmap(ThumbImage, 0, 0, ThumbImage.getWidth(), ThumbImage.getHeight(), matrix, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        orientedBmp.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        byte[] thumbnailByteArray = stream.toByteArray();

        FileOutputStream fosThumb = null;
        try {
            fosThumb = new FileOutputStream(file);
            fosThumb.write(thumbnailByteArray);
        } catch (Exception e) {
            // RaygunClient.send(e);
        }
        finally {
            try{
                fosThumb.close();
            }
            catch (Exception e){
                // RaygunClient.send(e);
            }
        }
        return orientedBmp;
    }
}