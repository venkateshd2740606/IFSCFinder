package com.ifscfinder.multiplayer

import com.ifscfinder.domain.model.IFSCFinderGame
import com.ifscfinder.domain.model.Difficulty
import com.ifscfinder.domain.model.MultiplayerMode
import com.ifscfinder.domain.model.MultiplayerSession
import com.ifscfinder.engine.IFSCFinderEngine
import com.ifscfinder.engine.IFSCFinderGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuzzleBotSession @Inject constructor() {
    private val _session = MutableStateFlow<MultiplayerSession?>(null)
    val session: StateFlow<MultiplayerSession?> = _session.asStateFlow()

    private var playerGame: IFSCFinderGame? = null
    private var botGame: IFSCFinderGame? = null
    private var playerName = "You"
    private val botName = "AI Bot"

    fun start(player: String, difficulty: Difficulty, seed: Long = System.currentTimeMillis()) {
        playerName = player
        val level = IFSCFinderGenerator.generate(seed, 1, difficulty)
        val game = IFSCFinderEngine.createInitialGame(level)
        playerGame = game
        botGame = game
        _session.value = MultiplayerSession(
            mode = MultiplayerMode.SAME_DEVICE,
            localPlayerName = playerName,
            remotePlayerName = botName,
            activePlayerName = playerName,
            isActive = true,
            seed = seed,
            difficulty = difficulty
        )
    }

    fun getPlayerGame(): IFSCFinderGame? = playerGame

    fun applyPlayerTubeClick(tubeId: Int): IFSCFinderGame? {
        val game = playerGame ?: return null
        val updated = IFSCFinderEngine.onTubeSelected(game, tubeId)
        playerGame = updated
        botGame = updated
        return updated
    }

    fun applyBotMove(): IFSCFinderGame? {
        val game = botGame ?: return null
        val hint = IFSCFinderEngine.getHintMove(game) ?: return game
        var updated = IFSCFinderEngine.onTubeSelected(game, hint.first)
        if (updated.selectedTubeId != null) {
            updated = IFSCFinderEngine.onTubeSelected(updated, hint.second)
        }
        playerGame = updated
        botGame = updated
        val session = _session.value
        if (session != null) {
            _session.value = session.copy(
                remoteScore = session.remoteScore + if (updated.isCompleted) 1 else 0,
                activePlayerName = playerName
            )
        }
        return updated
    }

    fun onPlayerWon() {
        val session = _session.value ?: return
        _session.value = session.copy(
            localScore = session.localScore + 1,
            activePlayerName = playerName
        )
        startNewRound(session)
    }

    fun onBotWon() {
        val session = _session.value ?: return
        _session.value = session.copy(
            remoteScore = session.remoteScore + 1,
            activePlayerName = playerName
        )
        startNewRound(session)
    }

    private fun startNewRound(session: MultiplayerSession) {
        val newSeed = session.seed + session.localScore + session.remoteScore
        val level = IFSCFinderGenerator.generate(newSeed, session.localScore + session.remoteScore + 1, session.difficulty)
        val game = IFSCFinderEngine.createInitialGame(level)
        playerGame = game
        botGame = game
    }

    fun isBotThinking(): Boolean = false

    fun end() {
        _session.value = null
        playerGame = null
        botGame = null
    }
}
