package de.dklotz.homeos.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity(name = "vaultMetaInfo")
@Table(name = "vaultMetaInfo", uniqueConstraints = [
    UniqueConstraint(name = "MetaIndex", columnNames = ["label", "value"])
])
data class VaultMetaInfoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var label: String,
    var value: String,
    @ManyToMany
    var files: Set<VaultFileEntity>,
)
