package core.supabase.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan(
    "core.supabase"
)
object SupabaseModule
