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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 * @author henrik lundgren
 */
@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Attachment(
        @kotlinx.serialization.Transient @JsonIgnore val id: String? = null,
        @SerialName("content_type") @field:JsonProperty("content_type") val contentType: String? = null,
        @kotlinx.serialization.Transient @JsonIgnore val contentLength: Long? = null,
        @SerialName("data") @field:JsonProperty("data") val dataBase64: String? = null,
        @SerialName("stub") @field:JsonProperty("stub")
        val isStub: Boolean = false,
        val revpos: Int? = null,
        val digest: String? = null,
        val length: Long? = null
) {
    companion object {
        val EmptyStub = Attachment(isStub = true)
    }
}
