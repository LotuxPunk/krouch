package org.taktik.couchdb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Membership(
	@SerialName("all_nodes") @field:JsonProperty("all_nodes") val allNodes: List<String> = emptyList(),
	@SerialName("cluster_nodes") @field:JsonProperty("cluster_nodes") val clusterNodes: List<String> = emptyList()
)