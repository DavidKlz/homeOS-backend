package de.dklotz.homeos.repositories

import de.dklotz.homeos.entities.MetaInfoEntity
import de.dklotz.homeos.entities.VaultMetaInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VaultMetaInfoRepository : JpaRepository<VaultMetaInfoEntity, Long> {
    fun findAllByLabel(label: String): Iterable<MetaInfoEntity>
}