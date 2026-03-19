package org.taktik.couchdb.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigDecimal
import java.time.Instant

/**
 * kotlinx-serialization serializer for [Instant] that encodes/decodes as a millisecond timestamp
 * (matching the Jackson [org.taktik.couchdb.handlers.InstantSerializer] /
 * [org.taktik.couchdb.handlers.InstantDeserializer] behavior).
 */
object InstantAsTimestampSerializer : KSerializer<Instant> {
    private val BD_1000 = BigDecimal.valueOf(1000)
    private val BD_1000000 = BigDecimal.valueOf(1000000)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.Instant", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: Instant) {
        val millis = BigDecimal.valueOf(1000L * value.epochSecond)
            .add(BigDecimal.valueOf(value.nano.toLong()).divide(BD_1000000))
        if (encoder is JsonEncoder) {
            encoder.encodeJsonElement(JsonPrimitive(millis))
        } else {
            encoder.encodeDouble(millis.toDouble())
        }
    }

    override fun deserialize(decoder: Decoder): Instant {
        val bd = if (decoder is JsonDecoder) {
            BigDecimal(decoder.decodeJsonElement().jsonPrimitive.content)
        } else {
            BigDecimal.valueOf(decoder.decodeDouble())
        }
        return Instant.ofEpochSecond(
            bd.divide(BD_1000).toLong(),
            bd.remainder(BD_1000).multiply(BD_1000000).toLong()
        )
    }
}
