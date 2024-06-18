package io.nullptr.filestorage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

@SpringBootApplication
class FileStorageApplication

fun main(args: Array<String>) {
    runApplication<FileStorageApplication>(*args)
}


@Controller
class FileStorageController {

    @PostMapping("/v1/upload")
    fun upload(request: StandardMultipartHttpServletRequest) {
        val file = request.getFile("file")
        println(file?.originalFilename)

        request.multiFileMap.forEach { (key, value) ->
            value.forEach {
                println("${it.originalFilename} - $key")
            }
        }
    }

    @PostMapping("/v2/upload")
    fun uploadV2(@RequestParam("hf") files: List<MultipartFile>) {
        files.forEach {
            println(it.originalFilename)
        }
    }
}