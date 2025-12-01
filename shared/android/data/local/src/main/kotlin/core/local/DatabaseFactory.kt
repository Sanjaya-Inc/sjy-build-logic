package core.local

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.koin.core.annotation.Single
import java.security.SecureRandom

@Single
class DatabaseFactory internal constructor(
    private val context: Context,
    private val databasePassphrasePref: DatabasePassphrasePref
) {
    /**
     * Creates a Room database instance with SQLCipher encryption and centralized configuration.
     *
     * @param klass The abstract class of the specific Database (defined in :app).
     * @param name The name of the database file on disk.
     * @param migrations A spread list of migrations to apply.
     */
    fun <T : RoomDatabase> create(
        klass: Class<T>,
        name: String,
        vararg migrations: Migration
    ): T {
        System.loadLibrary("sqlcipher")
        val passphraseBytes = runBlocking {
            val existing = databasePassphrasePref.data.firstOrNull()?.value
            if (!existing.isNullOrBlank()) {
                Base64.decode(existing, Base64.NO_WRAP)
            } else {
                val newBytes = ByteArray(32)
                SecureRandom().nextBytes(newBytes)
                val saveableString = Base64.encodeToString(newBytes, Base64.NO_WRAP)
                databasePassphrasePref.updateData {
                    DatabasePassphrase(saveableString)
                }
                newBytes
            }
        }
        val supportFactory = SupportOpenHelperFactory(passphraseBytes)
        return Room.databaseBuilder(context, klass, name)
            .openHelperFactory(supportFactory)
            .addMigrations(*migrations)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d("DatabaseFactory", "database created version: ${db.version}")
                }
            })
            .fallbackToDestructiveMigration(true)
            .build()
    }
}
