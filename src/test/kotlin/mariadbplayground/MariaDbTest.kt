package mariadbplayground

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.sql.DriverManager
import kotlin.test.assertEquals

object MariaDbTest : Spek({
    val dbPorts = arrayOf("13306", "13307", "13308")
    val dbHost = "127.0.0.1"
    val dbUser = "root"
    val dbPass = "admin"
    val db = "test"

    beforeEachTest {
        DriverManager.getConnection("jdbc:mariadb://$dbHost:${dbPorts[0]}/$db", dbUser, dbPass)
                .createStatement().execute("TRUNCATE TABLE test_user")
    }

    fun runTest(failoverOption: String, input: String): String {
        val url = "jdbc:mariadb:$failoverOption://" +
                "${dbPorts.map { "$dbHost:$it" }.joinToString(separator = ",")}/$db"
        println(url)
        repeat(10) { i ->
            Thread.sleep(500)
            DriverManager.getConnection(url, dbUser, dbPass).use { conn ->
                conn.isReadOnly = i % 2 == 1
                conn.createStatement().use { st ->
                    st.executeQuery("SELECT @@hostname").use {
                        it.next()
                        println("$i${if (conn.isReadOnly) "(ReadOnly)" else ""}: ${it.getString(1)}");
                    }
                }
            }
        }
        return DriverManager.getConnection(url, dbUser, dbPass).use { conn ->
            conn.prepareStatement("""INSERT INTO test_user VALUES (?)""").use { ps ->
                ps.setString(1, input)
                ps.executeUpdate()
            }
            conn.createStatement().use { st ->
                st.executeQuery("SELECT name FROM test_user").use {
                    it.next()
                    it.getString("name")
                }
            }
        }
    }

    describe("MariaDB connector/J") {
        it("loadbalance") {
            assertEquals("hoge", runTest("loadbalance", "hoge"))
        }
        it("failover") {
            assertEquals("hoge", runTest("failover", "hoge"))
        }
        it("sequential") {
            assertEquals("hoge", runTest("sequential", "hoge"))
        }
        it("replication") {
            assertEquals("hoge", runTest("replication", "hoge"))
        }
    }
})
