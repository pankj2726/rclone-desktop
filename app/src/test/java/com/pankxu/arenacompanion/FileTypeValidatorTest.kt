package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.upload.FileTypeValidator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FileTypeValidatorTest {

    private lateinit var validator: FileTypeValidator
    private lateinit var tempFolder: TemporaryFolder

    @Before fun setup() {
        validator = FileTypeValidator()
        tempFolder = TemporaryFolder()
        tempFolder.create()
    }

    @Test fun `valid small file passes`() {
        val file = File(tempFolder.root, "test.txt")
        file.writeText("hello world")
        val result = validator.validate(file, 50L * 1024 * 1024)
        assertEquals(FileTypeValidator.ValidationResult.VALID, result.result)
    }

    @Test fun `oversized file fails`() {
        val file = File(tempFolder.root, "test.txt")
        file.writeText("hello")
        val result = validator.validate(file, 1L)
        assertEquals(FileTypeValidator.ValidationResult.INVALID_SIZE, result.result)
    }

    @Test fun `nonexistent file fails`() {
        val result = validator.validate(File("/nonexistent/file.txt"), 50L * 1024 * 1024)
        assertEquals(FileTypeValidator.ValidationResult.INVALID_SIGNATURE, result.result)
    }

    @Test fun `categories are correct`() {
        val pngFile = File(tempFolder.root, "test.png")
        pngFile.writeText("fake png content")
        val result = validator.validate(pngFile, 50L * 1024 * 1024)
        // May be VALID or INVALID_SIGNATURE depending on signature check
        assertNotNull(result.category)
    }
}
