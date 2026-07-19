# Native libraries in this directory

`arm64-v8a/` now ships with the prebuilt binaries this app needs at runtime:

- `libsherpa-onnx-jni.so` + `libonnxruntime.so` - sherpa-onnx's JNI bridge and
  its ONNX Runtime dependency. Obtained from the `com.bihe0832.android:lib-sherpa-onnx`
  and `com.bihe0832.android:lib-onnx` artifacts on Maven Central (Apache-2.0) -
  a third-party republish of the official k2-fsa/sherpa-onnx build output.
  JNI symbol names were verified to match the vendored official Kotlin API in
  `app/src/main/java/com/k2fsa/sherpa/onnx/`.
- `libespeak-ng.so` + `libc++_shared.so` - from
  https://github.com/HeyLetsLearnSomething/eSpeak-libespeak-ng.so (GPL-3.0,
  see repo root `LICENSE`).

Only `arm64-v8a` is bundled (matches real devices, including Galaxy Fold5).
Add an `x86_64` set yourself if you need the emulator to also run offline
speech features.
