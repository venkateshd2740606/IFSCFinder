package com.ifscfinder.domain.repository

import com.ifscfinder.domain.model.Achievement
import com.ifscfinder.domain.model.ChallengeRecord
import com.ifscfinder.domain.model.ChallengeType
import com.ifscfinder.domain.model.IFSCFinderGame
import com.ifscfinder.domain.model.IFSCFinderLevel
import com.ifscfinder.domain.model.Difficulty
import com.ifscfinder.domain.model.EconomyState
import com.ifscfinder.domain.model.PuzzleProfile
import com.ifscfinder.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun createNewGame(difficulty: Difficulty, levelNumber: Int): IFSCFinderGame
    suspend fun createGameFromSeed(seed: Long, levelNumber: Int, difficulty: Difficulty): IFSCFinderGame
    suspend fun createTutorialGame(tutorialIndex: Int): IFSCFinderGame?
    suspend fun createEndlessGame(wave: Int): IFSCFinderGame
    suspend fun saveGame(game: IFSCFinderGame): Long
    suspend fun getGame(gameId: Long): IFSCFinderGame?
    suspend fun getInProgressGame(): IFSCFinderGame?
    fun observeInProgressGame(): Flow<IFSCFinderGame?>
    suspend fun completeGame(game: IFSCFinderGame): IFSCFinderGame
    suspend fun abandonGame(gameId: Long)
    suspend fun getLevel(seed: Long, levelNumber: Int, difficulty: Difficulty): IFSCFinderLevel
}

interface ChallengeRepository {
    suspend fun getChallenge(type: ChallengeType, key: String): ChallengeRecord?
    suspend fun createChallenge(type: ChallengeType, key: String, difficulty: Difficulty): ChallengeRecord
    suspend fun resolveActiveChallenge(type: ChallengeType): ChallengeRecord
    fun observeActiveChallenge(type: ChallengeType): Flow<ChallengeRecord?>
    suspend fun completeChallenge(record: ChallengeRecord, timeSeconds: Long, moves: Int): ChallengeRecord
    fun observeChallengeHistory(type: ChallengeType): Flow<List<ChallengeRecord>>
    suspend fun getCurrentStreak(type: ChallengeType): Int
    suspend fun getChallengeGame(record: ChallengeRecord): IFSCFinderGame
}

interface ProgressionRepository {
    fun observeStats(): Flow<UserStats>
    suspend fun getStats(): UserStats
    suspend fun updateStatsAfterGame(game: IFSCFinderGame)
    suspend fun grantChallengeRewards(rewardCoins: Int, rewardXp: Int)
    fun observePuzzleProfile(): Flow<PuzzleProfile>
    suspend fun getPuzzleProfile(): PuzzleProfile
    fun observeAchievements(): Flow<List<Achievement>>
    suspend fun checkAndUnlockAchievements(
        game: IFSCFinderGame,
        sameDevicePlayed: Boolean = false
    ): List<Achievement>
    fun observeEconomy(): Flow<EconomyState>
    suspend fun getEconomy(): EconomyState
    suspend fun spendCoins(amount: Int): Boolean
    suspend fun earnCoins(amount: Int)
    suspend fun unlockTheme(themeId: String): Boolean
}

interface PreferencesRepository {
    fun getUserPreferences(): Flow<com.ifscfinder.domain.model.UserPreferences>
    suspend fun updatePreferences(transform: (com.ifscfinder.domain.model.UserPreferences) -> com.ifscfinder.domain.model.UserPreferences)
    suspend fun getCampaignLevel(difficulty: Difficulty): Int
    suspend fun advanceCampaignLevel(difficulty: Difficulty): Int
}
