# ChessJava
ChessJava is helpful for small Java projects. You can use it for piece validation, validate position, create your own chess game with smaller and faster footprint. `ChessJava` is a small and best performance Java class. 

## What you can do with ChessJava

Below is the list of all of feature, which you can implement in your chess game.

- Validate chess pieces.
- Load game from `FEN` string.
- Store piece position in `FEN` formate.
- All type game draw supports
    
    - __Insufficiant Material__

        If a player has no sufficiant material to win the game and king is not in `check`. It results in `Draw game`.

    - __Stalemate__

        If a player has no legal move to run. And king is not in check. It results in `Draw game`.

    - __Fifty Move__

        If either of player complete 50 move without capturing any piece and without running 'Pawn'. It results in `Draw game`.

    - __Three fold repetition__

        If same piece position comes three times in the game. It results in `Draw game`.

    - __Timer__    

        It is a type of draw, In which if the timer ends. It results in `Draw game`. You can implement it yourself.

- Checkmate.        

- Random moves.

## How to use it.

I written it simply in a folder. There is no predefiend structure of this class. You can use as you need. But you just need to change the `package name`. You should not put other class with `ChessJava`.

    ChessJava
        |
        | -- Move.java
        | -- Constants.java (Some constants, you must not edit file.)
        | -- History (For internal working only)
        | -- Loader.java (For internal working only)
        | -- Pattern.java (For internal working only)
        | -- Piece.java 
        | -- Variables.java (For internal working only) 
        | -- Chess.java (Main class for use.)

At the top of each file you will see a package name. `package ChessJava;`. If you are storing this class folder in deep folder. You need to change the `package ChessJava;` to package `your-folder-path`.ChessJava; 


## Initilizing the Main class.

    // create a instance.
    Chess chess = new Chess();

    // load the game
    chess.load();

    // or you can do it as
    // chess.load(your_fen_string);


## Methods of the ChessJava

Below is the list of all methods, you can use them in your program.

- public String print()

    You can print piece position with board. It will return a string, you can then print that string using `System.out.println()`.

        // call the method
        String position = chess.print(); 

        // printing the position.
        System.out.println(position);

        // smaller letter show black piece and capital shows white piece.
        // 8 | r  n  b  q  k  b  n  r
        // 7 | p  p  p  p  p  p  p  p
        // 6 | -  -  -  -  -  -  -  -
        // 5 | -  -  -  -  -  -  -  -
        // 4 | -  -  -  -  -  -  -  -
        // 3 | -  -  -  -  -  -  -  -
        // 2 | P  P  P  P  P  P  P  P
        // 1 | R  N  B  Q  K  B  N  R
        // - - -  -  -  -  -  -  -  -
        //   | a  b  c  d  e  f  g  h

- public String load() | public String load(String fen)

    When you call `.load()` method, default position of pieces are used. You can pass your own `fen` string to load game. If your `fen` is invalid it will return error message, otherwise empty string.

        // without this game, you can start your game.
        // so call it to load default piece position.
        // default position: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0
        chess.load();

        // or you can pass fen string
        // note: whenever you will call this method, it will reset previous loaded game (if it was called previously.)
        chess.load("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0");

- public List<Move> getMoves(int index) | public List<Move> getMoves(String square)

    Get the all possible moves for perticular piece.



> I am still writing the readme file. wait untill i completed this readme file.