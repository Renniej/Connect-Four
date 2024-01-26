package connectfour

const val GAME_NAME = "Connect Four"
const val VALID_INPUT_REGEX = ".?. ?x ?.?."

const val DEFAULT_X = 6
const val DEFAULT_Y = 7

const val MIN_RANGE = 5
const val MAX_RANGE = 9



const val FREE_SPACE = 0;
const val PLAYER_1_TAKEN = -1;
const val PLAYER_2_TAKEN = 1;

enum class GameState {
    ONGOING,
    WON,
    FORFEITTED,
    DRAW
}

class Player(val name : String, val piece : Int) {}


//TODO: Fix check for win conditions and see if there are bugs where it wont check the next when condition when it should a.k.a win == false on the first condition
class Connect4(val rows : Int, val columns : Int, val player1 : Player, val player2 : Player) {

    private var gameBoard = createGameBoard(rows, columns)
    private var currentPlayer = player1
    private var gameState : GameState = GameState.ONGOING
    private var winner : Player = Player("empty",-1)

    private fun createGameBoard(rows : Int, columns : Int) : MutableList<MutableList<Int>> {  // outputs the gameboard to console and returns a multi-dimsensional array representing a new game board

        val gameBoard : MutableList<MutableList<Int>> = mutableListOf()

        for (i in 0..rows-1) {
            gameBoard.add(MutableList<Int>(columns){FREE_SPACE}) //adds a row to the gameboard and makes ensures the default in that index represents a FREE_SPACE on the board
        }

        return gameBoard
    }
    private fun createBottomRow(columns: Int) : String {
        var row = ""

        for (i in 1 .. columns) { //Create normal rows
            when (i) {
                1 -> row += "╚═╩"
                columns -> row += "═╝"
                else -> row += "═╩"
            }
        }

        return row
    }
    private fun createColumnIndices(columns: Int) : String {
        var indices : String = ""

        for (i in 1..columns) indices += " $i" // Create labels for each column
        return indices
    }
    private fun createStrRow(row : MutableList<Int>) : String {

        var strRow : String = "║"

        for (i in 0 .. row.lastIndex) { //Create normal rows
            val cell = "║"
            val r = row[i]
            val spot = if (r == FREE_SPACE){
                " "
            }
            else if (r == PLAYER_1_TAKEN) {
                "o"
            }
            else {
                "*"
            }
            strRow += spot+cell
        }


        return strRow

    }

    private fun isWin(rowIndex : Int, columnIndex: Int) : Boolean {
        return (checkForDiagonalWin(rowIndex,columnIndex) || checkForVerticalWin(rowIndex,columnIndex) || checkForHorizontalWin(rowIndex,columnIndex) )
    }
    private fun isDraw() :  Boolean{
        return (gameBoard[0].filter{ it == FREE_SPACE} ).isEmpty() //check top row for empty spaces. if there is none assume the board is full
    }

    private fun checkForHorizontalWin(rowIndex : Int, columnIndex: Int) : Boolean{

        var win = false;
        val playerPiece = gameBoard[rowIndex][columnIndex]

        when {
            columnIndex + 3 <= gameBoard[0].lastIndex -> win = (gameBoard[rowIndex][columnIndex+1] == playerPiece && gameBoard[rowIndex][columnIndex+2] == playerPiece && gameBoard[rowIndex][columnIndex+3] == playerPiece)
            columnIndex - 3 >= 0 -> win = (gameBoard[rowIndex][columnIndex-1] == playerPiece && gameBoard[rowIndex][columnIndex-2] == playerPiece && gameBoard[rowIndex][columnIndex-3] == playerPiece)
        }

        return win

    }
    private fun checkForVerticalWin(rowIndex : Int, columnIndex: Int)  : Boolean{
        var win = false;
        val playerPiece = gameBoard[rowIndex][columnIndex]

        when {
            rowIndex + 3 <= gameBoard.lastIndex -> win = (gameBoard[rowIndex+1][columnIndex] == playerPiece && gameBoard[rowIndex+2][columnIndex] == playerPiece && gameBoard[rowIndex+3][columnIndex] == playerPiece)
            rowIndex - 3 >= 0 -> win = (gameBoard[rowIndex-1][columnIndex] == playerPiece && gameBoard[rowIndex-2][columnIndex] == playerPiece && gameBoard[rowIndex-3][columnIndex] == playerPiece)
        }

        return win
    }
    private fun checkForDiagonalWin(rowIndex : Int, columnIndex: Int) : Boolean {
        var win = false;
        val playerPiece = gameBoard[rowIndex][columnIndex]

        when {
            rowIndex + 3 <= gameBoard.lastIndex && columnIndex + 3 <= gameBoard[0].lastIndex -> win = (gameBoard[rowIndex+1][columnIndex+1] == playerPiece && gameBoard[rowIndex+2][columnIndex+2] == playerPiece && gameBoard[rowIndex+3][columnIndex+3] == playerPiece)
            rowIndex - 3 >= 0 &&  columnIndex - 3 >= 0 -> win = (gameBoard[rowIndex-1][columnIndex-1] == playerPiece && gameBoard[rowIndex-2][columnIndex-2] == playerPiece && gameBoard[rowIndex-3][columnIndex-3] == playerPiece)

            rowIndex + 3 <= gameBoard.lastIndex && columnIndex - 3 >= 0 -> win = (gameBoard[rowIndex+1][columnIndex-1] == playerPiece && gameBoard[rowIndex+2][columnIndex-2] == playerPiece && gameBoard[rowIndex+3][columnIndex-3] == playerPiece)
            rowIndex - 3 >= 0 &&  columnIndex + 3 <= gameBoard[0].lastIndex -> win = (gameBoard[rowIndex-1][columnIndex+1] == playerPiece && gameBoard[rowIndex-2][columnIndex+2] == playerPiece && gameBoard[rowIndex-3][columnIndex+3] == playerPiece)

        }

        return win
    }
    private fun switchCurrentPlayer() {
        currentPlayer = if (currentPlayer == player1) player2 else player1
    }

