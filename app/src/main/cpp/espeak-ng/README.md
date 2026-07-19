# Vendored espeak-ng

`include/espeak-ng/` and `lib/arm64-v8a/libespeak-ng.so` are prebuilt
binaries + headers from
https://github.com/HeyLetsLearnSomething/eSpeak-libespeak-ng.so
(GPL-3.0 - see the repo root `LICENSE`; this app is GPL-3.0 as a result).

`CMakeLists.txt` in the parent directory links against this. If you need
another ABI (e.g. `x86_64` for the emulator), add
`lib/x86_64/libespeak-ng.so` yourself and add that ABI back to
`abiFilters` in `app/build.gradle.kts`.
