package rs.gdgoc.file_system

import java.io.File

/**
 * Otvara native file manager operativnog sistema u zadatom direktorijumu.
 * Podrzava Windows (File Explorer), macOS (Finder) i Linux (xdg-open).
 *
 * @param path Putanja do direktorijuma koji treba otvoriti.
 *             Po defaultu otvara home folder trenutnog korisnika.
 */


fun openFileExplorer(path: String = System.getProperty("user.home")) {
    val os = System.getProperty("os.name").lowercase()

    val command = when {
        os.contains("win") -> listOf("explorer", path)//windows
        os.contains("mac") -> listOf("open", path) //mac
        else -> listOf("xdg-open", path) // Linux
    }

    ProcessBuilder(command)
        .directory(File(path))
        .start()
}