# sherpa-onnx native libraries go here

Each ABI directory (`arm64-v8a/`, `x86_64/`) must contain, before building:

- `libonnxruntime.so`
- `libsherpa-onnx-jni.so`

Get a matching pair for your chosen sherpa-onnx release from
`sherpa-onnx-vX.Y.Z-android.tar.bz2` at
https://github.com/k2-fsa/sherpa-onnx/releases (or build them yourself with
the NDK - see that repo's Android build docs). Don't mix `.so` files from
different sherpa-onnx versions/builds.

See the root `README.md` ("Setting up offline speech recognition") for details.
