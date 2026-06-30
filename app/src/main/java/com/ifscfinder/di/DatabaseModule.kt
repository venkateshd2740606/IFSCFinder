package com.ifscfinder.di

import android.content.Context
import androidx.room.Room
import com.ifscfinder.data.local.database.IFSCFinderDatabase
import com.ifscfinder.data.local.database.dao.AchievementDao
import com.ifscfinder.data.local.database.dao.ChallengeDao
import com.ifscfinder.data.local.database.dao.EconomyDao
import com.ifscfinder.data.local.database.dao.GameDao
import com.ifscfinder.data.local.database.dao.IfscFavoriteDao
import com.ifscfinder.data.local.database.dao.ProfileDao
import com.ifscfinder.data.local.database.dao.StatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IFSCFinderDatabase =
        Room.databaseBuilder(context, IFSCFinderDatabase::class.java, "ifscfinder.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideGameDao(db: IFSCFinderDatabase): GameDao = db.gameDao()
    @Provides fun provideStatsDao(db: IFSCFinderDatabase): StatsDao = db.statsDao()
    @Provides fun provideAchievementDao(db: IFSCFinderDatabase): AchievementDao = db.achievementDao()
    @Provides fun provideChallengeDao(db: IFSCFinderDatabase): ChallengeDao = db.challengeDao()
    @Provides fun provideEconomyDao(db: IFSCFinderDatabase): EconomyDao = db.economyDao()
    @Provides fun provideProfileDao(db: IFSCFinderDatabase): ProfileDao = db.profileDao()
    @Provides fun provideIfscFavoriteDao(db: IFSCFinderDatabase): IfscFavoriteDao = db.ifscFavoriteDao()
}
