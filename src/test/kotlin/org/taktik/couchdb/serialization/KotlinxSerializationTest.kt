package org.taktik.couchdb.serialization

import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.taktik.couchdb.entity.ActiveTask
import org.taktik.couchdb.entity.Attachment
import org.taktik.couchdb.entity.AttachmentResult
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.entity.DatabaseCompactionTask
import org.taktik.couchdb.entity.DatabaseInfo
import org.taktik.couchdb.entity.DatabaseInfoWrapper
import org.taktik.couchdb.entity.DesignDocument
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.couchdb.entity.Indexer
import org.taktik.couchdb.entity.Membership
import org.taktik.couchdb.entity.NullKey
import org.taktik.couchdb.entity.Qnwr
import org.taktik.couchdb.entity.ReplicateCommand
import org.taktik.couchdb.entity.ReplicatorDocument
import org.taktik.couchdb.entity.ReplicationTask
import org.taktik.couchdb.entity.Scheduler
import org.taktik.couchdb.entity.Security
import org.taktik.couchdb.entity.ShardInfo
import org.taktik.couchdb.entity.Sizes
import org.taktik.couchdb.entity.UnsupportedTask
import org.taktik.couchdb.entity.View
import org.taktik.couchdb.entity.ViewCompactionTask
import org.taktik.couchdb.BulkUpdateResult
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ReplicatorResponse
import org.taktik.couchdb.mango.MangoDesignDocument
import java.time.Instant
import java.time.ZonedDateTime

/**
 * Tests for kotlinx-serialization support alongside Jackson.
 * These tests verify that entity classes can be serialized and deserialized
 * using the configured [CouchDbJson] instance.
 */
class KotlinxSerializationTest {

    @Test
    fun `IdAndRev round-trip`() {
        val original = IdAndRev("doc-1", "1-abc")
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<IdAndRev>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `AttachmentResult round-trip`() {
        val original = AttachmentResult("doc-1", "2-def", true)
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<AttachmentResult>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `Attachment round-trip with SerialName mappings`() {
        val original = Attachment(
            contentType = "application/pdf",
            dataBase64 = "dGVzdA==",
            isStub = false,
            revpos = 3,
            digest = "md5-abc",
            length = 42
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"content_type\""))
        assertTrue(json.contains("\"data\""))
        assertTrue(json.contains("\"stub\""))
        val decoded = CouchDbJson.decodeFromString<Attachment>(json)
        assertEquals(original.contentType, decoded.contentType)
        assertEquals(original.dataBase64, decoded.dataBase64)
        assertEquals(original.isStub, decoded.isStub)
        assertEquals(original.length, decoded.length)
    }

    @Test
    fun `Attachment Transient fields are excluded`() {
        val original = Attachment(id = "att-1", contentLength = 100L, isStub = true)
        val json = CouchDbJson.encodeToString(original)
        assertFalse(json.contains("\"contentLength\""))
    }

    @Test
    fun `DatabaseInfo round-trip with SerialName`() {
        val original = DatabaseInfo(
            dbName = "test-db",
            purgeSeq = "0",
            updateSeq = "100",
            sizes = Sizes(file = 1000, external = 800, active = 900),
            docDelCount = 5,
            docCount = 42,
            diskFormatVersion = 8,
            compactRunning = false,
            cluster = Qnwr(q = 8, n = 3, w = 2, r = 2),
            instanceStartTime = 0
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"db_name\""))
        assertTrue(json.contains("\"purge_seq\""))
        assertTrue(json.contains("\"doc_count\""))
        val decoded = CouchDbJson.decodeFromString<DatabaseInfo>(json)
        assertEquals(original.dbName, decoded.dbName)
        assertEquals(original.docCount, decoded.docCount)
        assertEquals(original.sizes, decoded.sizes)
        assertEquals(original.cluster, decoded.cluster)
    }

