package de.dklotz.homeos.services

import de.dklotz.homeos.dto.FileDTO
import de.dklotz.homeos.dto.MetaInfoDTO
import de.dklotz.homeos.dto.ServeFileDTO
import de.dklotz.homeos.entities.FileEntity
import de.dklotz.homeos.models.MimeType
import de.dklotz.homeos.repositories.FileRepository
import de.dklotz.homeos.repositories.MetaInfoRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class FileService(val repository: FileRepository, val metaInfoRepository: MetaInfoRepository) {
    fun getFile(id: Long): Optional<FileDTO> {
        return repository.findById(id).map {
            FileDTO(
                id = id,
                name = it.name,
                favorite = it.favorite,
                isVideo = it.mimetype.isVideo(),
                metaInfos = it.metaInfos.map { mi ->
                    MetaInfoDTO(
                        id = mi.id!!,
                        value = mi.value,
                        type = mi.type,
                    )
                }
            )
        }
    }

    fun serveFileInfos(id: Long): Optional<ServeFileDTO> {
        return repository.findById(id).map {
            ServeFileDTO(
                mimetype = it.mimetype.type,
                file = Path.of(it.location).toFile()
            )
        }
    }

    fun serveThumbnailInfos(id: Long): Optional<ServeFileDTO> {
        return repository.findById(id).map {
            ServeFileDTO(
                mimetype = MimeType.IMAGE_JPEG.type,
                file = Path.of(it.thumbnailLocation).toFile()
            )
        }
    }

    fun getAllFiles(): List<FileDTO> {
        val result = repository.findAll()
        result.sortWith { o1: FileEntity, o2: FileEntity -> o2.id!!.compareTo(o1.id!!) }
        return result.map {
            FileDTO(
                id = it.id!!,
                favorite = it.favorite,
                isVideo = it.mimetype.isVideo(),
                name = it.name,
                metaInfos = it.metaInfos.map { mi ->
                    MetaInfoDTO(
                        id = mi.id!!,
                        value = mi.value,
                        type = mi.type,
                    )
                }
            )
        }
    }

    fun uploadFile(files: Array<MultipartFile>) {
        var i = 1
        for (file in files) {
            try {
                if (file.isEmpty) {
                    // TODO: Log empty file
                    break;
                }
                val extension = file.name.substringAfterLast('.')

                storeFile(extension = extension, count = i, inStream = file.inputStream)
                i += 1
            } catch (e: IOException) {
                // TODO: Log Error
                break;
            }
        }
    }

    private fun storeFile(extension: String, count: Int, inStream: InputStream) {
        val contentType = MimeType.getMimeType(extension) ?: throw Exception("Unknown extension $extension")
        val filename = "FILE_${count}u${UUID.randomUUID()}"
        val outFilename = "$filename.$extension"
        val outPath = "./Server/${contentType.getFolderName()}/$outFilename"

        val thumbPath : String

        Files.write(Path.of(outPath), inStream.readAllBytes())

        if(contentType.isVideo()) {
            val thumbFilename = "$filename.jpg"
            thumbPath = "./Server/${contentType.getFolderName()}/thumb/$thumbFilename"
            Runtime.getRuntime().exec(arrayOf("ffmpeg", "-i", outPath, "-vf", "scale=\"360:-1\"", "-ss", "1", "-vframes", "1", thumbPath))
        } else {
            val thumbFilename = "$filename.jpg"
            thumbPath = "./Server/${contentType.getFolderName()}/thumb/$thumbFilename"
            Runtime.getRuntime().exec(arrayOf("ffmpeg", "-i", outPath, "-vf", "scale=\"360:-1\"", thumbPath))
        }

        repository.save(
            FileEntity(
                id = null,
                name = filename,
                metaInfos = Collections.emptySet(),
                favorite = false,
                location = outPath,
                thumbnailLocation = thumbPath,
                mimetype = contentType,
            )
        )
    }

    fun syncFiles() {
        val directory = File("./Sync")
        val files = directory.listFiles()?.filter { it.isFile }
        var i = 1
        files?.forEach {
            val extension = it.name.substringAfterLast('.')
            it.inputStream().use { inStream ->
                storeFile(extension = extension, count = i, inStream = inStream)
            }
            Files.deleteIfExists(it.toPath())

            i += 1
        }
    }

    fun addMetaInfo(id: Long, metaInfoId: Long): FileDTO {
        val file = repository.findById(id).get()
        val metaInfo = metaInfoRepository.findById(metaInfoId).get()
        file.metaInfos.add(metaInfo)
        val save = repository.save(file)
        return FileDTO(
            id = id,
            name = save.name,
            favorite = save.favorite,
            isVideo = save.mimetype.isVideo(),
            metaInfos = save.metaInfos.map { mi ->
                MetaInfoDTO(
                    id = mi.id!!,
                    value = mi.value,
                    type = mi.type,
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

    fun deleteFile(id: Long): Boolean {
        repository.deleteById(id)
        // TODO: Remove from Disk
        return true
    }

    @PostConstruct
    fun createFolders() {
        Files.createDirectories(Paths.get("./Sync"))
        Files.createDirectories(Paths.get("./Server/Image/thumb"))
        Files.createDirectories(Paths.get("./Server/Video/thumb"))
        Files.createDirectories(Paths.get("./Server/Animation"))
    }
}