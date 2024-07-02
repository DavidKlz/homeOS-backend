package de.dklotz.homeos.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity(name="file")
data class FileEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var name: String,
    var location: String,
    var mimetype: String,
    var favorite: Boolean,
    @ManyToMany
    var metaInfos: MutableSet<MetaInfoEntity>
)
