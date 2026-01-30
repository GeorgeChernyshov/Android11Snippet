cmake_minimum_required(VERSION 3.4.1)

add_library(basic SHARED snippet.cpp)
add_library(ndk_image_decoder SHARED ndkImageDecoder.cpp)

target_link_libraries(
        basic
        android
        log)

target_link_libraries(
        ndk_image_decoder
        android
        log
        jnigraphics)