package com.pankxu.arenacompanion.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.unit.dp

/** Custom vector icons designed for the app. No emojis, no external icon libraries. */
object AppIcons {

    val Browser: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(12f, 2f)
                lineTo(2f, 12f)
                lineTo(12f, 22f)
                lineTo(22f, 12f)
                lineTo(12f, 2f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(2f, 12f)
                lineTo(22f, 12f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(5f, 5f)
                lineTo(19f, 19f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Bookmark: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(6f, 2f)
                lineTo(18f, 2f)
                lineTo(18f, 10f)
                lineTo(12f, 22f)
                lineTo(6f, 10f)
                lineTo(6f, 2f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Chat: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(4f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 12f)
                lineTo(12f, 22f)
                lineTo(4f, 12f)
                lineTo(4f, 4f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Brain: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(9f, 4f)
                lineTo(15f, 4f)
                lineTo(14f, 8f)
                lineTo(14f, 12f)
                lineTo(15f, 20f)
                lineTo(12f, 22f)
                lineTo(9f, 20f)
                lineTo(8f, 12f)
                lineTo(8f, 8f)
                lineTo(9f, 4f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(11f, 6f)
                lineTo(13f, 6f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(11f, 10f)
                lineTo(13f, 10f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Key: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(14f, 2f)
                lineTo(22f, 10f)
                lineTo(22f, 18f)
                lineTo(18f, 20f)
                lineTo(12f, 14f)
                lineTo(8f, 8f)
                lineTo(14f, 2f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(6f, 6f)
                lineTo(8f, 8f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(8f, 8f)
                lineTo(10f, 10f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Disk: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(4f, 6f)
                lineTo(20f, 6f)
                lineTo(20f, 18f)
                lineTo(4f, 18f)
                lineTo(4f, 6f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(2f, 10f)
                lineTo(22f, 10f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(7f, 13f)
                lineTo(13f, 13f)
            }, fillColor = Color.Black)
        }.build()
    }

    val List: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(4f, 6f)
                lineTo(20f, 6f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(4f, 12f)
                lineTo(20f, 12f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(4f, 18f)
                lineTo(20f, 18f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(12f, 4f)
                lineTo(14f, 6f)
                lineTo(18f, 6f)
                lineTo(20f, 8f)
                lineTo(20f, 12f)
                lineTo(18f, 14f)
                lineTo(18f, 18f)
                lineTo(14f, 20f)
                lineTo(10f, 20f)
                lineTo(6f, 18f)
                lineTo(4f, 18f)
                lineTo(4f, 14f)
                lineTo(6f, 12f)
                lineTo(6f, 8f)
                lineTo(10f, 6f)
                lineTo(12f, 4f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(11f, 12f)
                lineTo(13f, 12f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(11f, 14f)
                lineTo(13f, 14f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Send: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(2f, 12f)
                lineTo(22f, 2f)
                lineTo(14f, 22f)
                lineTo(22f, 2f)
                lineTo(2f, 12f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Stop: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(6f, 6f)
                lineTo(18f, 6f)
                lineTo(18f, 18f)
                lineTo(6f, 18f)
                lineTo(6f, 6f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Refresh: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(4f, 12f)
                lineTo(8f, 4f)
                lineTo(16f, 4f)
                lineTo(20f, 12f)
                lineTo(16f, 14f)
                lineTo(8f, 14f)
                lineTo(4f, 22f)
                lineTo(8f, 22f)
                lineTo(4f, 12f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Plus: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(12f, 4f)
                lineTo(12f, 20f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(4f, 12f)
                lineTo(20f, 12f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(5f, 5f)
                lineTo(19f, 19f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(19f, 5f)
                lineTo(5f, 19f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Copy: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(4f, 4f)
                lineTo(12f, 4f)
                lineTo(12f, 18f)
                lineTo(4f, 18f)
                lineTo(4f, 4f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(6f, 6f)
                lineTo(16f, 6f)
                lineTo(16f, 18f)
                lineTo(6f, 18f)
                lineTo(6f, 6f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Delete: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(6f, 4f)
                lineTo(18f, 4f)
                lineTo(18f, 8f)
                lineTo(20f, 8f)
                lineTo(20f, 20f)
                lineTo(4f, 20f)
                lineTo(4f, 8f)
                lineTo(6f, 4f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(8f, 8f)
                lineTo(16f, 8f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(10f, 8f)
                lineTo(10f, 6f)
                lineTo(14f, 6f)
                lineTo(14f, 8f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Reveal: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(2f, 12f)
                lineTo(22f, 12f)
                lineTo(12f, 22f)
                lineTo(2f, 12f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(2f, 12f)
                lineTo(12f, 2f)
                lineTo(22f, 12f)
                lineTo(2f, 12f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Split: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(3f, 4f)
                lineTo(11f, 4f)
                lineTo(11f, 20f)
                lineTo(3f, 20f)
                lineTo(3f, 4f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(13f, 4f)
                lineTo(21f, 4f)
                lineTo(21f, 20f)
                lineTo(13f, 20f)
                lineTo(13f, 4f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Microphone: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(8f, 2f)
                lineTo(16f, 2f)
                lineTo(16f, 12f)
                lineTo(22f, 18f)
                lineTo(2f, 18f)
                lineTo(8f, 12f)
                lineTo(8f, 2f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(6f, 18f)
                lineTo(18f, 18f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Document: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(6f, 2f)
                lineTo(14f, 2f)
                lineTo(18f, 6f)
                lineTo(18f, 24f)
                lineTo(6f, 24f)
                lineTo(6f, 2f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(14f, 2f)
                lineTo(14f, 6f)
                lineTo(18f, 6f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(8f, 10f)
                lineTo(16f, 10f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(8f, 14f)
                lineTo(16f, 14f)
            }, fillColor = Color.Black)
            path(PathBuilder().apply {
                moveTo(8f, 18f)
                lineTo(14f, 18f)
            }, fillColor = Color.Black)
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            width = 24.dp, height = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(PathBuilder().apply {
                moveTo(4f, 12f)
                lineTo(9f, 17f)
                lineTo(20f, 6f)
            }, fillColor = Color.Black)
        }.build()
    }
}
