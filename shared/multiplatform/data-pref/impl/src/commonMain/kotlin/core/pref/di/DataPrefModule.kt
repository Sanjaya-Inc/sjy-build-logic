package core.pref.di

import core.pref.createDataStore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("core.pref")
object DataPrefModule {

    @Single
    fun provideDataStore() = createDataStore("core.preferences_pb")
}
