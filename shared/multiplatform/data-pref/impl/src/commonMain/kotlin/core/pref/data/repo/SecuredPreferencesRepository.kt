package core.pref.data.repo

import core.pref.PreferenceRepository
import core.pref.SecuredPreferences
import org.koin.core.annotation.Single

@Single
@SecuredPreferences
internal class SecuredPreferencesRepository(
    private val delegated: PreferenceRepository = createSecuredRepository()
) : PreferenceRepository by delegated