    fun makeMove(columnIndex: Int) : Boolean { //returns if the turn was successful

        var moveSuccessful = false
        val columnFull = gameBoard[0][columnIndex] != FREE_SPACE //Check if top row of the board is full because if it is then the entire column is full


        if (!columnFull) {

            for (i in gameBoard.lastIndex downTo 0){

                if (gameBoard[i][columnIndex] == FREE_SPACE) { //if the first row in that column isn't filled then it means the column was empty and the first space should be filled. // If user inputs "1" ensure column index is 0   columnIndex = input - 1

                    gameBoard[i][columnIndex] = currentPlayer.piece

                    gameState = when {
                        (isWin(i,columnIndex)) -> {winner = currentPlayer; GameState.WON;}
                        isDraw() -> GameState.DRAW
                        else -> GameState.ONGOING
                    }

                    switchCurrentPlayer()
                    moveSuccessful = true

                    break
                }
            }
        }

        return moveSuccessful
    }

    fun renderGameBoard() {
        val strBottomRow = createBottomRow(columns)
        val indices = createColumnIndices(columns)

        var output : String = "$indices\n"

        for (i in 0..rows) {
            when (i){
                rows -> output += strBottomRow  // adds bottom closing row of characters to game board display ( "╚═╩═╩═╩═╩═╩═╩═╩═╝" )
                else -> {
                    output += createStrRow(gameBoard[i]) + "\n"
                }
            }
        }

        println(output)
    }


    fun forfeit(){
        gameState = GameState.FORFEITTED
        winner = if (currentPlayer == player1) player2 else player1
    }







    fun getWinner() : Player {
        if (gameState == GameState.DRAW || gameState == GameState.ONGOING)  throw Exception("Cannot get winner if the game was a draw or ongoing")

        return winner
    }
    fun getGameState() : GameState  = gameState
    fun getCurrentPlayer() = currentPlayer
    fun isOngoing() : Boolean  = gameState == GameState.ONGOING

}


fun trimInput(str : String) : String { //removes spaces, non-alphaetic and non-numeric characters out of string
    return str.replace("[^a-zA-Z0-9]".toRegex(), "")
}
fun createPlayers(player1Name : String, player2Name : String) : List<Player> {
    return listOf(Player(player1Name, PLAYER_1_TAKEN), Player(player2Name, PLAYER_2_TAKEN))
}


class PlayerData(val player : Player, var score : Int = 0) {
    fun getName() : String {
        return player.name
    }
}

class GameManager() {

    private val playerData : List<PlayerData>
    private val rows : Int
    private val columns : Int
    private val totalRounds :Int


    init {
        println(GAME_NAME);

        playerData  = createPlayers(inputPlayerName("First"), inputPlayerName("Second")).map {PlayerData(it)}
        val boardDims = inputBoardDimensions()
        rows = boardDims[0]
        columns = boardDims[1]
        totalRounds = inputRounds()
    }

