package cz.pedro.auth.helper

import java.io.File

class FileLoader {
    companion object {

        private const val BASE_PATH = "src/test/resources/"

        fun loadFile(path: String): File? {
            val file = File(BASE_PATH.plus(path))
            return if (file.exists()) {
                file
            } else {
                null
            }
        }
    }
}
