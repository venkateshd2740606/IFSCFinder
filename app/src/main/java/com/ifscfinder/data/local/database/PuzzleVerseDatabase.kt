package com.ifscfinder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ifscfinder.data.local.database.dao.AchievementDao
import com.ifscfinder.data.local.database.dao.ChallengeDao
import com.ifscfinder.data.local.database.dao.EconomyDao
import com.ifscfinder.data.local.database.dao.GameDao
import com.ifscfinder.data.local.database.dao.IfscFavoriteDao
import com.ifscfinder.data.local.database.dao.ProfileDao
import com.ifscfinder.data.local.database.dao.StatsDao
import com.ifscfinder.data.local.database.entity.IfscFavoriteEntity
import com.ifscfinder.data.local.database.entity.ProfileEntity
import com.ifscfinder.data.local.database.entity.AchievementEntity
import com.ifscfinder.data.local.database.entity.ChallengeEntity
import com.ifscfinder.data.local.database.entity.EconomyEntity
import com.ifscfinder.data.local.database.entity.GameEntity
import com.ifscfinder.data.local.database.entity.StatsEntity

@Database(
    entities = [
        GameEntity::class,
        StatsEntity::class,
        AchievementEntity::class,
        ChallengeEntity::class,
        EconomyEntity::class,
        ProfileEntity::class,
        IfscFavoriteEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class IFSCFinderDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun statsDao(): StatsDao
    abstract fun achievementDao(): AchievementDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun economyDao(): EconomyDao
    abstract fun profileDao(): ProfileDao
    abstract fun ifscFavoriteDao(): IfscFavoriteDao
}
