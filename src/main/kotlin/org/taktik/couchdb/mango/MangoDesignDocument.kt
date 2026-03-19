package org.taktik.couchdb.mango

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.taktik.couchdb.CouchDbDocument


@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class MangoDesignDocument(
    @SerialName("_id") @JsonProperty("_id") override var id: String,
    @SerialName("_rev") @JsonProperty("_rev") override var rev: String? = null,
    @SerialName("rev_history") @JsonProperty("rev_history") override val revHistory: Map<String, String> = mapOf(),
    val language: String? = "query",
    val views: MutableMap<String, MangoIndexView?> = mutableMapOf()
) : CouchDbDocument {
    override fun withIdRev(id: String?, rev: String) =
        if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)

    fun mergeWith(mangoDesignDocument: MangoDesignDocument, forceUpdate: Boolean): Boolean? {
        var changed = false
        for ((name, candidate) in mangoDesignDocument.views) {
            if (!views.containsKey<String?>(name)) {
                views[name] = candidate
                changed = true
            } else if (forceUpdate) {
                val existing = views[name]
                if (existing != candidate) {
                    views[name] = candidate
                    changed = true
                }
            }
        }
        return changed
    }
}

@Serializable
data class MangoIndexView(
    val map: MangoMap,
    val options: Options,
    val reduce: String = "_count"
)

@Serializable
data class MangoMap(
    val fields: Map<String, String>,
    val partial_filter_selector: PartialFilterSelector
)

@Serializable
data class Options(
    val def: Def
)

@Serializable
data class PartialFilterSelector(
    val java_type: JavaType
)

@Serializable
data class JavaType(
    val `$eq`: String
)

@Serializable
data class Def(
    val fields: List<String>,
    val partial_filter_selector: PartialFilterSelector
)
