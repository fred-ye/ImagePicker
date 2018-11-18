/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.fredye.imagepicker.util;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * MIME Type enumeration to restrict selectable media on the selection activity. Matisse only supports images and
 * videos.
 * <p>
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
@SuppressWarnings("unused")
public enum MimeType {

    // ============== images ==============
    JPEG("image/jpeg", arraySetOf(
            "jpg",
            "jpeg"
    )),
    PNG("image/png", arraySetOf(
            "png"
    )),
    GIF("image/gif", arraySetOf(
            "gif"
    )),
    BMP("image/x-ms-bmp", arraySetOf(
            "bmp"
    )),
    WEBP("image/webp", arraySetOf(
            "webp"
    ));



    private final String mMimeTypeName;
    private final Set<String> mExtensions;

    MimeType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public static Set<MimeType> ofAll() {
        return EnumSet.allOf(MimeType.class);
    }

    public static Set<MimeType> of(MimeType type, MimeType... rest) {
        return EnumSet.of(type, rest);
    }

    public static Set<MimeType> ofImage() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    private static Set<String> arraySetOf(String... suffixes) {
        HashSet<String> set = new HashSet<>();
        set.addAll(Arrays.asList(suffixes));
        return set;
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

//    public boolean checkType(ContentResolver resolver, Uri uri) {
//        MimeTypeMap map = MimeTypeMap.getSingleton();
//        if (uri == null) {
//            return false;
//        }
//        String type = map.getExtensionFromMimeType(resolver.getType(uri));
//        String path = null;
//        // lazy load the path and prevent resolve for multiple times
//        boolean pathParsed = false;
//        for (String extension : mExtensions) {
//            if (extension.equals(type)) {
//                return true;
//            }
//            if (!pathParsed) {
//                // we only resolve the path for one time
//                path = PhotoMetadataUtils.getPath(resolver, uri);
//                if (!TextUtils.isEmpty(path)) {
//                    path = path.toLowerCase(Locale.US);
//                }
//                pathParsed = true;
//            }
//            if (path != null && path.endsWith(extension)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
