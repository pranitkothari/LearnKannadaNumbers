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

**License:** GPL-3.0 (see `LICENSE`) - this app bundles espeak-ng, which is
GPL-3.0, so the combined app is too.

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
  espeak-ng/                 - vendored espeak-ng headers + .so (bundled)
app/src/main/jniLibs/arm64-v8a/
  libsherpa-onnx-jni.so, libonnxruntime.so  - sherpa-onnx native libs (bundled)
  libespeak-ng.so, libc++_shared.so         - espeak-ng native libs (bundled)
```

## Before you can build

Almost everything offline this app needs is already committed - native
libraries for both sherpa-onnx and espeak-ng, and espeak-ng's full voice/
dictionary data (including Kannada). See `app/src/main/jniLibs/README.md`
and `app/src/main/cpp/espeak-ng/README.md` for exactly where each came from.

**One thing is missing**: the Whisper-tiny (multilingual) *model weights*
themselves. They're hosted on HuggingFace, which the network this app was
built in couldn't reach at all (everything else above came from Maven
Central and raw.githubusercontent.com, which were reachable). You need to
fetch these once, locally:

Download the multilingual Whisper-tiny export from
https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny and place
`tiny-encoder.int8.onnx`, `tiny-decoder.int8.onnx`, `tiny-tokens.txt`
into `app/src/main/assets/sherpa-onnx-whisper-tiny/` (delete the
placeholder `README.md` there once the real files are in place).

That's a one-time step on your dev machine before building - the shipped
APK itself makes no network calls.

## Building

Open the project in Android Studio (it will generate the Gradle wrapper on
first sync), or from the command line with a system-installed Gradle 8.14+
and the Android SDK/NDK configured:

```
gradle assembleRelease   # minified/optimized, much faster - recommended
gradle assembleDebug     # unoptimized, for debugging only
```

Both build types are debug-signed (see `app/build.gradle.kts`), so both
install fine when sideloaded - `release` just isn't set up for Play Store
distribution. A CI workflow (`.github/workflows/build-apk.yml`) also builds
both and publishes them to a rolling GitHub Release on every push.

Only `arm64-v8a` is built (matches real devices, incl. Galaxy Fold5); the
emulator (`x86_64`) isn't supported unless you add `x86_64` native libs
yourself for both sherpa-onnx and espeak-ng and add that ABI back to
`abiFilters` in `app/build.gradle.kts`.

**This sandboxed environment has no Android SDK or NDK installed**, so none
of this app's code has been compiled or run here. To de-risk that as much as
possible without a compiler: the Kotlin API in `com.k2fsa.sherpa.onnx` and
the C code in `espeak_bridge.c` were written against the real upstream
sources (fetched and read, not recalled from memory), and the JNI symbol
names exported by the bundled `libsherpa-onnx-jni.so` were checked with
`strings`/`readelf` to confirm they match what that Kotlin API expects. You'll
still want to do a first real build and a pass through the app on a device.

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
- **Native library provenance**: `libsherpa-onnx-jni.so` and `libonnxruntime.so`
  came from a third-party Maven republish (`com.bihe0832.android`) of the
  official k2-fsa/sherpa-onnx build output, not built from source here (no NDK
  available). JNI symbol names were verified to match, but you may prefer to
  rebuild these yourself from official source for anything beyond personal use.
- **GPL-3.0**: bundling espeak-ng makes the whole app GPL-3.0 (see `LICENSE`).
  If that's not what you want, espeak-ng needs to be swapped out.
