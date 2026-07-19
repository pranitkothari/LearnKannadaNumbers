# Kannada Numbers

An offline Android app for practicing Kannada number pronunciation. Type a
number (0-100), speak it in Kannada into the mic, and the app tells you
whether you got it right - fully offline, no LLM calls, no network access
at runtime.

## How it works

1. Type a number from 0 to 100.
2. Tap the mic and say it in Kannada. Recording auto-stops after you go
   quiet (or after a ~4s cap).
3. The recording is transcribed on-device with a quantized, multilingual
   **Whisper-tiny** model running through **sherpa-onnx**, and fuzzy-matched
   against the expected Kannada word for that number.
4. Correct: a checkmark and "Correct!". Incorrect: the app speaks the
   correct pronunciation via an offline Kannada voice (**espeak-ng**) and
   shows what it heard vs. what was expected.

No internet permission is requested or used anywhere in the app.

## Project layout

```
app/src/main/java/com/learnkannadanumbers/app/
  MainActivity.kt            - permission handling, Compose host
  ui/MainScreen.kt           - the single screen UI
  ui/MainViewModel.kt        - round state machine
  data/KannadaNumbers.kt     - 0-100 Kannada number word data
  speech/AudioRecorder.kt    - mic capture, silence-based auto-stop
  speech/SpeechRecognizerManager.kt - sherpa-onnx Whisper wrapper
  speech/FuzzyMatch.kt       - lenient transcript-vs-expected matching
  speech/KannadaTts.kt       - espeak-ng JNI wrapper + playback
app/src/main/java/com/k2fsa/sherpa/onnx/
  ...                        - vendored sherpa-onnx Kotlin API (Apache-2.0,
                                copied from github.com/k2-fsa/sherpa-onnx)
app/src/main/cpp/
  espeak_bridge.c            - small JNI bridge around espeak-ng's
                                synchronous synth API
  espeak-ng/                 - vendored espeak-ng headers + .so (you add this)
```

## Before you can build

This app depends on two offline speech engines whose binary assets are
**not committed to this repo** (they're large, and versioned/licensed
separately). You need to fetch them once, locally:

### 1. Offline speech recognition (sherpa-onnx + Whisper-tiny)

a) Native libraries - download a `sherpa-onnx-vX.Y.Z-android.tar.bz2` from
   https://github.com/k2-fsa/sherpa-onnx/releases (use the latest release),
   extract it, and copy the matching `libonnxruntime.so` +
   `libsherpa-onnx-jni.so` pair into:
   - `app/src/main/jniLibs/arm64-v8a/`
   - `app/src/main/jniLibs/x86_64/` (only needed for the emulator)

b) Model files - download the multilingual Whisper-tiny export from
   https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny and place
   `tiny-encoder.int8.onnx`, `tiny-decoder.int8.onnx`, `tiny-tokens.txt`
   into `app/src/main/assets/sherpa-onnx-whisper-tiny/` (delete the
   placeholder `README.md` there once real files are in place).

### 2. Offline text-to-speech (espeak-ng, Kannada voice)

a) Native library + headers - get a prebuilt `libespeak-ng.so` (e.g. from
   https://github.com/HeyLetsLearnSomething/eSpeak-libespeak-ng.so) or build
   espeak-ng from source with the NDK, and place:
   - headers under `app/src/main/cpp/espeak-ng/include/`
   - `libespeak-ng.so` under `app/src/main/cpp/espeak-ng/lib/arm64-v8a/`
     and `.../lib/x86_64/`

b) Voice/dictionary data - copy espeak-ng's `espeak-ng-data` directory
   contents into `app/src/main/assets/espeak-ng-data/` (delete the
   placeholder `README.md` there once real files are in place).

Both steps happen once, on your dev machine, before building - the shipped
APK makes no network calls.

## Building

Open the project in Android Studio (it will generate the Gradle wrapper on
first sync), or from the command line with a system-installed Gradle 8.14+
and the Android SDK/NDK configured:

```
gradle assembleDebug
```

**This sandboxed environment has no Android SDK or NDK installed**, so none
of this app's code has been compiled or run here - I've written it against
the real, verified sherpa-onnx Kotlin API (fetched from its GitHub source)
and the documented espeak-ng C API, but you'll want to do a first real build
and a pass through the app on a device to catch anything a compiler would.

## Known gaps / things worth checking

- **Kannada number-word data** (`KannadaNumbers.kt`): 0-20, the decade words,
  and 100 are individually verified numerals. 21-99 are generated from a
  tens-stem + unit-suffix sandhi pattern that was cross-checked against six
  known compounds (21, 22, 23, 32, 45, 57) and applied uniformly to the
  other six decades - it hasn't been checked word-by-word by a native
  speaker, which matters given this app is teaching pronunciation.
- **Whisper-tiny accuracy on Kannada**: tiny is a small, lower-resource-language
  model; the lenient fuzzy-match threshold in `FuzzyMatch.kt`
  (`SIMILARITY_THRESHOLD`) may need tuning after testing with real speech -
  it's a single constant, easy to adjust.
- **espeak-ng's Kannada voice** is formant-synthesized, not a natural human
  voice - intelligible but robotic.
