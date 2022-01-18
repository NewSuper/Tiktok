package com.aitd.module_chat.utils.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;

import com.aitd.module_chat.pojo.LocalMedia;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
public class MediaUtil {

    /**
     * 获取视频第一帧
     *
     * @param path
     * @return
     */
    public static Bitmap getVideoFirstFrame(String path) {

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();

    }

    public static Bitmap fileToBitmap(Context context, Uri uri) {
        try {
            if (uri.toString().startsWith("file://")) {
                InputStream is = context.getContentResolver().openInputStream(uri);
                return BitmapFactory.decodeStream(is);
            }
            return BitmapFactory.decodeFile(new File(uri.toString()).getPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取音频文件的总时长
     *
     * @param filePath 音频文件路径
     * @return 返回时长（秒）
     */
    public static int getMediaFileDuration(String filePath) {
        int mediaPlayerDuration = 0;
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        return mediaPlayerDuration / 1000;
    }

    public static LocalMedia toLocalMedia(Context context, Uri uri) {
        try {
            File file;
            if (uri.toString().startsWith("file://") || uri.toString().startsWith("content://")) {
                file = new File(FilePathUtil.getPath(context, uri));
            } else {
                file = new File(uri.toString());
            }

            if (file.exists()) {
                LocalMedia media = new LocalMedia();
                media.setName(file.getName());
                media.setPath(file.getAbsolutePath());
                media.setMimeType(FileUtil.getSuffixName(file.getAbsolutePath()));
                media.setSize(file.length());
                media.setMediaType(getMediaType(media.getMimeType()));
                if((media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_AUDIO) ||
                        (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_VIDEO)) {
                    media.setDuration(getMediaFileDuration(file.getAbsolutePath()));
                }

                Bitmap bitmap = null;
                int duration = 0;
                if (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_IMAGE) {
                    bitmap = fileToBitmap(context, uri);
                } else if (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_VIDEO) {
                    bitmap = getVideoFirstFrame(file.getAbsolutePath());
                    duration = getMediaFileDuration(file.getAbsolutePath());
                } else if (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_AUDIO) {
                    duration = getMediaFileDuration(file.getAbsolutePath());
                }
                if (bitmap != null) {
                    media.setWidth(bitmap.getWidth());
                    media.setHeight(bitmap.getHeight());
                    bitmap.recycle();
                }
                media.setDuration(duration);
                return media;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<LocalMedia> toLocalMedia(Context context, ArrayList<Uri> list) {
        ArrayList<LocalMedia> mediaList = new ArrayList<>();
        try {
            for (Uri uri : list) {
                String path = FilePathUtil.getPath(context, uri);
                File file = new File(path);
                if (file.exists()) {
                    LocalMedia media = new LocalMedia();
                    media.setName(file.getName());
                    media.setPath(file.getAbsolutePath());
                    media.setMimeType(FileUtil.getSuffixName(file.getAbsolutePath()));
                    media.setSize(file.length());
                    media.setMediaType(getMediaType(media.getMimeType()));
                    if((media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_AUDIO) ||
                            (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_VIDEO)) {
                        media.setDuration(getMediaFileDuration(file.getAbsolutePath()));
                    }
                    Bitmap bitmap = null;
                    int duration = 0;
                    if (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_IMAGE) {
                        bitmap = fileToBitmap(context, uri);
                    } else if (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_VIDEO) {
                        bitmap = getVideoFirstFrame(file.getAbsolutePath());
                        duration = getMediaFileDuration(file.getAbsolutePath());
                    } else if (media.getMediaType() == LocalMedia.MediaType.MEDIA_TYPE_AUDIO) {
                        duration = getMediaFileDuration(file.getAbsolutePath());
                    }
                    if (bitmap != null) {
                        media.setWidth(bitmap.getWidth());
                        media.setHeight(bitmap.getHeight());
                        bitmap.recycle();
                    }
                    media.setDuration(duration);
                    mediaList.add(media);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaList;
    }

    public static int getMediaType(String mimeType) {
        if (imageTypeList.contains(mimeType)) {
            return LocalMedia.MediaType.MEDIA_TYPE_IMAGE;
        } else if (videoTypeList.contains(mimeType)) {
            return LocalMedia.MediaType.MEDIA_TYPE_VIDEO;
        } else if (audioTypeList.contains(mimeType)) {
            return LocalMedia.MediaType.MEDIA_TYPE_AUDIO;
        }
        return LocalMedia.MediaType.MEDIA_TYPE_UNKNOWN;
    }

    private static ArrayList<String> audioTypeList = new ArrayList<>();
    private static ArrayList<String> imageTypeList = new ArrayList<>();
    private static ArrayList<String> videoTypeList = new ArrayList<>();

    static {
        imageTypeList.add(".png");
        imageTypeList.add(".PNG");
        imageTypeList.add(".jpg");
        imageTypeList.add(".JPG");
        imageTypeList.add(".jpeg");
        imageTypeList.add(".JPEG");
        imageTypeList.add(".gif");
        imageTypeList.add(".webp");
        imageTypeList.add(".svg");
        imageTypeList.add(".bmp");

        videoTypeList.add(".mp4");
        videoTypeList.add(".avi");
        videoTypeList.add(".3gp");
        videoTypeList.add(".flv");
        videoTypeList.add(".rm");
        videoTypeList.add(".rmvb");
        videoTypeList.add(".mov");
        videoTypeList.add(".mpeg");
        videoTypeList.add(".wmv");
        videoTypeList.add(".mkv");
        videoTypeList.add(".f4v");

        audioTypeList.add(".mp3");
        audioTypeList.add(".wav");
        audioTypeList.add(".wma");
        audioTypeList.add(".md");
        audioTypeList.add(".mp3");
        audioTypeList.add(".ogg");
        audioTypeList.add(".ape");
        audioTypeList.add(".aac");
        audioTypeList.add(".flac");
    }
}