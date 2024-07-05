package de.dklotz.homeos.entities

import de.dklotz.homeos.models.MimeType
import jakarta.persistence.*

@Entity(name="file")
data class FileEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var name: String,
    var location: String,
    var thumbnailLocation: String,
    @Enumerated(EnumType.STRING)
    var mimetype: MimeType,
    var favorite: Boolean,
    @ManyToMany
    var metaInfos: MutableSet<MetaInfoEntity>
)
