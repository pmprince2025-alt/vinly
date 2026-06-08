package com.vinyl.app.di

import com.vinyl.app.data.settings.SettingsDataSource
import com.vinyl.app.data.settings.SettingsRepositoryImpl
import com.vinyl.app.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataSource: SettingsDataSource
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataSource)
    }
}
