package de.dklotz.homeos.dto

data class FileDTO(
    var id: Long?,
    var name: String,
    var favorite: Boolean,
    var isVideo: Boolean,
    var metaInfos: List<MetaInfoDTO>
)
