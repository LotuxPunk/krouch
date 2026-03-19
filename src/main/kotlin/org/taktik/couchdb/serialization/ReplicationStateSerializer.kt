package org.taktik.couchdb.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.taktik.couchdb.entity.Scheduler

/**
 * kotlinx-serialization serializer for [Scheduler.ReplicationState] that deserializes
 * from lowercase string values (matching the Jackson
 * [org.taktik.couchdb.handlers.ReplicationStateDeserializer] behavior).
 */
object ReplicationStateSerializer : KSerializer<Scheduler.ReplicationState> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Scheduler.ReplicationState", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Scheduler.ReplicationState) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): Scheduler.ReplicationState {
        return Scheduler.ReplicationState.fromString(decoder.decodeString())
    }
}
