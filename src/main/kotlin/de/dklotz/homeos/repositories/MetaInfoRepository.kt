package de.dklotz.homeos.repositories

import de.dklotz.homeos.entities.MetaInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MetaInfoRepository : JpaRepository<MetaInfoEntity, Long> {
    fun findAllByLabel(label: String): Iterable<MetaInfoEntity>
}