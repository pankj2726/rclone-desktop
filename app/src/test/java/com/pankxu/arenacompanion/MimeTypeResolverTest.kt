package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.upload.MimeTypeResolver
import org.junit.Assert.*
import org.junit.Test

class MimeTypeResolverTest {

    @Test fun `image mime types are recognized`() {
        assertTrue(MimeTypeResolver.isImage("image/png"))
        assertTrue(MimeTypeResolver.isImage("image/jpeg"))
        assertTrue(MimeTypeResolver.isImage("image/webp"))
        assertFalse(MimeTypeResolver.isImage("text/plain"))
    }

    @Test fun `document mime types are recognized`() {
        assertTrue(MimeTypeResolver.isDocument("application/pdf"))
        assertTrue(MimeTypeResolver.isDocument("text/plain"))
        assertTrue(MimeTypeResolver.isDocument("text/markdown"))
        assertFalse(MimeTypeResolver.isDocument("image/png"))
    }

    @Test fun `code mime types are recognized`() {
        assertTrue(MimeTypeResolver.isCode("text/javascript"))
        assertTrue(MimeTypeResolver.isCode("text/x-kotlin"))
        assertTrue(MimeTypeResolver.isCode("text/x-python"))
        assertFalse(MimeTypeResolver.isCode("image/png"))
    }

    @Test fun `data mime types are recognized`() {
        assertTrue(MimeTypeResolver.isData("application/json"))
        assertTrue(MimeTypeResolver.isData("application/xml"))
        assertTrue(MimeTypeResolver.isData("text/csv"))
        assertFalse(MimeTypeResolver.isData("image/png"))
    }

    @Test fun `all supported types include major categories`() {
        assertTrue(MimeTypeResolver.ALL_TYPES.containsAll(
            listOf("image/png", "image/jpeg", "application/pdf", "application/json", "text/javascript")
        ))
    }

    @Test fun `extension to mime mapping`() {
        val ext = MimeTypeResolver.getExtension("image/png")
        assertNotNull(ext)
    }
}
