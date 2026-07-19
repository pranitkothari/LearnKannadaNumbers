cmake_minimum_required(VERSION 3.22.1)
project(kannada_numbers_native)

# Vendored espeak-ng (see espeak-ng/README.md for provenance). Expected layout:
#   app/src/main/cpp/espeak-ng/include/espeak-ng/speak_lib.h
#   app/src/main/cpp/espeak-ng/lib/${ANDROID_ABI}/libespeak-ng.so
set(ESPEAK_NG_DIR ${CMAKE_CURRENT_SOURCE_DIR}/espeak-ng)

add_library(espeak-ng SHARED IMPORTED)
set_target_properties(espeak-ng PROPERTIES
    IMPORTED_LOCATION ${ESPEAK_NG_DIR}/lib/${ANDROID_ABI}/libespeak-ng.so
)

add_library(espeak_bridge SHARED espeak_bridge.c)

target_include_directories(espeak_bridge PRIVATE
    ${ESPEAK_NG_DIR}/include
)

find_library(log-lib log)

target_link_libraries(espeak_bridge
    espeak-ng
    ${log-lib}
)
