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
            MetaInfoDTO(id = it.id!!, label = it.label, value = it.value)
        }
    }

    fun getAllMetaInfoByLabel(label: String): List<MetaInfoDTO> {
        return repository.findAllByLabel(label).map { MetaInfoDTO(id = it.id!!, label = it.label, value = it.value) }
    }

    fun getMetaInfo(id: Long): MetaInfoDTO {
        return repository.findById(id).map {
            MetaInfoDTO(id = it.id!!, label = it.label, value = it.value)
        }.orElseGet(null)
    }

    fun addMetaInfo(metaInfo: MetaInfoDTO) : MetaInfoDTO {
        val entity = repository.save(MetaInfoEntity(
            id = null,
            label = metaInfo.label,
            value = metaInfo.value,
            files = Collections.emptySet(),
        ))

        return MetaInfoDTO(id = entity.id!!, label = metaInfo.label, value = metaInfo.value)
    }

    fun updateMetaInfo(id: Long, metaInfo: MetaInfoDTO) : MetaInfoDTO {
        return repository.findById(id).map {
            val save = repository.save(MetaInfoEntity(id = id, label = metaInfo.label, value = metaInfo.value, files = it.files))
            MetaInfoDTO(id = save.id!!, label = metaInfo.label, value = metaInfo.value)
        }.orElseGet(null)
    }

    fun removeMetaInfo(id: Long) : Boolean {
        repository.deleteById(id)
        return true
    }
}