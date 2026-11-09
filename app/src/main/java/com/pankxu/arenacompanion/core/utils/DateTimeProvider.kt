package com.pankxu.arenacompanion.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/** Abstraction for time (testable). */
interface DateTimeProvider {
    fun nowMillis(): Long
    fun nowIso(): String
    fun nowZoned(zone: ZoneId): ZonedDateTime
}

class DefaultDateTimeProvider : DateTimeProvider {
    override fun nowMillis(): Long = Instant.now().toEpochMilli()
    override fun nowIso(): String = Instant.now().toString()
    override fun nowZoned(zone: ZoneId): ZonedDateTime = ZonedDateTime.now(zone)
}