    @Test
    fun `DatabaseInfoWrapper round-trip`() {
        val wrapper = DatabaseInfoWrapper(
            info = DatabaseInfo(
                dbName = "test", purgeSeq = null, updateSeq = null,
                sizes = Sizes(0, 0, 0), docDelCount = null, docCount = null,
                diskFormatVersion = null, compactRunning = null,
                cluster = Qnwr(null, null, null, null), instanceStartTime = null
            ),
            error = null
        )
        val json = CouchDbJson.encodeToString(wrapper)
        val decoded = CouchDbJson.decodeFromString<DatabaseInfoWrapper>(json)
        assertNotNull(decoded.info)
        assertEquals("test", decoded.info?.dbName)
    }

    @Test
    fun `Security round-trip`() {
        val original = Security(
            admins = Security.Right(names = setOf("admin"), roles = setOf("_admin")),
            members = Security.Right(names = setOf("user1"), roles = setOf("reader"))
        )
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<Security>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `ShardInfo round-trip`() {
        val original = ShardInfo(shards = mapOf("00000000-ffffffff" to setOf("node1", "node2")))
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<ShardInfo>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `Membership round-trip with SerialName`() {
        val original = Membership(
            allNodes = listOf("node1@host", "node2@host"),
            clusterNodes = listOf("node1@host")
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"all_nodes\""))
        assertTrue(json.contains("\"cluster_nodes\""))
        val decoded = CouchDbJson.decodeFromString<Membership>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `DesignDocument round-trip`() {
        val original = DesignDocument(
            id = "_design/test",
            rev = "1-abc",
            language = "javascript",
            views = mapOf("by_name" to View(map = "function(doc) { emit(doc.name); }", reduce = "_count"))
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"_id\""))
        assertTrue(json.contains("\"_rev\""))
        val decoded = CouchDbJson.decodeFromString<DesignDocument>(json)
        assertEquals(original.id, decoded.id)
        assertEquals(original.rev, decoded.rev)
        assertEquals(original.views.size, decoded.views.size)
    }

    @Test
    fun `View round-trip`() {
        val original = View(map = "function(doc) { emit(doc._id); }", reduce = "_count")
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<View>(json)
        assertEquals(original.map, decoded.map)
        assertEquals(original.reduce, decoded.reduce)
    }

    @Test
    fun `User entity round-trip`() {
        val original = org.taktik.couchdb.entity.User(
            id = "user:admin",
            rev = "1-abc",
            name = "Admin",
            password = "secret",
            roles = listOf("admin")
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"_id\""))
        assertTrue(json.contains("\"_rev\""))
        val decoded = CouchDbJson.decodeFromString<org.taktik.couchdb.entity.User>(json)
        assertEquals(original.id, decoded.id)
        assertEquals(original.name, decoded.name)
        assertEquals(original.roles, decoded.roles)
    }

