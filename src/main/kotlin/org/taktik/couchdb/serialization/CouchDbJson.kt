package org.taktik.couchdb.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.taktik.couchdb.entity.ActiveTask
import org.taktik.couchdb.entity.DatabaseCompactionTask
import org.taktik.couchdb.entity.Indexer
import org.taktik.couchdb.entity.ReplicationTask
import org.taktik.couchdb.entity.UnsupportedTask
import org.taktik.couchdb.entity.ViewCompactionTask

/**
 * A [SerializersModule] containing all custom serializers required for CouchDB entity serialization
 * using kotlinx-serialization. This module registers:
 * - Contextual serializers for [java.time.Instant] and [java.time.ZonedDateTime]
 * - Polymorphic serializers for the [ActiveTask] sealed class hierarchy
 *
 * Use this module when building your own [Json] instance, or use the pre-configured [CouchDbJson].
 */
val CouchDbSerializersModule = SerializersModule {
    contextual(java.time.Instant::class, InstantAsTimestampSerializer)
    contextual(java.time.ZonedDateTime::class, ZonedDateTimeAsIsoSerializer)

    polymorphic(ActiveTask::class) {
        subclass(UnsupportedTask::class)
        subclass(DatabaseCompactionTask::class)
        subclass(ViewCompactionTask::class)
        subclass(Indexer::class)
        subclass(ReplicationTask::class)
    }
}

/**
 * A pre-configured [Json] instance for serializing/deserializing CouchDB entities using
 * kotlinx-serialization. This configuration mirrors the behavior of the Jackson ObjectMapper
 * used by the CouchDB client:
 * - Ignores unknown keys (equivalent to `@JsonIgnoreProperties(ignoreUnknown = true)`)
 * - Encodes defaults (so that default values are always serialized)
 * - Does not encode null values (equivalent to `@JsonInclude(JsonInclude.Include.NON_NULL)`)
 * - Uses "type" as the class discriminator for polymorphic types
 *
 * Example usage:
 * ```kotlin
 * val json = CouchDbJson.encodeToString(entity)
 * val entity = CouchDbJson.decodeFromString<MyEntity>(json)
 * ```
 */
val CouchDbJson = Json {
    serializersModule = CouchDbSerializersModule
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
    classDiscriminator = "type"
}
