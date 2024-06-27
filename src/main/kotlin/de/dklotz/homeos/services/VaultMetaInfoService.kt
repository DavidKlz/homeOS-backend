package de.dklotz.homeos.services

import de.dklotz.homeos.dto.MetaInfoDTO
import de.dklotz.homeos.entities.VaultMetaInfoEntity
import de.dklotz.homeos.repositories.VaultMetaInfoRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class VaultMetaInfoService(val repository: VaultMetaInfoRepository) {
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
        val entity = repository.save(
            VaultMetaInfoEntity(
            id = null,
            label = metaInfo.label,
            value = metaInfo.value,
            files = Collections.emptySet(),
        )
        )

        return MetaInfoDTO(id = entity.id!!, label = metaInfo.label, value = metaInfo.value)
    }

    fun updateMetaInfo(id: Long, metaInfo: MetaInfoDTO) : MetaInfoDTO {
        return repository.findById(id).map {
            val save = repository.save(VaultMetaInfoEntity(id = id, label = metaInfo.label, value = metaInfo.value, files = it.files))
            MetaInfoDTO(id = save.id!!, label = metaInfo.label, value = metaInfo.value)
        }.orElseGet(null)
    }

    fun removeMetaInfo(id: Long) : Boolean {
        repository.deleteById(id)
        return true
    }
}