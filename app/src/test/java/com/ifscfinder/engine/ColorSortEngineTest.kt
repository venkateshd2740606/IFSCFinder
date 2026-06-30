package com.ifscfinder.engine

import com.ifscfinder.domain.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IFSCFinderEngineTest {

    @Test
    fun tutorialLevel_isValidAndSolvable() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        assertTrue(IFSCFinderEngine.validateLevel(level))
    }

    @Test
    fun pour_updatesTubeState() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = IFSCFinderEngine.createInitialGame(level)
        assertTrue(IFSCFinderEngine.canPour(game, 0, 2))
        game = IFSCFinderEngine.pour(game, 0, 2)
        assertEquals(1, game.moves)
        assertTrue(game.tubes[2].isNotEmpty())
    }

    @Test
    fun solveTutorial_completesGame() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = IFSCFinderEngine.createInitialGame(level)
        val solution = IFSCFinderEngine.solve(game)!!
        solution.forEach { (from, to) ->
            game = IFSCFinderEngine.pour(game, from, to)
        }
        assertTrue(IFSCFinderEngine.isWon(game))
    }

    @Test
    fun generatedLevel_isValid() {
        val level = IFSCFinderGenerator.generate(12345L, 1, Difficulty.EASY)
        assertTrue(IFSCFinderEngine.validateLevel(level))
    }

    @Test
    fun tubeSelection_poursWhenSecondTubeSelected() {
        val level = TutorialLevels.getTutorialLevel(0)!!
        var game = IFSCFinderEngine.createInitialGame(level)
        game = IFSCFinderEngine.onTubeSelected(game, 0)
        assertEquals(0, game.selectedTubeId)
        game = IFSCFinderEngine.onTubeSelected(game, 2)
        assertEquals(1, game.moves)
    }

    @Test
    fun generator_sameSeed_producesSameLevel() {
        val a = IFSCFinderGenerator.generate(999L, 5, Difficulty.MEDIUM)
        val b = IFSCFinderGenerator.generate(999L, 5, Difficulty.MEDIUM)
        assertEquals(a.initialTubes, b.initialTubes)
    }
}
