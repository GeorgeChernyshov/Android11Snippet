#include <jni.h>
#include <string>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <android/imagedecoder.h>
#include <android/bitmap.h> // For AndroidBitmap_getInfo, AndroidBitmap_lockPixels etc.
#include <memory>
#include <sys/system_properties.h>

#define TAG "NdkImageDecoder"

// Helper to get system property, e.g., for API level
// (Alternatively, pass API level from Java)
static int get_android_api_level() {
    char sdk_version_str[PROP_VALUE_MAX];
    __system_property_get("ro.build.version.sdk", sdk_version_str);
    return atoi(sdk_version_str);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_post30_ui_screen_newfeatures_graphics_NDKImageLoader_decodeImageFromAsset(
        JNIEnv *env,
        jobject thiz,
        jobject asset_manager,
        jstring asset_file_name,
        jobject bitmap) {

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Native decodeImageFromAsset called.");

    // Check API level at runtime
    if (get_android_api_level() < 30) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "AImageDecoder requires API level 30 or higher. Current API: %d", get_android_api_level());
        return JNI_FALSE;
    }

    // 1. Get AAssetManager from Java AssetManager
    AAssetManager* mgr = AAssetManager_fromJava(env, asset_manager);
    if (mgr == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not get AAssetManager.");
        return JNI_FALSE;
    }

    // 2. Open the asset file
    const char* filename = env->GetStringUTFChars(asset_file_name, 0);
    AAsset* asset = AAssetManager_open(mgr, filename, AASSET_MODE_BUFFER);
    env->ReleaseStringUTFChars(asset_file_name, filename);

    if (asset == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not open asset: %s", filename);
        return JNI_FALSE;
    }

    // 3. Get asset buffer and length
    off_t bufferSize = AAsset_getLength(asset);
    const void* assetBuffer = AAsset_getBuffer(asset);

    if (assetBuffer == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not get asset buffer.");
        AAsset_close(asset);
        return JNI_FALSE;
    }

    // 4. Create an ImageDecoder from the asset buffer
    AImageDecoder* decoder;
    int result = AImageDecoder_createFromAAsset(asset, &decoder);
    if (result != ANDROID_IMAGE_DECODER_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "ImageDecoder::Create failed (unsupported format or data?).");
        AAsset_close(asset);
        return JNI_FALSE;
    }

    // 5. Get info about the image to be decoded
    const AImageDecoderHeaderInfo* headerInfo = AImageDecoder_getHeaderInfo(decoder);
    int32_t imageWidth = AImageDecoderHeaderInfo_getWidth(headerInfo);
    int32_t imageHeight = AImageDecoderHeaderInfo_getHeight(headerInfo);
    AndroidBitmapFormat imageFormat = (AndroidBitmapFormat) AImageDecoderHeaderInfo_getAndroidBitmapFormat(headerInfo);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Image Header Info: W=%d, H=%d, Format=%d", imageWidth, imageHeight, imageFormat);

    // 6. Get info about the target Bitmap passed from Java
    AndroidBitmapInfo bitmapInfo;
    if (AndroidBitmap_getInfo(env, bitmap, &bitmapInfo) != ANDROID_BITMAP_RESULT_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not get AndroidBitmapInfo from target bitmap.");
        AImageDecoder_delete(decoder);
        AAsset_close(asset);
        return JNI_FALSE;
    }
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Target Bitmap Info: W=%d, H=%d, Format=%d, Stride=%d, Flags=%d",
                        bitmapInfo.width, bitmapInfo.height, bitmapInfo.format, bitmapInfo.stride, bitmapInfo.flags);

    // 7. Check if the target Bitmap is mutable and compatible
//    if (!(bitmapInfo.flags & ANDROID_BITMAP_FLAGS_IS_MUTABLE)) {
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Target Bitmap is not mutable.");
//        AImageDecoder_delete(decoder);
//        AAsset_close(asset);
//        return JNI_FALSE;
//    }
    // Also, check if the target bitmap dimensions match or are sufficient
    if (bitmapInfo.width < imageWidth || bitmapInfo.height < imageHeight) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Target Bitmap dimensions (%dx%d) are smaller than image dimensions (%dx%d).",
                            bitmapInfo.width, bitmapInfo.height, imageWidth, imageHeight);
        // Depending on requirements, one might resize the Java Bitmap here or return an error
        AImageDecoder_delete(decoder);
        AAsset_close(asset);
        return JNI_FALSE;
    }

    // 8. Lock the pixel buffer of the target Bitmap
    void* bitmapPixels;
    if (AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels) != ANDROID_BITMAP_RESULT_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not lock pixels of target bitmap.");
        AImageDecoder_delete(decoder);
        AAsset_close(asset);
        return JNI_FALSE;
    }

    // 9. Decode the image directly into the locked pixel buffer
    // Use the target bitmap's stride and total buffer size
    size_t targetStride = bitmapInfo.stride;
    size_t targetBufferSize = bitmapInfo.height * bitmapInfo.stride;
    
    result = AImageDecoder_decodeImage(decoder, bitmapPixels, targetStride, targetBufferSize);
    if (result != ANDROID_IMAGE_DECODER_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "ImageDecoder::decode failed.");
        AndroidBitmap_unlockPixels(env, bitmap); // Ensure pixels are unlocked
        AImageDecoder_delete(decoder);
        AAsset_close(asset);
        return JNI_FALSE;
    }

    // 10. Unlock the pixel buffer
    if (AndroidBitmap_unlockPixels(env, bitmap) != ANDROID_BITMAP_RESULT_SUCCESS) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Could not unlock pixels of target bitmap.");
        AImageDecoder_delete(decoder);
        AAsset_close(asset);
        return JNI_FALSE;
    }

    // We’re done with the decoder, so now it’s safe to delete it.
    AImageDecoder_delete(decoder);

    // The decoder is no longer accessing the AAsset, so it is safe to
    // close it.
    AAsset_close(asset);

    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Image decoded successfully into passed Bitmap.");

    return JNI_TRUE;
}