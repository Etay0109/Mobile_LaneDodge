package com.example.lanedodge1.logic

import com.example.lanedodge1.utilities.CellType

class GameManager(private val lifeCount: Int = 3) {
    companion object {
        const val ROWS = 8
        const val COLS = 3
    }
    private var ticks = 0
    private val obstaclesMatrix = Array(ROWS) { Array(COLS) { CellType.EMPTY } }
    private var lastRowState = Array(COLS) { CellType.EMPTY }

    var carPosition: Int = 1
        private set

    var collisions: Int = 0
        private set

    val isGameOver: Boolean
        get() = collisions == lifeCount

    fun onCollision() {
        if (!isGameOver)    collisions++
    }

    fun moveCarLeft() {
        if (carPosition > 0) carPosition--
    }

    fun moveCarRight() {
        if (carPosition < COLS - 1) carPosition++
    }

    fun getObstaclesMatrix(): Array<Array<CellType>> = obstaclesMatrix

    fun initObstacles() {   //Random 2 stones in the matrix
        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                obstaclesMatrix[row][col] = CellType.EMPTY
            }
        }
        val row1 = (0 until ROWS - 2).random()
        val col1 = (0 until COLS).random()
        obstaclesMatrix[row1][col1] = CellType.OBSTACLE
        var row2: Int
        var col2: Int
        do {
            row2 = (0 until ROWS - 2).random()
            col2 = (0 until COLS).random()
        } while (row2 == row1 && col2 == col1)

        obstaclesMatrix[row2][col2] = CellType.OBSTACLE
    }

    fun checkCollision() {
        val last_row = ROWS - 1

        for (col in 0 until COLS) {

            if (obstaclesMatrix[last_row][col] == CellType.OBSTACLE &&
                lastRowState[col] == CellType.OBSTACLE) {

                if (col == carPosition) {
                    onCollision()
                }

                obstaclesMatrix[last_row][col] = CellType.EMPTY
            }
        }

        for (col in 0 until COLS) {
            lastRowState[col] = obstaclesMatrix[last_row][col]
        }
    }



    fun addNewObstacle() {  //Add new obstacle at the first row
        ticks++
        if (ticks % 3 == 0) {
            val col = (0 until COLS).random()
            if (obstaclesMatrix[0][col] == CellType.EMPTY) {
                obstaclesMatrix[0][col] = CellType.OBSTACLE
            }
        }
    }

    fun moveObstaclesDown() {   //Move obstacle down on the matrix

        for (row in ROWS - 2 downTo 0) {
            for (col in 0 until COLS) {
                if (obstaclesMatrix[row][col] == CellType.OBSTACLE) {
                    if (obstaclesMatrix[row + 1][col] == CellType.EMPTY) {
                        obstaclesMatrix[row + 1][col] = CellType.OBSTACLE
                        obstaclesMatrix[row][col] = CellType.EMPTY
                    }
                }
            }
        }
    }

    fun tick() {
        moveObstaclesDown()
        checkCollision()
        addNewObstacle()
    }
}