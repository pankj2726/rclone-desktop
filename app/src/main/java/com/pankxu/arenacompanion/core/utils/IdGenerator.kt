package com.pankxu.arenacompanion.core.utils

import java.util.UUID

object IdGenerator {
    fun uuid(): String = UUID.randomUUID().toString()
    fun shortId(): String = UUID.randomUUID().toString().substring(0, 8)
}
