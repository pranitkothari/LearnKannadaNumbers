# Keep sherpa-onnx JNI-bound classes: their fields/methods are accessed by name from native code.
-keep class com.k2fsa.sherpa.onnx.** { *; }

# ViewModelProvider instantiates this reflectively; without this R8 can strip the
# constructor it needs, causing a runtime NoSuchMethodException in release builds.
-keep class com.learnkannadanumbers.app.ui.MainViewModel { <init>(...); }
