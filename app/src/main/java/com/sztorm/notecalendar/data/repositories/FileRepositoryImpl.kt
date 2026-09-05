package com.sztorm.notecalendar.data.repositories

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.sztorm.notecalendar.domain.repositories.FileRepository
import com.sztorm.notecalendar.domain.repositories.LoadResult
import com.sztorm.notecalendar.domain.repositories.SaveResult
import com.sztorm.notecalendar.feature.settings.notes.NotesBackupFile
import com.sztorm.notecalendar.feature.settings.theme.ThemeFile
import java.io.BufferedReader
import java.io.InputStreamReader

private const val LOAD_FAILURE_JSON_FORMAT = "Could not load the file. Wrong JSON format."

class FileRepositoryImpl(val activity: ComponentActivity) : FileRepository {
    private val createDocumentLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { createDocumentCallback(it) }
    private val openDocumentLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { openDocumentCallback(it) }
    private val savedFilesQueue = ArrayDeque<Pair<ByteArray, (SaveResult) -> Unit>>()
    private val loadedFileCallbacksQueue = ArrayDeque<(LoadResult<String>) -> Unit>()

    private fun onCreateDocumentFailure(message: String, onSaveResult: (SaveResult) -> Unit) {
        onSaveResult(SaveResult.Failure(message))
    }

    private fun onCreateDocumentSuccess(
        fileUri: Uri, fileContent: ByteArray, onSaveResult: (SaveResult) -> Unit
    ) {
        activity.contentResolver.openOutputStream(fileUri, "wt")?.use { outputStream ->
            outputStream.write(fileContent)
        }.let {
            when (it) {
                null -> onSaveResult(SaveResult.Failure(message = "Could not open output stream."))
                else -> onSaveResult(SaveResult.Success)
            }
        }
    }

    private fun createDocumentCallback(result: ActivityResult) {
        val savedFileData = savedFilesQueue.removeFirstOrNull()

        savedFileData?.let {
            val (fileContent, onSaveResult) = savedFileData

            if (result.resultCode == RESULT_OK) {
                when (val uri = result.data?.data) {
                    null -> onCreateDocumentFailure(
                        message = "Could not save the file.", onSaveResult
                    )

                    else -> onCreateDocumentSuccess(uri, fileContent, onSaveResult)
                }
            } else {
                onCreateDocumentFailure(
                    message = "Could not save the file. Result code: ${result.resultCode}",
                    onSaveResult
                )
            }
        }
    }

    private fun onOpenDocumentFailure(
        message: String, onLoadResult: (LoadResult<String>) -> Unit
    ) {
        onLoadResult(LoadResult.Failure(message))
    }

    private fun onOpenDocumentSuccess(fileUri: Uri, onLoadResult: (LoadResult<String>) -> Unit) {
        activity.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            val content = StringBuilder()

            reader.forEachLine {
                content.append(it).append("\n")
            }
            content.toString()
        }.let { fileContent ->
            when (fileContent) {
                null -> onLoadResult(LoadResult.Failure(message = "Could not open input stream."))
                else -> onLoadResult(LoadResult.Success(fileContent))
            }
        }
    }

    private fun openDocumentCallback(result: ActivityResult) {
        val onLoadResult = loadedFileCallbacksQueue.removeFirstOrNull()

        onLoadResult?.let {
            if (result.resultCode == RESULT_OK) {
                when (val uri = result.data?.data) {
                    null -> onOpenDocumentFailure(
                        message = "Could not load the file.", onLoadResult
                    )

                    else -> onOpenDocumentSuccess(uri, onLoadResult)
                }
            } else {
                onOpenDocumentFailure(
                    message = "Could not load the file. Result code: ${result.resultCode}",
                    onLoadResult
                )
            }
        }
    }

    private fun saveFileImpl(
        fileName: String,
        filetype: String,
        fileContent: ByteArray,
        onSaveResult: (SaveResult) -> Unit
    ) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = filetype
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        savedFilesQueue.addFirst(fileContent to onSaveResult)
        createDocumentLauncher.launch(intent)
    }

    private fun loadFileImpl(filetype: String, onLoadResult: (LoadResult<String>) -> Unit) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = filetype
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        loadedFileCallbacksQueue.addFirst(onLoadResult)
        openDocumentLauncher.launch(intent)
    }

    override fun saveThemeFile(
        fileName: String, filetype: String, file: ThemeFile, onSaveResult: (SaveResult) -> Unit
    ) = saveFileImpl(fileName, filetype, file.toJson().toByteArray(), onSaveResult)

    override fun saveNotesBackupFile(
        fileName: String,
        filetype: String,
        file: NotesBackupFile,
        onSaveResult: (SaveResult) -> Unit
    ) = saveFileImpl(fileName, filetype, file.toJson().toByteArray(), onSaveResult)

    override fun loadThemeFile(filetype: String, onLoadResult: (LoadResult<ThemeFile>) -> Unit) =
        loadFileImpl(filetype) { result ->
            when (result) {
                is LoadResult.Success<String> ->
                    when (val file = ThemeFile.fromJson(result.file)) {
                        null ->
                            onLoadResult(LoadResult.Failure(message = LOAD_FAILURE_JSON_FORMAT))

                        else -> onLoadResult(LoadResult.Success(file))
                    }

                is LoadResult.Failure -> onLoadResult(LoadResult.Failure(result.message))
            }
        }

    override fun loadNotesBackupFile(
        filetype: String,
        onLoadResult: (LoadResult<NotesBackupFile>) -> Unit
    ) = loadFileImpl(filetype) { result ->
        when (result) {
            is LoadResult.Success<String> -> {
                when (val file = NotesBackupFile.fromJson(result.file)) {
                    null ->
                        onLoadResult(LoadResult.Failure(message = LOAD_FAILURE_JSON_FORMAT))

                    else -> onLoadResult(LoadResult.Success(file))
                }
            }

            is LoadResult.Failure -> onLoadResult(LoadResult.Failure(result.message))
        }
    }
}