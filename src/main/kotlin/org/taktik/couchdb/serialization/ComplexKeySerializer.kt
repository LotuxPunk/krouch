package org.taktik.couchdb.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import org.taktik.couchdb.entity.ComplexKey

/**
 * kotlinx-serialization serializer for [ComplexKey] that encodes/decodes as a JSON array
 * (matching the Jackson [org.taktik.couchdb.handlers.JacksonComplexKeySerializer] /
 * [org.taktik.couchdb.handlers.JacksonComplexKeyDeserializer] behavior).
 */
object ComplexKeySerializer : KSerializer<ComplexKey> {
    override val descriptor: SerialDescriptor =
        ListSerializer(JsonElement.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: ComplexKey) {
        require(encoder is JsonEncoder) { "ComplexKeySerializer only supports JSON format" }
        val elements = value.components.map { component ->
            when {
                component == null -> JsonNull
                component::class == Object::class -> JsonObject(emptyMap())
                component is String -> JsonPrimitive(component)
                component is Int -> JsonPrimitive(component)
                component is Long -> JsonPrimitive(component)
                component is Double -> JsonPrimitive(component)
                component is Float -> JsonPrimitive(component)
                component is Boolean -> JsonPrimitive(component)
                component is Number -> JsonPrimitive(component.toDouble())
                component is Array<*> && component.isEmpty() -> JsonArray(emptyList())
                else -> JsonPrimitive(component.toString())
            }
        }
        encoder.encodeJsonElement(JsonArray(elements))
    }

    override fun deserialize(decoder: Decoder): ComplexKey {
        require(decoder is JsonDecoder) { "ComplexKeySerializer only supports JSON format" }
        val array = decoder.decodeJsonElement() as JsonArray
        val components = array.map { element -> jsonElementToAny(element) }.toTypedArray()
        return ComplexKey.of(*components)
    }

    private fun jsonElementToAny(element: JsonElement): Any? = when (element) {
        is JsonNull -> null
        is JsonObject -> if (element.isEmpty()) ComplexKey.emptyObject() else element
        is JsonArray -> if (element.isEmpty()) ComplexKey.emptyArray() else element
        is JsonPrimitive -> when {
            element.isString -> element.content
            element.booleanOrNull != null -> element.boolean
            element.intOrNull != null -> element.int
            element.longOrNull != null -> element.long
            element.doubleOrNull != null -> element.double
            else -> element.content
        }
    }
}
