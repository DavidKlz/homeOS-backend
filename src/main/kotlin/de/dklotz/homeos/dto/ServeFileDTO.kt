package de.dklotz.homeos.dto

data class ServeFileDTO (
    var mimetype: String,
    var file: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServeFileDTO

        if (mimetype != other.mimetype) return false
        if (!file.contentEquals(other.file)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mimetype.hashCode()
        result = 31 * result + file.contentHashCode()
        return result
    }
}