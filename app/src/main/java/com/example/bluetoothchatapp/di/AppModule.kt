package com.example.bluetoothchatapp.di

import android.content.Context
import com.example.bluetoothchatapp.data.chat.AndroidBluetoothRepository
import com.example.bluetoothchatapp.domain.chat.BluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothRepository(@ApplicationContext context: Context): BluetoothRepository {
        return AndroidBluetoothRepository(context)
    }


}