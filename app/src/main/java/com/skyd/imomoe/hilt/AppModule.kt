package com.skyd.imomoe.hilt

import com.skyd.imomoe.net.RetrofitManager
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
    fun provideRetrofitManager(): RetrofitManager {
        return RetrofitManager.get()
    }
}