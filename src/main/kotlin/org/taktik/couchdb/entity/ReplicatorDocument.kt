/*
 *    Copyright 2020 Taktik SA
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.taktik.couchdb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.taktik.couchdb.CouchDbDocument
import org.taktik.couchdb.handlers.ZonedDateTimeDeserializer
import org.taktik.couchdb.handlers.ZonedDateTimeSerializer
import java.io.Serializable as JSerializable
import java.time.ZonedDateTime

@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ReplicatorDocument(
        @SerialName("_id") @JsonProperty("_id") override val id: String,
        @SerialName("_rev") @JsonProperty("_rev") override val rev: String?,
        val source: ReplicateCommand.Remote? = null,
        val target: ReplicateCommand.Remote? = null,
        val owner: String? = null,
        val create_target: Boolean? = null,
        val continuous: Boolean? = null,
        val doc_ids: List<String>? = null,
        @SerialName("_replication_state") @JsonProperty("_replication_state") val replicationState: String? = null,
        @SerialName("_replication_state_time") @JsonProperty("_replication_state_time")
        @JsonSerialize(using = ZonedDateTimeSerializer::class)
        @JsonDeserialize(using = ZonedDateTimeDeserializer::class)
        @Contextual
        val replicationStateTime: ZonedDateTime? = null,
        @SerialName("_replication_stats") @JsonProperty("_replication_stats") val replicationStats: ReplicationStats? = null,
        @SerialName("error_count") @JsonProperty("error_count") val errorCount: Int? = null,
        @SerialName("_revs_info") @JsonProperty("_revs_info") val revsInfo: List<Map<String,String>>? = null,
        @SerialName("rev_history") @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
) : CouchDbDocument {
    override fun withIdRev(id: String?, rev: String) = id?.let { this.copy(id = it, rev = rev) } ?: this.copy(rev = rev)
}

@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ReplicationStats(
        @SerialName("revisions_checked") @JsonProperty("revisions_checked") val revisionsChecked: Int? = null,
        @SerialName("missing_revisions_found") @JsonProperty("missing_revisions_found") val missingRevisionsFound: Int? = null,
        @SerialName("docs_read") @JsonProperty("docs_read") val docsRead: Int? = null,
        @SerialName("docs_written") @JsonProperty("docs_written") val docsWritten: Int? = null,
        @SerialName("changes_pending") @JsonProperty("changes_pending") val changesPending: Int? = null,
        @SerialName("doc_write_failures") @JsonProperty("doc_write_failures") val docWriteFailures: Int? = null,
        @SerialName("checkpointed_source_seq") @JsonProperty("checkpointed_source_seq") val checkpointedSourceSeq: String? = null,
        @SerialName("start_time") @JsonProperty("start_time")
        @JsonSerialize(using = ZonedDateTimeSerializer::class)
        @JsonDeserialize(using = ZonedDateTimeDeserializer::class)
        @Contextual
        val startTime: ZonedDateTime? = null,
        val error: String? = null
) : JSerializable
