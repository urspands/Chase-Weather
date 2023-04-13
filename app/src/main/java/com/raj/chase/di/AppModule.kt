package com.raj.chase.di

import com.raj.chase.api.NetworkApi
import com.raj.chase.repository.DataRepository
import com.raj.chase.repository.DataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    var client = OkHttpClient.Builder()
        .addInterceptor(InternetPermissionInterceptor())
        .build()
    @Provides
    @Singleton
    fun provideRepository(networkApi: NetworkApi): DataRepository =
        DataRepositoryImpl(networkApi)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(NetworkApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNetworkApi(retrofit: Retrofit): NetworkApi =
        retrofit.create(NetworkApi::class.java)
}

class InternetPermissionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            // Proceed with the network request
            chain.proceed(chain.request())
        } catch (e: SecurityException) {
            // Handle the exception here
            throw IOException("Internet permission is required", e)
        }
    }

}