    fun start() {

        var firstMove : Player = Player("Empty", FREE_SPACE)

        println("${playerData[0].getName()} VS ${playerData[1].getName()}\n$rows X $columns board")
        println(if (totalRounds == 1) "Single game" else "Total $totalRounds games")

        gameLoop@ for (i in 1..totalRounds){

           if (totalRounds > 1) println("Game #$i")

            firstMove  = if (firstMove == playerData[0].player) playerData[1].player else playerData[0].player


            var curGame = startRound(firstMove, if (firstMove == playerData[0].player) playerData[1].player else playerData[0].player)
            val state = curGame.getGameState()


            when(state) {
                GameState.FORFEITTED -> break@gameLoop
                GameState.DRAW -> playerData.forEach{it.score++}
                GameState.ONGOING ->{}
                GameState.WON -> {
                    playerSearch@ for (data in playerData)
                        if (data.player == curGame.getWinner()) {
                            data.score += 2
                            break@playerSearch
                        }
                }

            }





            println("Score\n" +
                    "${playerData[0].getName()}: ${playerData[0].score} ${playerData[1].getName()}: ${playerData[1].score}")
        }


    }

    fun inputPlayerName(str : String) : String {
        println("$str player's name:")
        return readln()
    }
    fun inputBoardDimensions() : List<Int> {


        val validFormat = Regex(VALID_INPUT_REGEX, RegexOption.IGNORE_CASE)
        val inputRange = MIN_RANGE..MAX_RANGE
        var x : Int
        var y : Int

        do {
            x  = DEFAULT_X;
            y  = DEFAULT_Y;
            var isValidFormat : Boolean= false;
            var isValidRange : Boolean = false;

            println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
            val input : String = trimInput(readln())

            if (input.isEmpty()) break; // use Default X and Y values

            try {
                if (validFormat.matches(input)) {
                    val (strX, strY) = input.split("x".toRegex(RegexOption.IGNORE_CASE))
                    x = strX.toInt()
                    y = strY.toInt()
                    isValidFormat = true
                }

            } catch (e : Exception) {

            }

            if (!isValidFormat) {
                println("Invalid input")
            } else if (!(x in inputRange)) {
                println("Board rows should be from $MIN_RANGE to $MAX_RANGE")
            } else if (!(y in inputRange)) {
                println("Board columns should be from $MIN_RANGE to $MAX_RANGE")
            } else {
                isValidRange = true;
            }

        } while(!(isValidRange && isValidFormat))

        return listOf(x,y)

    }
    fun inputMove(game : Connect4) : Boolean { //Returns true if the player inputs end the game

        var moveSuccessful = false

        do {
            println("${game.getCurrentPlayer().name}'s turn")
            val input = trimInput(readln())

            if (input == "end") game.forfeit() //game stops if forfeitted


            if (game.isOngoing()) {
                val chosenColumn = input.toIntOrNull()
                when {
                    chosenColumn == null -> println("Incorrect column number")
                    !(chosenColumn in (1..game.columns)) ->  println("The column number is out of range (1 - ${game.columns})")
                    else -> moveSuccessful = game.makeMove(chosenColumn-1)
                }
            }

        } while (!moveSuccessful && game.isOngoing())


        return moveSuccessful
    }



    private fun inputRounds() : Int {



        while (true) {
            println("Do you want to play single or multiple games?\n" +
                    "For a single game, input 1 or press Enter\n" +
                    "Input a number of games:")

                try {

                    val input = readln()

                    when {
                        input.isEmpty() -> return 1
                        input.toInt() > 0 -> return input.toInt()
                    }


                } catch (_: Exception) { }
            println("Invalid input")

        }



    }

    private fun startRound(firstPlayer : Player, secondPlayer : Player) : Connect4 { // returns winner

        var game = Connect4(rows, columns,   firstPlayer, secondPlayer);
        var endMsg = ""



        while (game.isOngoing()) {

            game.renderGameBoard()
            inputMove(game)

            when(game.getGameState()) {
                GameState.FORFEITTED -> break
                GameState.WON -> endMsg = "Player ${game.getWinner().name} won"
                GameState.DRAW -> endMsg = "It is a draw"
                GameState.ONGOING -> {}
            }

        }

        if (game.getGameState() != GameState.FORFEITTED){
            game.renderGameBoard()
            println(endMsg)
        }



        return game
    }

}


fun main() {
    val gameManager = GameManager()
    gameManager.start()
    println("Game over!")
}