    @Test
    fun `ReplicateCommand round-trip`() {
        val original = ReplicateCommand(
            id = "rep-1",
            continuous = true,
            createTarget = true,
            source = ReplicateCommand.Remote(
                url = "http://source:5984/db",
                auth = ReplicateCommand.Remote.Authentication(
                    basic = ReplicateCommand.Remote.Authentication.Basic("user", "pass")
                )
            ),
            target = ReplicateCommand.Remote(url = "http://target:5984/db")
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"_id\""))
        assertTrue(json.contains("\"create_target\""))
        val decoded = CouchDbJson.decodeFromString<ReplicateCommand>(json)
        assertEquals(original.id, decoded.id)
        assertEquals(original.continuous, decoded.continuous)
        assertEquals(original.source.url, decoded.source.url)
    }

    @Test
    fun `BulkUpdateResult round-trip`() {
        val original = BulkUpdateResult("doc-1", "2-abc", true, null, null)
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<BulkUpdateResult>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `DocIdentifier round-trip`() {
        val original = DocIdentifier("doc-1", "1-rev")
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<DocIdentifier>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `ReplicatorResponse round-trip`() {
        val original = ReplicatorResponse(ok = true, id = "rep-1", rev = "1-abc")
        val json = CouchDbJson.encodeToString(original)
        val decoded = CouchDbJson.decodeFromString<ReplicatorResponse>(json)
        assertEquals(original, decoded)
    }

    @Test
    fun `Instant serialization as millisecond timestamp`() {
        val instant = Instant.ofEpochSecond(1609459200, 500000000)
        val json = CouchDbJson.encodeToString(InstantAsTimestampSerializer, instant)
        val decoded = CouchDbJson.decodeFromString(InstantAsTimestampSerializer, json)
        assertEquals(instant, decoded)
    }

    @Test
    fun `ZonedDateTime serialization as ISO string`() {
        val zdt = ZonedDateTime.parse("2021-01-01T00:00:00+01:00[Europe/Paris]")
        val json = CouchDbJson.encodeToString(ZonedDateTimeAsIsoSerializer, zdt)
        val decoded = CouchDbJson.decodeFromString(ZonedDateTimeAsIsoSerializer, json)
        assertEquals(zdt, decoded)
    }

    @Test
    fun `ComplexKey serialization as JSON array`() {
        val key = ComplexKey.of("hello", 42, null, true)
        val json = CouchDbJson.encodeToString(ComplexKeySerializer, key)
        assertEquals("[\"hello\",42,null,true]", json)
        val decoded = CouchDbJson.decodeFromString(ComplexKeySerializer, json)
        assertEquals(key, decoded)
    }

    @Test
    fun `ComplexKey with empty object and empty array`() {
        val key = ComplexKey.of("test", ComplexKey.emptyObject(), ComplexKey.emptyArray())
        val json = CouchDbJson.encodeToString(ComplexKeySerializer, key)
        assertTrue(json.contains("{}"))
        assertTrue(json.contains("[]"))
    }

    @Test
    fun `NullKey serialization`() {
        val json = CouchDbJson.encodeToString(NullKeySerializer, NullKey)
        assertEquals("null", json)
    }

    @Test
    fun `ReplicationState serialization`() {
        val state = Scheduler.ReplicationState.RUNNING
        val json = CouchDbJson.encodeToString(ReplicationStateSerializer, state)
        assertEquals("\"running\"", json)
        val decoded = CouchDbJson.decodeFromString(ReplicationStateSerializer, json)
        assertEquals(Scheduler.ReplicationState.RUNNING, decoded)
    }

    @Test
    fun `ReplicatorDocument round-trip with ZonedDateTime`() {
        val original = ReplicatorDocument(
            id = "rep-1",
            rev = "1-abc",
            source = ReplicateCommand.Remote(url = "http://source/db"),
            target = ReplicateCommand.Remote(url = "http://target/db"),
            replicationState = "running"
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"_id\""))
        assertTrue(json.contains("\"_replication_state\""))
        val decoded = CouchDbJson.decodeFromString<ReplicatorDocument>(json)
        assertEquals(original.id, decoded.id)
        assertEquals(original.replicationState, decoded.replicationState)
    }

    @Test
    fun `Scheduler Docs deserialization with SerialName`() {
        val jsonStr = """
            {
                "total_rows": 1,
                "offset": 0,
                "docs": [{
                    "database": "test-db",
                    "doc_id": "rep-1",
                    "state": "running",
                    "error_count": 0
                }]
            }
        """.trimIndent()
        val decoded = CouchDbJson.decodeFromString<Scheduler.Docs>(jsonStr)
        assertEquals(1, decoded.totalRows)
        assertEquals(1, decoded.docs.size)
        assertEquals("test-db", decoded.docs[0].database)
        assertEquals(Scheduler.ReplicationState.RUNNING, decoded.docs[0].state)
    }

    @Test
    fun `ignoreUnknownKeys works correctly`() {
        val jsonStr = """{"id":"doc-1","rev":"1-abc","unknown_field":"value"}"""
        val decoded = CouchDbJson.decodeFromString<IdAndRev>(jsonStr)
        assertEquals("doc-1", decoded.id)
        assertEquals("1-abc", decoded.rev)
    }

    @Test
    fun `MangoDesignDocument round-trip`() {
        val original = MangoDesignDocument(
            id = "_design/test_mango",
            rev = "1-abc",
            language = "query"
        )
        val json = CouchDbJson.encodeToString(original)
        assertTrue(json.contains("\"_id\""))
        val decoded = CouchDbJson.decodeFromString<MangoDesignDocument>(json)
        assertEquals(original.id, decoded.id)
        assertEquals(original.language, decoded.language)
    }

    @Test
    fun `ActiveTask polymorphic serialization - Indexer`() {
        val indexer = Indexer(
            pid = "<0.123.0>",
            database = "test-db",
            node = "nonode@nohost",
            design_document = "_design/test",
            total_changes = 100.0,
            completedChanges = 50.0
        )
        val json = CouchDbJson.encodeToString<ActiveTask>(indexer)
        assertTrue(json.contains("\"type\":\"indexer\""))
        val decoded = CouchDbJson.decodeFromString<ActiveTask>(json)
        assertTrue(decoded is Indexer)
        assertEquals("test-db", (decoded as Indexer).database)
    }

    @Test
    fun `ActiveTask polymorphic serialization - ReplicationTask`() {
        val task = ReplicationTask(
            pid = "<0.456.0>",
            replication_id = "rep-1",
            doc_id = "doc-1",
            node = "nonode@nohost",
            continuous = true,
            changes_pending = 10.0,
            doc_write_failures = 0.0,
            docs_read = 100.0,
            docs_written = 100.0,
            missing_revisions_found = 0.0,
            revisions_checked = 100.0,
            source = "http://source/db",
            target = "http://target/db",
            source_seq = "100",
            checkpointed_source_seq = "95",
            checkpoint_interval = 30000.0
        )
        val json = CouchDbJson.encodeToString<ActiveTask>(task)
        assertTrue(json.contains("\"type\":\"replication\""))
        val decoded = CouchDbJson.decodeFromString<ActiveTask>(json)
        assertTrue(decoded is ReplicationTask)
        assertEquals(true, (decoded as ReplicationTask).continuous)
    }

    @Test
    fun `ActiveTask polymorphic serialization - DatabaseCompactionTask`() {
        val task = DatabaseCompactionTask(
            pid = "<0.789.0>",
            database = "test-db",
            total_changes = 50.0,
            completed_changes = 25.0
        )
        val json = CouchDbJson.encodeToString<ActiveTask>(task)
        assertTrue(json.contains("\"type\":\"database_compaction\""))
        val decoded = CouchDbJson.decodeFromString<ActiveTask>(json)
        assertTrue(decoded is DatabaseCompactionTask)
    }

    @Test
    fun `ActiveTask polymorphic serialization - ViewCompactionTask`() {
        val task = ViewCompactionTask(
            pid = "<0.999.0>",
            database = "test-db",
            design_document = "_design/test",
            phase = "compact",
            total_changes = 30.0,
            view = 1.0,
            completed_changes = 15.0
        )
        val json = CouchDbJson.encodeToString<ActiveTask>(task)
        assertTrue(json.contains("\"type\":\"view_compaction\""))
        val decoded = CouchDbJson.decodeFromString<ActiveTask>(json)
        assertTrue(decoded is ViewCompactionTask)
    }

    @Test
    fun `ActiveTask polymorphic serialization - UnsupportedTask`() {
        val task = UnsupportedTask(
            pid = "<0.111.0>",
            progress = 50
        )
        val json = CouchDbJson.encodeToString<ActiveTask>(task)
        assertTrue(json.contains("\"type\":\"unsupported\""))
        val decoded = CouchDbJson.decodeFromString<ActiveTask>(json)
        assertTrue(decoded is UnsupportedTask)
    }

    @Test
    fun `ActiveTask with Instant fields`() {
        val now = Instant.now()
        val task = UnsupportedTask(
            pid = "<0.111.0>",
            started_on = now,
            updated_on = now
        )
        val json = CouchDbJson.encodeToString<ActiveTask>(task)
        val decoded = CouchDbJson.decodeFromString<ActiveTask>(json)
        assertTrue(decoded is UnsupportedTask)
        assertEquals(now, decoded.started_on)
        assertEquals(now, decoded.updated_on)
    }

    @Test
    fun `CouchDbJson configuration matches Jackson defaults`() {
        // Verify null fields are not encoded (matching @JsonInclude NON_NULL)
        val result = BulkUpdateResult("id-1", null, true, null, null)
        val json = CouchDbJson.encodeToString(result)
        assertFalse(json.contains("\"rev\""))
        assertFalse(json.contains("\"error\""))
        assertFalse(json.contains("\"reason\""))
    }
}
