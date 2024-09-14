package de.dklotz.homeos.services

import de.dklotz.homeos.config.ServerConfig
import de.dklotz.homeos.dto.FileDTO
import de.dklotz.homeos.dto.MetaInfoDTO
import de.dklotz.homeos.dto.ServeFileDTO
import de.dklotz.homeos.entities.FileEntity
import de.dklotz.homeos.models.MimeType
import de.dklotz.homeos.repositories.FileRepository
import de.dklotz.homeos.repositories.MetaInfoRepository
import jakarta.annotation.PostConstruct
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong


@Service
class FileService(val repository: FileRepository, val metaInfoRepository: MetaInfoRepository, val serverConfig: ServerConfig) {
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
                    break
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
        val outPath = "${getPath(contentType)}/$outFilename"

        val thumbFilename = "$filename.jpg"
        val thumbPath = "${getThumbnailPath(contentType)}/$thumbFilename"

        Files.write(Path.of(outPath), inStream.readAllBytes())

        createImageThumb(outPath, thumbPath, contentType.isVideo())

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

    fun createImageThumb(inDir : String, outDir : String, isVideo : Boolean) {
        val ffmpeg = FFmpeg("/usr/bin/ffmpeg")
        val ffprobe = FFprobe("/usr/bin/ffprobe")
        val builder: FFmpegBuilder

        val inProbe = ffprobe.probe(inDir)
        val probeStream = inProbe.getStreams()[0]
        val thumbWidth = 360
        val thumbHeight =  (probeStream.height / probeStream.width) * thumbWidth

        if (isVideo) {
            val duration = (inProbe.getFormat().duration / 2).roundToLong()
            builder = FFmpegBuilder()
                .setInput(inDir)
                .addOutput(outDir)
                .setVideoResolution(thumbWidth, thumbHeight)
                .setFrames(1)
                .setStartOffset(duration, TimeUnit.SECONDS)
                .done()
        } else {
            builder = FFmpegBuilder()
                .setInput(inDir)
                .addOutput(outDir)
                .setVideoResolution(thumbWidth, thumbHeight)
                .setFrames(1)
                .done()
        }

        val executor = FFmpegExecutor(ffmpeg, ffprobe)
        executor.createJob(builder).run()
    }

    fun syncFiles() {
        val directory = File(getSyncPath())
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
        Files.createDirectories(Paths.get(getSyncPath()))
        Files.createDirectories(Paths.get(getThumbnailPath(MimeType.IMAGE_JPEG)))
        Files.createDirectories(Paths.get(getThumbnailPath(MimeType.VIDEO_MP4)))
        Files.createDirectories(Paths.get(getThumbnailPath(MimeType.IMAGE_GIF)))
    }

    private fun getSyncPath() : String {
        return "${serverConfig.root}/Sync"
    }

    private fun getPath(mime: MimeType) : String {
        return "${serverConfig.root}/Server/${mime.getFolderName()}"
    }

    private fun getThumbnailPath(mime: MimeType) : String {
        return "${getPath(mime)}/thumbnail"
    }
}