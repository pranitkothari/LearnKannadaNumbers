# Whisper-tiny (multilingual) model files go here

This is the one offline asset this repo doesn't bundle - the model is
hosted on HuggingFace, which the environment this app was built in
couldn't reach (network policy blocked huggingface.co entirely). Everything
else (espeak-ng, sherpa-onnx's native libraries) is already committed.

Before building, this directory must contain:

- `tiny-encoder.int8.onnx`
- `tiny-decoder.int8.onnx`
- `tiny-tokens.txt`

Source: https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny

Delete this placeholder `README.md` once the real model files are in place.
