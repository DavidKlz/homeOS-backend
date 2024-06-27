package de.dklotz.homeos.repositories

import de.dklotz.homeos.entities.VaultFileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VaultFileRepository : JpaRepository<VaultFileEntity, Long> {}
