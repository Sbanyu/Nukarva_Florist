package com.example.nukarva_florist.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.example.nukarva_florist.data.AppPreferences
import com.example.nukarva_florist.network.ApiService
import com.example.nukarva_florist.network.AuthInterceptor
import com.example.nukarva_florist.repository.AuthRepository
import com.example.nukarva_florist.repository.CategoryRepository
import com.example.nukarva_florist.repository.OtpRepository
import com.example.nukarva_florist.repository.ProductRepository
import com.example.nukarva_florist.repository.SplashRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        appPreferences: AppPreferences
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(appPreferences))
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        val retrofit = Retrofit.Builder()
//            .baseUrl("http://192.168.1.19:9090/") //LocalHost Xiomi
//            .baseUrl("http://192.168.100.46:9090/")
//            .baseUrl(getBaseUrl())
            .baseUrl("http://localhost:9090/")
//            .baseUrl("http://10.0.2.2:9090") //Emulator
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    fun provideAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepository(apiService)
    }

    @Provides
    fun provideOtpRepository(apiService: ApiService): OtpRepository {
        return OtpRepository(apiService)
    }

    @Provides
    fun provideCategoriesRepository(apiService: ApiService): CategoryRepository {
        return CategoryRepository(apiService)
    }

    @Provides
    fun provideProductsRepository(apiService: ApiService): ProductRepository {
        return ProductRepository(apiService)
    }
    @Provides
    @Singleton
    fun provideSplashRepository(appPreferences: AppPreferences): SplashRepository {
        return SplashRepository(appPreferences)
    }

    fun getBaseUrl(): String {
        return if (Build.FINGERPRINT.contains("generic")) {
            // Emulator
            "http://10.10.20.214:9090/"
        } else {
            // Real Device (local IP from Mac for backend)
            "http://192.168.161.11:9090/"
        }
    }
}

