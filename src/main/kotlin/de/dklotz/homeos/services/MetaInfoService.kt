package de.dklotz.homeos.services

import de.dklotz.homeos.dto.MetaInfoDTO
import de.dklotz.homeos.entities.MetaInfoEntity
import de.dklotz.homeos.repositories.MetaInfoRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class MetaInfoService(val repository: MetaInfoRepository) {
    fun getAllMetaInfo(): List<MetaInfoDTO> {
        return repository.findAll().map {
            MetaInfoDTO(id = it.id!!, type = it.type, value = it.value)
        }
    }

    fun getAllMetaInfoByLabel(label: String): List<MetaInfoDTO> {
        return repository.findAllByType(label).map { MetaInfoDTO(id = it.id!!, type = it.type, value = it.value) }
    }

    fun getMetaInfo(id: Long): MetaInfoDTO {
        return repository.findById(id).map {
            MetaInfoDTO(id = it.id!!, type = it.type, value = it.value)
        }.orElseGet(null)
    }

    fun safeMetaInfo(metaInfo: MetaInfoDTO) : MetaInfoDTO {
        if(metaInfo.id != null) {
            val id = metaInfo.id!!
            return repository.findById(id).map {
                val save = repository.save(MetaInfoEntity(id = id, type = metaInfo.type, value = metaInfo.value, files = it.files))
                MetaInfoDTO(id = save.id!!, type = metaInfo.type, value = metaInfo.value)
            }.orElseGet(null)
        } else {
            val entity = repository.save(
                MetaInfoEntity(
                    id = null,
                    type = metaInfo.type,
                    value = metaInfo.value,
                    files = Collections.emptySet(),
                )
            )

            return MetaInfoDTO(id = entity.id!!, type = metaInfo.type, value = metaInfo.value)
        }
    }

    fun removeMetaInfo(id: Long) : Boolean {
        repository.deleteById(id)
        return true
    }
}