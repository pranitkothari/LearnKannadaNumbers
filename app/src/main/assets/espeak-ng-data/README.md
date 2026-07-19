# espeak-ng voice/phoneme data goes here

This directory must contain the contents of espeak-ng's `espeak-ng-data`
directory (voices, dictionaries, including the Kannada `kn` voice) before
building. `KannadaTts.kt` copies this whole directory out of assets to
internal storage at first run, since espeak-ng needs a real filesystem path.

See the root `README.md` ("Setting up offline TTS") for where to get this.
Delete this placeholder `README.md` once the real data files are in place.
