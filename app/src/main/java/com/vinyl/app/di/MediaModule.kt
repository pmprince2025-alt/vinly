package com.vinyl.app.di

import android.content.Context
import com.vinyl.app.data.mediasession.MediaSessionDataSource
import com.vinyl.app.data.mediasession.MediaSessionDataSourceImpl
import com.vinyl.app.data.mediasession.MediaSessionRepositoryImpl
import com.vinyl.app.domain.repository.MediaSessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideMediaSessionDataSource(
        @ApplicationContext context: Context
    ): MediaSessionDataSource {
        return MediaSessionDataSourceImpl(context)
    }

    @Provides
    @Singleton
    fun provideMediaSessionRepository(
        dataSource: MediaSessionDataSource
    ): MediaSessionRepository {
        return MediaSessionRepositoryImpl(dataSource)
    }
}
