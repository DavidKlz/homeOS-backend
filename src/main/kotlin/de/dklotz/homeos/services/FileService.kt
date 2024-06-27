package de.dklotz.homeos.services

import de.dklotz.homeos.dto.FileDTO
import de.dklotz.homeos.dto.MetaInfoDTO
import de.dklotz.homeos.dto.ServeFileDTO
import de.dklotz.homeos.entities.FileEntity
import de.dklotz.homeos.models.MimeType
import de.dklotz.homeos.repositories.FileRepository
import de.dklotz.homeos.repositories.MetaInfoRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*


@Service
class FileService(val repository: FileRepository, val metaInfoRepository: MetaInfoRepository) {
    fun getFile(id: Long): Optional<FileDTO> {
        return repository.findById(id).map {
            FileDTO(
                id = id,
                name = it.name,
                favorite = it.favorite,
                metaInfos = it.metaInfos.map { mi ->
                    MetaInfoDTO(
                        id = mi.id!!,
                        value = mi.value,
                        label = mi.label,
                    )
                }
            )
        }
    }

    fun serveFileInfos(id: Long) : Optional<ServeFileDTO> {
        return repository.findById(id).map {
            ServeFileDTO(
                mimetype = it.mimetype,
                file = Path.of(it.location).toFile().readBytes()
            )
        }
    }

    fun getAllFiles(): List<FileDTO> {
        return repository.findAll().map {
            FileDTO(
                id = it.id!!,
                name = it.name,
                favorite = it.favorite,
                metaInfos = it.metaInfos.map { mi ->
                    MetaInfoDTO(
                        id = mi.id!!,
                        value = mi.value,
                        label = mi.label,
                    )
                }
            )
        }
    }

    fun storeFile(files: Array<MultipartFile>) {
        var i = 1
        for(file in files) {
            try {
                if(file.isEmpty) {
                    // TODO: Log empty file
                    break;
                }
                val extension = file.name.substringAfterLast('.')
                val filename = "FILE_${i}u${UUID.randomUUID()}.$extension"
                val path = "E:/Server/$filename"
                file.inputStream.use {
                    Files.copy(it, Path.of(path), StandardCopyOption.REPLACE_EXISTING)
                }

                val contentType = MimeType.getMimeType(extension) ?: throw Exception("Unknown extension $extension")

                repository.save(FileEntity(
                    id = null,
                    name = filename,
                    metaInfos = Collections.emptySet(),
                    favorite = false,
                    location = path,
                    mimetype = contentType.type,
                ))
                i+=1
            } catch(e: IOException) {
                // TODO: Log Error
                break;
            }
        }
    }

    fun syncFiles() {
        val directory = File("E:/In")
        val files = directory.listFiles()?.filter { it.isFile }
        var i = 1
        files?.forEach {
            val extension = it.name.substringAfterLast('.')
            val filename = "FILE_${i}u${UUID.randomUUID()}.$extension"
            val path = "E:/Server/$filename"

            Files.move(it.toPath(), Path.of(path), StandardCopyOption.REPLACE_EXISTING)

            val contentType = MimeType.getMimeType(extension) ?: throw Exception("Unknown extension $extension")

            repository.save(FileEntity(
                id = null,
                name = filename,
                metaInfos = Collections.emptySet(),
                favorite = false,
                location = path,
                mimetype = contentType.type,
            ))
            i+=1
        }

    }

    fun addMetaInfo(id: Long, metaInfoId: Long): FileDTO {
        val file = repository.findById(id).get()
        val metaInfo = metaInfoRepository.findById(metaInfoId).get()
        file.metaInfos.plus(metaInfo)
        val save = repository.save(file)
        return FileDTO(
            id = id,
            name = save.name,
            favorite = save.favorite,
            metaInfos = save.metaInfos.map { mi ->
                MetaInfoDTO(
                    id = mi.id!!,
                    value = mi.value,
                    label = mi.label,
                )
            }
        )
    }

    fun removeMetaInfo(id: Long, metaInfoId: Long): Boolean {
        val file = repository.findById(id).get()
        val metaInfo = metaInfoRepository.findById(metaInfoId).get()
        file.metaInfos.minus(metaInfo)
        repository.save(file)
        return true
    }

    fun deleteFile(id: Long) : Boolean {
        repository.deleteById(id)
        // TODO: Remove from Disk
        return true
    }
}