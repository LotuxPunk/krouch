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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.Serializable as JSerializable

@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DatabaseInfoWrapper(val info: DatabaseInfo?, val error: String?)

@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DatabaseInfo(
        @SerialName("db_name") @JsonProperty("db_name") val dbName: String,
        @SerialName("purge_seq") @JsonProperty("purge_seq") val purgeSeq: String?,
        @SerialName("update_seq") @JsonProperty("update_seq") val updateSeq: String?,
        val sizes: Sizes,
        @SerialName("doc_del_count") @JsonProperty("doc_del_count") val docDelCount: Long?,
        @SerialName("doc_count") @JsonProperty("doc_count") val docCount: Long?,
        @SerialName("disk_format_version") @JsonProperty("disk_format_version") val diskFormatVersion: Long?,
        @SerialName("compact_running") @JsonProperty("compact_running") val compactRunning: Boolean?,
        val cluster: Qnwr,
        @SerialName("instance_start_time") @JsonProperty("instance_start_time") val instanceStartTime: Long?,
) : JSerializable

@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Sizes(
        val file: Long,
        val external: Long,
        val active: Long,
) : JSerializable

@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Qnwr(
        val q: Int?,
        val n: Int?,
        val w: Int?,
        val r: Int?,
) : JSerializable
