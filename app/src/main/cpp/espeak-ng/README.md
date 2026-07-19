# Vendored espeak-ng goes here

Before building, this directory must contain:

- `include/espeak-ng/speak_lib.h` (and the other espeak-ng public headers it pulls in)
- `lib/arm64-v8a/libespeak-ng.so`
- `lib/x86_64/libespeak-ng.so`

Source options:

- Prebuilt: https://github.com/HeyLetsLearnSomething/eSpeak-libespeak-ng.so
- Build from source with the NDK: https://github.com/espeak-ng/espeak-ng
  (see its `android/` directory)

See the root `README.md` ("Setting up offline TTS") for details. `CMakeLists.txt`
in the parent directory expects exactly this layout.
