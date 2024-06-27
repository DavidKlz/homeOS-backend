package de.dklotz.homeos.entities

import jakarta.persistence.*

@Entity(name = "vaultFile")
data class VaultFileEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var name: String,
    var location: String,
    var mimetype: String,
    var favorite: Boolean,
    @ManyToMany
    var metaInfos: Set<VaultMetaInfoEntity>,
)
