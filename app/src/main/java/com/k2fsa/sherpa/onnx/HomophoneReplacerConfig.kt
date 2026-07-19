// Vendored from k2-fsa/sherpa-onnx (kotlin-api), Apache-2.0 license: https://github.com/k2-fsa/sherpa-onnx
package com.k2fsa.sherpa.onnx

data class HomophoneReplacerConfig(
    var dictDir: String = "", // unused
    var lexicon: String = "",
    var ruleFsts: String = "",
)
