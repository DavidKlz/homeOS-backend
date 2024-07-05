package de.dklotz.homeos.dto

import java.io.File

data class ServeFileDTO (
    var mimetype: String,
    var file: File,
)