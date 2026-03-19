package org.taktik.couchdb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.taktik.couchdb.handlers.ReplicationStateDeserializer
import org.taktik.couchdb.handlers.ZonedDateTimeDeserializer
import java.time.ZonedDateTime

interface Scheduler {
    interface ListResult {
        val totalRows: Int
        val offset: Int
    }
    @Serializable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
        data class Docs(
            @SerialName("total_rows") @JsonProperty("total_rows") override val totalRows: Int,
            override val offset: Int,
            val docs: List<Doc>
    ) : ListResult {
        @Serializable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
                data class Doc (
                val database : String? = null,
                @SerialName("doc_id") @JsonProperty("doc_id") val docId : String? = null,
                val id : String? = null,
                val node : String? = null,
                val source : String? = null,
                val target : String? = null,
                @JsonDeserialize(using = ReplicationStateDeserializer::class) val state : ReplicationState? = null,
                val info : Info? = null,
                @SerialName("error_count") @JsonProperty("error_count") val errorCount : Int? = null,
                @SerialName("last_updated") @JsonProperty("last_updated")
                @JsonDeserialize(using = ZonedDateTimeDeserializer::class)
                @Contextual
                val lastUpdated : ZonedDateTime? = null,
                @SerialName("start_time") @JsonProperty("start_time")
                @JsonDeserialize(using = ZonedDateTimeDeserializer::class)
                @Contextual
                val startTime : ZonedDateTime? = null,
                @SerialName("source_proxy") @JsonProperty("source_proxy") val sourceProxy : String? = null,
                @SerialName("target_proxy") @JsonProperty("target_proxy") val targetProxy : String? = null
        )
    }

    @Serializable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
        data class Info (
            @SerialName("revisions_checked") @JsonProperty("revisions_checked") val revisionsChecked : Int? = null,
            @SerialName("missing_revisions_found") @JsonProperty("missing_revisions_found") val missingRevisionsFound : Int? = null,
            @SerialName("docs_read") @JsonProperty("docs_read") val docsRead : Int? = null,
            @SerialName("docs_written") @JsonProperty("docs_written") val docsWritten : Int? = null,
            @SerialName("changes_pending") @JsonProperty("changes_pending") val changesPending : Int? = null,
            @SerialName("doc_write_failures") @JsonProperty("doc_write_failures") val docWriteFailures : Int? = null,
            @SerialName("checkpointed_source_seq") @JsonProperty("checkpointed_source_seq") val checkpointedSourceSeq : String? = null,
            @SerialName("source_seq") @JsonProperty("source_seq") val sourceSeq : String? = null,
            @SerialName("through_seq") @JsonProperty("through_seq") val throughSeq : String? = null,
            val error : String? = null
    )

    interface State {
        val healthy: Boolean
        val terminal: Boolean
    }

    @Serializable(with = org.taktik.couchdb.serialization.ReplicationStateSerializer::class)
    enum class ReplicationState() : State {
        INITIALIZING {
            override val healthy = true
            override val terminal = false
        },
        ERROR {
            override val healthy = false
            override val terminal = false
        },
        FAILED {
            override val healthy = false
            override val terminal = true
        },
        RUNNING {
            override val healthy = true
            override val terminal = false
        },
        PENDING {
            override val healthy = true
            override val terminal = false
        },
        CRASHING {
            override val healthy = false
            override val terminal = false
        },
        COMPLETED {
            override val healthy = true
            override val terminal = true
        };
        companion object {
            fun fromString(value: String?): ReplicationState = when(value) {
                "initializing" -> INITIALIZING
                "error" -> ERROR
                "failed" -> FAILED
                "running" -> RUNNING
                "pending" -> PENDING
                "crashing" -> CRASHING
                "completed" -> COMPLETED
                else -> FAILED
            }
        }

    }

    @Serializable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
        data class Jobs(
            @SerialName("total_rows") @JsonProperty("total_rows") override val totalRows: Int,
            override val offset: Int,
            val jobs: List<Job>
    ) : ListResult {
        @Serializable
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
                data class Job(
                val database: String? = null,
                @SerialName("doc_id") @JsonProperty("doc_id") val docId: String? = null,
                val id: String? = null,
                val node: String? = null,
                val source: String? = null,
                val target: String? = null,
                val pid: String? = null,
                val user: String? = null,
                val info: Info? = null,
                val history: List<History>? = null,
                @SerialName("start_time") @JsonProperty("start_time")
                @JsonDeserialize(using = ZonedDateTimeDeserializer::class)
                @Contextual
                val startTime: ZonedDateTime? = null
        ) {
            @Serializable
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonIgnoreProperties(ignoreUnknown = true)
                        data class History(
                    @JsonDeserialize(using = ZonedDateTimeDeserializer::class) @Contextual val timestamp: ZonedDateTime? = null,
                    val type: String? = null,
                    val reason: String? = null
            )
        }
    }
}
