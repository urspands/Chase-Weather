package com.raj.chase.di

import com.raj.chase.api.NetworkApi
import com.raj.chase.repository.DataRepository
import com.raj.chase.repository.DataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRepository(networkApi: NetworkApi): DataRepository =
        DataRepositoryImpl(networkApi)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(NetworkApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNetworkApi(retrofit: Retrofit): NetworkApi =
        retrofit.create(NetworkApi::class.java)
}