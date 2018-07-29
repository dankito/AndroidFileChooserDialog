package net.dankito.filechooserdialog.model


enum class ExtensionsFilter(val filter: List<String>) {

    WebViewSupportedImages(listOf(".bmp", ".gif", ".jpg", ".png", ".webp")),

    WebViewSupportedAudioFormats(listOf(".3gp", ".mp4", ".m4a", ".aac", ".ts", ".flac", ".mid", ".xmf", ".mxmf", ".rtttl", ".rtx", ".ota", ".imy", ".mp3", ".mkv", ".wav", ".ogg")),

    WebViewSupportedVideoFormats(listOf(".3gp", ".mp4", ".ts", ".webm"))

}