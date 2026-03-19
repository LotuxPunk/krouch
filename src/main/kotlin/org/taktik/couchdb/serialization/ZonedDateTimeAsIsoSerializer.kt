package org.taktik.couchdb.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * kotlinx-serialization serializer for [ZonedDateTime] that encodes/decodes as an ISO 8601 string
 * (matching the Jackson [org.taktik.couchdb.handlers.ZonedDateTimeSerializer] /
 * [org.taktik.couchdb.handlers.ZonedDateTimeDeserializer] behavior).
 */
object ZonedDateTimeAsIsoSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString())
    }
}
