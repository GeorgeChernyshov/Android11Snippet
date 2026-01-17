#include <jni.h>
#include <string>

jstring native_get_string(JNIEnv* env) {
    std::string s = "Hellooooooooooooooo ";
    std::string_view sv = s + "World\n";

    // BUG: Use-after-free. `sv` holds a dangling reference to the ephemeral
    // string created by `s + "World\n"`. Accessing the data here is a
    // use-after-free.
    return env->NewStringUTF(sv.data());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_post30_ui_screen_newfeatures_gwpasan_TestGwpAsanHelper_testGwpAsan(
        JNIEnv* env,
        jobject thiz) {

    // Repeat the buggy code a few thousand times. GWP-ASan has a small chance
    // of detecting the use-after-free every time it happens. A single user who
    // triggers the use-after-free thousands of times will catch the bug once.
    // Alternatively, if a few thousand users each trigger the bug a single time,
    // you'll also get one report (this is the assumed model).
    jstring return_string;
    for (unsigned i = 0; i < 0x100; ++i) {
        return_string = native_get_string(env);
    }

    return reinterpret_cast<jstring>(env->NewGlobalRef(return_string));
}