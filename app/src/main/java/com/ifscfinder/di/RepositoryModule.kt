package com.ifscfinder.di

import com.ifscfinder.data.repository.ChallengeRepositoryImpl
import com.ifscfinder.data.repository.GameRepositoryImpl
import com.ifscfinder.data.repository.PreferencesRepositoryImpl
import com.ifscfinder.data.repository.ProgressionRepositoryImpl
import com.ifscfinder.domain.repository.ChallengeRepository
import com.ifscfinder.domain.repository.GameRepository
import com.ifscfinder.domain.repository.PreferencesRepository
import com.ifscfinder.domain.repository.ProgressionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository
    @Binds @Singleton abstract fun bindChallengeRepository(impl: ChallengeRepositoryImpl): ChallengeRepository
    @Binds @Singleton abstract fun bindProgressionRepository(impl: ProgressionRepositoryImpl): ProgressionRepository
    @Binds @Singleton abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
