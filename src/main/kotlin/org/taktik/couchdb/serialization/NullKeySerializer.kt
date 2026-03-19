package org.taktik.couchdb.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import org.taktik.couchdb.entity.NullKey

/**
 * kotlinx-serialization serializer for [NullKey] that serializes to JSON null
 * (matching the Jackson [org.taktik.couchdb.handlers.JacksonNullKeySerializer] behavior).
 */
object NullKeySerializer : KSerializer<NullKey> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("NullKey")

    override fun serialize(encoder: Encoder, value: NullKey) {
        require(encoder is JsonEncoder) { "NullKeySerializer only supports JSON format" }
        encoder.encodeJsonElement(JsonNull)
    }

    override fun deserialize(decoder: Decoder): NullKey {
        require(decoder is JsonDecoder) { "NullKeySerializer only supports JSON format" }
        decoder.decodeJsonElement()
        return NullKey
    }
}
