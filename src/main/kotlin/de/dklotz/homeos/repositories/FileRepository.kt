package de.dklotz.homeos.repositories

import de.dklotz.homeos.entities.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<FileEntity, Long> {
}