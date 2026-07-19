// Minimal JNI bridge around espeak-ng's synchronous synthesis API.
// Pairs with app/src/main/java/com/learnkannadanumbers/app/speech/KannadaTts.kt.
//
// espeak-ng itself (headers + prebuilt libespeak-ng.so) is vendored separately,
// not committed to this repo - see README.md.

#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <espeak-ng/speak_lib.h>

#define LOG_TAG "espeak_bridge"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Grows as the synth callback delivers chunks. Single-threaded use only
// (one synthesize call at a time), which matches how KannadaTts.kt uses it.
static short *g_pcm_buffer = NULL;
static int g_pcm_capacity = 0;
static int g_pcm_length = 0;

static void ensure_capacity(int extra_samples) {
    if (g_pcm_length + extra_samples <= g_pcm_capacity) {
        return;
    }
    int new_capacity = g_pcm_capacity == 0 ? 16384 : g_pcm_capacity * 2;
    while (new_capacity < g_pcm_length + extra_samples) {
        new_capacity *= 2;
    }
    g_pcm_buffer = (short *) realloc(g_pcm_buffer, new_capacity * sizeof(short));
    g_pcm_capacity = new_capacity;
}

static int synth_callback(short *wav, int numsamples, espeak_EVENT *events) {
    (void) events;
    if (wav != NULL && numsamples > 0) {
        ensure_capacity(numsamples);
        memcpy(g_pcm_buffer + g_pcm_length, wav, numsamples * sizeof(short));
        g_pcm_length += numsamples;
    }
    return 0; // continue synthesis
}

JNIEXPORT jint JNICALL
Java_com_learnkannadanumbers_app_speech_KannadaTts_nativeInit(
        JNIEnv *env, jobject thiz, jstring data_path) {
    (void) thiz;
    const char *path = (*env)->GetStringUTFChars(env, data_path, NULL);

    int sample_rate = espeak_Initialize(AUDIO_OUTPUT_SYNCHRONOUS, 0, path, 0);

    (*env)->ReleaseStringUTFChars(env, data_path, path);

    if (sample_rate <= 0) {
        LOGE("espeak_Initialize failed (rc=%d)", sample_rate);
        return -1;
    }

    espeak_SetSynthCallback(synth_callback);
    return sample_rate;
}

JNIEXPORT jint JNICALL
Java_com_learnkannadanumbers_app_speech_KannadaTts_nativeSetVoice(
        JNIEnv *env, jobject thiz, jstring voice_name) {
    (void) thiz;
    const char *voice = (*env)->GetStringUTFChars(env, voice_name, NULL);
    espeak_ERROR result = espeak_SetVoiceByName(voice);
    (*env)->ReleaseStringUTFChars(env, voice_name, voice);
    return (jint) result;
}

JNIEXPORT jshortArray JNICALL
Java_com_learnkannadanumbers_app_speech_KannadaTts_nativeSynthesize(
        JNIEnv *env, jobject thiz, jstring text) {
    (void) thiz;
    const char *utf8Text = (*env)->GetStringUTFChars(env, text, NULL);

    g_pcm_length = 0; // reuse the buffer; capacity only grows

    unsigned int unique_identifier;
    espeak_ERROR result = espeak_Synth(
            utf8Text,
            strlen(utf8Text) + 1,
            0,
            POS_CHARACTER,
            0,
            espeakCHARS_UTF8,
            &unique_identifier,
            NULL);

    (*env)->ReleaseStringUTFChars(env, text, utf8Text);

    if (result != EE_OK) {
        LOGE("espeak_Synth failed (rc=%d)", result);
        return (*env)->NewShortArray(env, 0);
    }

    jshortArray out = (*env)->NewShortArray(env, g_pcm_length);
    if (out != NULL && g_pcm_length > 0) {
        (*env)->SetShortArrayRegion(env, out, 0, g_pcm_length, g_pcm_buffer);
    }
    return out;
}

JNIEXPORT void JNICALL
Java_com_learnkannadanumbers_app_speech_KannadaTts_nativeTerminate(
        JNIEnv *env, jobject thiz) {
    (void) env;
    (void) thiz;
    espeak_Terminate();
    free(g_pcm_buffer);
    g_pcm_buffer = NULL;
    g_pcm_capacity = 0;
    g_pcm_length = 0;
}
