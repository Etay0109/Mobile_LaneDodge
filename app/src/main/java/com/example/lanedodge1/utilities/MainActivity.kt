package com.example.lanedodge1.utilities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.lanedodge1.R
import com.example.lanedodge1.logic.GameManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    private lateinit var main_IMG_cells: Array<Array<AppCompatImageView>>

    private val cellsId = arrayOf(
        intArrayOf(R.id.cell_0_0, R.id.cell_0_1, R.id.cell_0_2),
        intArrayOf(R.id.cell_1_0, R.id.cell_1_1, R.id.cell_1_2),
        intArrayOf(R.id.cell_2_0, R.id.cell_2_1, R.id.cell_2_2),
        intArrayOf(R.id.cell_3_0, R.id.cell_3_1, R.id.cell_3_2),
        intArrayOf(R.id.cell_4_0, R.id.cell_4_1, R.id.cell_4_2),
        intArrayOf(R.id.cell_5_0, R.id.cell_5_1, R.id.cell_5_2),
        intArrayOf(R.id.cell_6_0, R.id.cell_6_1, R.id.cell_6_2)
    )

    private lateinit var bottom_car_row: Array<AppCompatImageView>
    private lateinit var bottom_obstacle_row: Array<AppCompatImageView>
    private lateinit var main_BTN_left: ImageButton
    private lateinit var main_BTN_right: ImageButton
    private lateinit var timerJob: Job
    private lateinit var gameManager: GameManager
    private var lastCollisions = 0
    private lateinit var main_LBL_game_over: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        initViews()

    }

    private fun findViews() {
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_IMG_cells = Array(7) { row ->
            Array(3) { col ->
                findViewById(cellsId[row][col])
            }
        }
        bottom_car_row = arrayOf(
            findViewById(R.id.cell_7_0_car),
            findViewById(R.id.cell_7_1_car),
            findViewById(R.id.cell_7_2_car)
        )

        bottom_obstacle_row = arrayOf(
            findViewById(R.id.cell_7_0_obstacle),
            findViewById(R.id.cell_7_1_obstacle),
            findViewById(R.id.cell_7_2_obstacle)
        )

        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)

        main_LBL_game_over = findViewById(R.id.main_LBL_game_over)


    }
    private fun initViews() {
        gameManager.initObstacles()

        main_BTN_left.setOnClickListener {
            gameManager.moveCarLeft()
            refreshUI()
        }

        main_BTN_right.setOnClickListener {
            gameManager.moveCarRight()
            refreshUI()
        }

        refreshUI()
        startTimer()
    }

    private fun refreshUI() {
        // vibrate and toast if collision
        if (gameManager.collisions > lastCollisions) {
            SignalManager.getInstance().vibrate()
            SignalManager.getInstance().toast("Crash!")
        }
        lastCollisions = gameManager.collisions

        if (gameManager.collisions != 0) { //Update the hearts in the UI
            val heart_to_remove = gameManager.collisions - 1
            if (heart_to_remove in main_IMG_hearts.indices) {
                main_IMG_hearts[heart_to_remove].visibility = View.INVISIBLE
            }
        }
        //Lost
        if (gameManager.isGameOver) {
            stopTimer()
            main_LBL_game_over.visibility = View.VISIBLE
            return
        }
        //Ongoing
        val matrix = gameManager.getObstaclesMatrix()

        for (row in matrix.indices) {
            for (col in matrix[row].indices) {

                if (row < matrix.lastIndex) {  //Update the obstacles in the UI except the last row
                    if (matrix[row][col] == CellType.OBSTACLE) {
                        main_IMG_cells[row][col].setImageResource(R.drawable.obstacle)
                        main_IMG_cells[row][col].visibility = View.VISIBLE
                    } else {
                        main_IMG_cells[row][col].visibility = View.INVISIBLE
                    }
                }

                else {  //Update the car and obstacles in the UI in the last row
                    bottom_obstacle_row[col].visibility =
                        if (matrix[row][col] == CellType.OBSTACLE) View.VISIBLE else View.INVISIBLE

                    bottom_car_row[col].apply {
                        visibility = if (col == gameManager.carPosition) View.VISIBLE else View.INVISIBLE
                        rotation = -90f
                    }
                }
            }
        }
    }

    private fun startTimer() {
        if (::timerJob.isInitialized && timerJob.isActive) {
            return
        }
        timerJob = lifecycleScope.launch {
            while (true) {
                gameManager.tick()
                refreshUI()

                if (gameManager.isGameOver) {
                    stopTimer()
                    break
                }

                delay(600)
            }
        }
    }

    private fun stopTimer() {
        if (::timerJob.isInitialized && timerJob.isActive) {
            timerJob.cancel()
        }
    }


    override fun onPause() {    // Pause the app when we are not using it
        super.onPause()
        stopTimer()
    }

    override fun onResume() {   //resume the app when we are back
        super.onResume()
        if (!gameManager.isGameOver) {
            startTimer()
        }
    }

}


