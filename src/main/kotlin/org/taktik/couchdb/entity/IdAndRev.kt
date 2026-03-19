package org.taktik.couchdb.entity

import kotlinx.serialization.Serializable
import java.io.Serializable as JSerializable

@Serializable
data class IdAndRev(val id: String, val rev: String?) : JSerializable
