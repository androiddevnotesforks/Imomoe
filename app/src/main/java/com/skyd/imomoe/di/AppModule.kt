package com.skyd.imomoe.di

import com.skyd.imomoe.util.update.AppUpdateHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAppUpdateHelper(): AppUpdateHelper {
        return AppUpdateHelper.instance
    }
}