package com.ifscfinder.data

import com.ifscfinder.domain.model.Difficulty
import com.ifscfinder.domain.model.GameStatus
import com.ifscfinder.engine.IFSCFinderEngine
import com.ifscfinder.engine.IFSCFinderGenerator
import com.ifscfinder.util.ProgressionCalculator
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionCalculatorTest {

    @Test
    fun xpForCompletedGame_isPositive() {
        val level = IFSCFinderGenerator.generate(1L, 1, Difficulty.EASY)
        val game = IFSCFinderEngine.createInitialGame(level).copy(status = GameStatus.COMPLETED)
        assertTrue(ProgressionCalculator.xpForGame(game) > 0)
    }

    @Test
    fun xpForGame_withHints_isLowerThanWithoutHints() {
        val level = IFSCFinderGenerator.generate(1L, 1, Difficulty.EASY)
        val withHints = IFSCFinderEngine.createInitialGame(level).copy(hintsUsed = 2, status = GameStatus.COMPLETED)
        val noHints = IFSCFinderEngine.createInitialGame(level).copy(hintsUsed = 0, status = GameStatus.COMPLETED)
        assertTrue(ProgressionCalculator.xpForGame(noHints) >= ProgressionCalculator.xpForGame(withHints))
    }
}
