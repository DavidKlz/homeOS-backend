package de.dklotz.homeos.models

enum class MimeType(val type: String, val extensions: List<String>) {
    VIDEO_MPEG("video/mpeg", listOf("mpeg", "mpg", "mpe")),
    VIDEO_MP4("video/mp4", listOf("mp4", "m4v")),
    VIDEO_OGG("video/ogg", listOf("ogg", "ogv")),
    VIDEO_QUICKTIME("video/quicktime", listOf("qt", "mov")),
    VIDEO_WEBM("video/webm", listOf("webm")),
    VIDEO_X_MSVIDEO("video/x-msvideo", listOf("avi")),
    VIDEO_X_SGI_MOVIE("video/x-sgi-movie", listOf("movie")),
    IMAGE_BMP("image/bmp", listOf("bmp")),
    IMAGE_CIS_COD("image/cis-cod", listOf("cod")),
    IMAGE_CMU_RASTER("image/cmu-raster", listOf("ras")),
    IMAGE_FIF("image/fif", listOf("fif")),
    IMAGE_GIF("image/gif", listOf("gif")),
    IMAGE_IEF("image/ief", listOf("ief")),
    IMAGE_JPEG("image/jpeg", listOf("jpeg", "jpg", "jpe")),
    IMAGE_PNG("image/png", listOf("png", "png")),
    IMAGE_SVG_XML("image/svg+xml", listOf("svg")),
    IMAGE_TIFF("image/tiff", listOf("tiff")),
    IMAGE_VASA("image/vasa", listOf("mcf")),
    IMAGE_HEIC("image/heic", listOf("heic", "heif")),
    IMAGE_WEBP("image/webp", listOf("webp"));

    companion object {
        fun getMimeType(extension: String): MimeType? {
            return entries.find { it.extensions.contains(extension.lowercase()) }
        }
    }
}