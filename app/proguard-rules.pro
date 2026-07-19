# Keep sherpa-onnx JNI-bound classes: their fields/methods are accessed by name from native code.
-keep class com.k2fsa.sherpa.onnx.** { *; }
