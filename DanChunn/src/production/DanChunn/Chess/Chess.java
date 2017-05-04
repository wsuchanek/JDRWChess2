package production.DanChunn.Chess;


import model.TextToSpeech;
import production.DanChunn.Chess.Check;
import production.DanChunn.Game.AI;
import production.DanChunn.Game.Board;
import production.DanChunn.Game.Player;
import production.DanChunn.Pieces.Bishop;
import production.DanChunn.Pieces.King;
import production.DanChunn.Pieces.Knight;
import production.DanChunn.Pieces.Pawn;
import production.DanChunn.Pieces.Piece;
import production.DanChunn.Pieces.Queen;
import production.DanChunn.Pieces.Rook;
///import java.io.BufferedReader;
///import java.io.IOException;
///import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import production.DanChunn.util.Color;
import production.DanChunn.util.LastMoveQueue;
import java.io.*;

public class Chess {
    Board board;
    Player[] players = new Player[2];
    List<Piece> recentlyRem;
    boolean draw;
    LastMoveQueue lastMoves = new LastMoveQueue();
    ArrayList<int[]> LoadedMoves = new ArrayList<>();
    AI myAi;
    TextToSpeech tts = new TextToSpeech();
    SpeechRecognition myRec = new SpeechRecognition();


    public Chess() {
        tts.setVoice("dfki-prudence-hsmm");

        this.players[0] = new Player(Color.White);
        this.players[1] = new Player(Color.Black);
        this.board = new Board(this.players);
        this.board.addPieces(this.players[0]);
        this.board.addPieces(this.players[1]);
        this.recentlyRem = new ArrayList();
        this.draw = false;
        myAi = new AI();
    }

    public void printBoard() {
        System.out.println();
        this.board.printBoard();
        System.out.println();
    }

    public void start(int task) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int[] start = new int[2];
        int[] end = new int[2];
        String line = "";
        int i = 0;
        boolean check = false;
        boolean success = false;




        int[] loadstart = new int[2];  //for loaded moves
        int[] loadend = new int[2];    //for loaded moves


        if(task ==1){
            this.loadMoves();
            if (LoadedMoves.size() > 0) {

                for (int[] moves : LoadedMoves) {
                    loadstart[0] = moves[0];
                    //System.out.println(moves[0]);
                    loadstart[1] = moves[1];
                    //System.out.println(moves[1]);
                    loadend[0] = moves[2];
                    //System.out.println(moves[2]);
                    loadend[1] = moves[3];
                    //System.out.println(moves[3]);
                    this.attemptMove(loadstart, loadend, this.players[i]);
                    lastMoves.addMoves(moves);
                    if (i == 0) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                }
            }else{
                System.out.println("Sorry no game to load found");
                tts.speak("Sorry no game to load found", 2.0f, false, true);
            }
        }
        i=0;
        this.printBoard();
        sayCommands();

        while(true) {
            while(true) {
                while(true) {
                    if(i == 0) {
                        System.out.print("White's move (Format - a2 a3): ");
                        tts.speak("White's Move: ", 2.0f, false, true);
                        try {
                            tts.speak("starting position or command: ", 2.0f, false, true);
                            String linePart1 = myRec.getResponse().toLowerCase();

                            //Checks to see if the user said the command words
                            if (linePart1.equals("resign")) {
                                System.exit(1);

                            } else if (this.draw) {
                                if (linePart1.equals("draw")) {
                                    System.exit(1);
                                } else {
                                    this.draw = false;
                                    System.out.println("\nError: Incorrect input.\n");
                                    tts.speak("Error: Incorrect Input", 2.0f, false, true);
                                    break;
                                }
                            } else if(linePart1.equals("repeat")) {
                                lastMoves.printLastFiveMoves();
                                break;
                            }else if(linePart1.equals("quit")) {
                                System.out.println("Game Saved, Quitting.");
                                tts.speak("Game Saved, Quitting", 2.0f, false, true);
                                lastMoves.saveGameFile();
                                lastMoves.saveGameFileLoad();
                                System.exit(1);
                            }


                            tts.speak("ending position: ", 2.0f, false, true);
                            String linePart2 = myRec.getResponse().toLowerCase();
                            line = linePart1 + " " + linePart2;

                            System.out.println(line.toLowerCase());
                            break;
                        } catch (Exception var11) {
                            System.out.println("Error: No moved specified");
                            tts.speak("Error: No moved specified", 2.0f, false, true);
                        }

                    } else {
                        line = (String) "a7 a6";
                        break;
                        //System.out.print("Black\'s move:(Format - a7 a6) : ");
                    }

                }
                String[] argv = line.split(" ");
                if (argv.length == 0) {
                    System.out.println("\nError: No moved specified");
                    tts.speak("Error: No moved specified", 2.0f, false, true);
                } else if (argv.length == 1) {
                    if (argv[0].equals("resign")) {
                        System.out.println("\nError: Incorrect input.");
                        tts.speak("Error: Incorrect Input", 2.0f, false, true);
                    }
                } else if (argv.length >= 2 && argv.length <= 3) {
                    try {
                        start = this.translatePos(argv[0]);
                        end = this.translatePos(argv[1]);
                    } catch (IllegalArgumentException var14) {
                        System.out.println("\nError: Moves are in incorrect format. Moves consist of fileRank");
                        tts.speak("Error: Moves are in incorrect format. Moves consist of fileRank", 2.0f, false, true);
                        continue;
                    }

                    int success1;
                    /*
                    System.out.println("UStartY: " + start[0] + " UStartX: " + start[1]);
                    System.out.println("UEndY: " + end[0] + " UEndX: " + end[1]);
                    System.out.println("UPlayer: " + this.players[i].toString());
                    */


                    if(i == 0) {
                        try {

                            success1 = this.attemptMove(start, end, this.players[i]);
                            int[] addList = new int[4];
                            addList[0] = start[0];
                            addList[1] = start[1];
                            addList[2] = end[0];
                            addList[3] = end[1];

                            lastMoves.addMoves(addList);

                        } catch (IllegalArgumentException var13) {
                            System.out.println("\nIllegal Move, try again.\n");
                            tts.speak("Illegal Move,try again" , 2.0f, false, true);
                            continue;
                        }
                    }else{
                        //MAKE ALEXA's Move:
                        try {

                            success1 = myAi.makeRandMove(board.posList,board,this.players[i],this,lastMoves);

                        } catch (IllegalArgumentException var13) {
                            System.out.println("\nIllegal Move, try again.\n");
                            tts.speak("Illegal Move,try again" , 2.0f, false, true);

                            continue;
                        }
                    }

                    this.draw = false;
                    if (argv.length == 3 && argv[2].equals("draw?")) {
                        this.draw = true;
                    }

                    this.players[i].addMove(argv[0] + " " + argv[1]);
                    if (success1 == 31 || success1 == 32 || success1 == 33 || success1 == 34) {
                        try {
                            this.promotePawn(end, argv.length, argv);
                        } catch (IllegalArgumentException var12) {
                            line = this.players[i].getLastMove();
                            argv = line.split(" ");
                            start = this.translatePos(argv[0]);
                            end = this.translatePos(argv[1]);
                            this.redoLastMove(end, start, this.players[i], success1);
                            System.out.println("\nIllegal Move, try again\n");
                            tts.speak("Illegal Move, try again", 2.0f, false, true);

                            continue;
                        }
                    }

                    King king;
                    if (this.players[i].isInCheck()) {
                        king = (King) this.players[i].getKingRef();
                        if (Check.scanCheck(king.getPosition().getRank(), king.getPosition().getFile(), king.getColor(), king.board)) {
                            line = this.players[i].getLastMove();
                            argv = line.split(" ");
                            start = this.translatePos(argv[0]);
                            end = this.translatePos(argv[1]);
                            this.redoLastMove(end, start, this.players[i], success1);
                            System.out.println("\nIllegal Move, try again\n");
                            tts.speak("Illegal Move, try again", 2.0f, false, true);
                            continue;
                        }

                        this.players[i].notCheck();
                    } else {
                        king = (King) this.players[i].getKingRef();
                        if (Check.scanCheck(king.getPosition().getRank(), king.getPosition().getFile(), king.getColor(), king.board)) {
                            line = this.players[i].getLastMove();
                            argv = line.split(" ");
                            start = this.translatePos(argv[0]);
                            end = this.translatePos(argv[1]);
                            this.redoLastMove(end, start, this.players[i], success1);
                            System.out.println("\nIllegal Move, try again\n");
                            tts.speak("Illegal Move, try again", 2.0f, false, true);
                            continue;
                        }
                    }

                    this.printBoard();
                    this.resetEnPassent(i);
                    i = (i + 1) % 2;
                    king = (King) this.players[i].getKingRef();
                    int check1 = this.isCheckMate(king);
                    if (check1 != 0) {
                        if (check1 == 11) {
                            System.out.println("\nCheck\n");
                            this.players[i].inCheck();
                        } else {
                            System.out.println("\nCheckmate\n");
                            if (i == 0) {
                                System.out.println("Black wins");
                            } else {
                                System.out.println("White wins");
                            }

                            System.exit(1);
                        }
                    }
                    lastMoves.enqueue(line);

                } else {
                    System.out.println("\nError: Incorrect input.");
                    tts.speak("Illegal Move, try again", 2.0f, false, true);
                }
            }

        }
    }

    public int[] translatePos(String move) {
        if(move.charAt(0) >= 97 && move.charAt(0) <= 104) {
            int[] fr = new int[2];
            switch(move.charAt(0)) {
                case 'a':
                    fr[1] = 0;
                    break;
                case 'b':
                    fr[1] = 1;
                    break;
                case 'c':
                    fr[1] = 2;
                    break;
                case 'd':
                    fr[1] = 3;
                    break;
                case 'e':
                    fr[1] = 4;
                    break;
                case 'f':
                    fr[1] = 5;
                    break;
                case 'g':
                    fr[1] = 6;
                    break;
                case 'h':
                    fr[1] = 7;
            }

            try {
                fr[0] = Integer.parseInt(move.substring(1));
                if(fr[0] < 1 || fr[0] > 8) {
                    throw new IllegalArgumentException();
                }

                --fr[0];
            } catch (NumberFormatException var4) {
                throw new IllegalArgumentException();
            }

            //System.out.println("the coordinates are: " + fr[0] + " ," + fr[1]);
            return fr;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int attemptMove(int[] start, int[] end, Player currP) throws IllegalArgumentException {
        Piece toMove = this.board.getSquare(start[0], start[1]).getPiece();
        if(toMove == null) {
            throw new IllegalArgumentException();
        } else if(toMove.getColor() != currP.getColor()) {
            throw new IllegalArgumentException();
        } else {
            int legal = toMove.legalMove(start[0], start[1], end[0], end[1]);
            if(legal != 0 && legal != 3) {
                if(legal == 1) {
                    this.board.getSquare(start[0], start[1]).removePiece();
                    this.board.getSquare(end[0], end[1]).setPiece(toMove);
                    if(end[0] == 7 && currP.getColor() == Color.White) {
                        if(toMove instanceof Pawn) {
                            return 31;
                        }
                    } else if(end[0] == 0 && currP.getColor() == Color.Black && toMove instanceof Pawn) {
                        return 33;
                    }

                    return 1;
                } else if(legal != 2) {
                    if(legal == 11) {
                        this.board.getSquare(start[0], start[1]).removePiece();
                        this.board.getSquare(end[0], end[1]).setPiece(toMove);
                        toMove = this.board.getSquare(end[0], end[1] + 1).removePiece();
                        this.board.getSquare(end[0], end[1] - 1).setPiece(toMove);
                        return 11;
                    } else if(legal == 12) {
                        this.board.getSquare(start[0], start[1]).removePiece();
                        this.board.getSquare(end[0], end[1]).setPiece(toMove);
                        toMove = this.board.getSquare(end[0], end[1] - 2).removePiece();
                        this.board.getSquare(end[0], end[1] + 1).setPiece(toMove);
                        return 12;
                    } else if(legal == 21) {
                        this.recentlyRem.add(this.board.getSquare(end[0] + 1, end[1]).removePiece());
                        this.board.getSquare(start[0], start[1]).removePiece();
                        this.board.getSquare(end[0], end[1]).setPiece(toMove);
                        return 21;
                    } else if(legal == 22) {
                        this.recentlyRem.add(this.board.getSquare(end[0] - 1, end[1]).removePiece());
                        this.board.getSquare(start[0], start[1]).removePiece();
                        this.board.getSquare(end[0], end[1]).setPiece(toMove);
                        return 22;
                    } else {
                        throw new IllegalArgumentException();
                    }
                } else {
                    this.board.getSquare(start[0], start[1]).removePiece();
                    this.recentlyRem.add(this.board.getSquare(end[0], end[1]).removePiece());
                    this.board.getSquare(end[0], end[1]).setPiece(toMove);
                    if(end[0] == 7 && currP.getColor() == Color.White) {
                        if(toMove instanceof Pawn) {
                            return 32;
                        }
                    } else if(end[0] == 0 && currP.getColor() == Color.Black && toMove instanceof Pawn) {
                        return 34;
                    }

                    return 2;
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public void redoLastMove(int[] end, int[] start, Player currP, int wasPieceCaptured) {
        Piece toMove = this.board.getSquare(end[0], end[1]).removePiece();
        if(toMove == null) {
            throw new IllegalArgumentException();
        } else if(toMove.getColor() != currP.getColor()) {
            throw new IllegalArgumentException();
        } else {
            this.board.getSquare(start[0], start[1]).setPiece(toMove);
            Piece recRem;
            if(wasPieceCaptured == 2) {
                recRem = (Piece)this.recentlyRem.remove(this.recentlyRem.size() - 1);
                this.board.getSquare(end[0], end[1]).setPiece(recRem);
            } else {
                Piece p;
                if (wasPieceCaptured == 11) {
                    p = this.board.getSquare(end[0], end[1] - 1).removePiece();
                    this.board.getSquare(end[0], end[1] + 1).setPiece(p);
                } else if (wasPieceCaptured == 12) {
                    p = this.board.getSquare(end[0], end[1] + 1).removePiece();
                    this.board.getSquare(end[0], end[1] - 2).setPiece(p);
                } else if (wasPieceCaptured == 21) {
                    p = (Piece) this.recentlyRem.remove(this.recentlyRem.size() - 1);
                    this.board.getSquare(end[0] + 1, end[1]).setPiece(p);
                } else if (wasPieceCaptured == 22) {
                    p = (Piece) this.recentlyRem.remove(this.recentlyRem.size() - 1);
                    this.board.getSquare(end[0] - 1, end[1]).setPiece(p);
                } else {
                    Pawn p1;
                    if (wasPieceCaptured == 31) {
                        p1 = new Pawn(toMove.getColor(), this.board);
                        ((Pawn) p1).incMoveCount();
                        this.board.getSquare(start[0], start[1]).removePiece();
                        this.board.getSquare(start[0], start[1]).setPiece(p1);
                    } else if (wasPieceCaptured == 32) {
                        p1 = new Pawn(toMove.getColor(), this.board);
                        ((Pawn) p1).incMoveCount();
                        this.board.getSquare(start[0], start[1]).removePiece();
                        this.board.getSquare(start[0], start[1]).setPiece(p1);
                        recRem = (Piece) this.recentlyRem.remove(this.recentlyRem.size() - 1);
                        this.board.getSquare(end[0], end[1]).setPiece(recRem);
                    }
                }
            }
        }
    }

    public void promotePawn(int[] end, int argvlength, String[] argv) throws IllegalArgumentException {
        Piece q;
        Queen p3;
        if(argvlength == 3) {
            if(argv[3].equals("N")) {
                q = this.board.getSquare(end[0], end[1]).removePiece();
                Knight p = new Knight(q.getColor(), this.board);
                this.board.getSquare(end[0], end[1]).setPiece(p);
            } else if(argv[3].equals("R")) {
                q = this.board.getSquare(end[0], end[1]).removePiece();
                Rook p1 = new Rook(q.getColor(), this.board);
                this.board.getSquare(end[0], end[1]).setPiece(p1);
            } else if(argv[3].equals("B")) {
                q = this.board.getSquare(end[0], end[1]).removePiece();
                Bishop p2 = new Bishop(q.getColor(), this.board);
                this.board.getSquare(end[0], end[1]).setPiece(p2);
            } else {
                if(!argv[3].equals("Q")) {
                    throw new IllegalArgumentException();
                }

                q = this.board.getSquare(end[0], end[1]).removePiece();
                p3 = new Queen(q.getColor(), this.board);
                this.board.getSquare(end[0], end[1]).setPiece(p3);
            }
        } else {
            q = this.board.getSquare(end[0], end[1]).removePiece();
            p3 = new Queen(q.getColor(), this.board);
            this.board.getSquare(end[0], end[1]).setPiece(p3);
        }

    }

    public int isCheckMate(Piece p) {
        int Kx = p.getPosition().getRank();
        int Ky = p.getPosition().getFile();
        short[] safe = new short[8];
        Piece[] danger = new Piece[8];
        ArrayList Knights = new ArrayList();
        int key = Check.scanCheckKing(p, this.board, safe, danger, Knights);
        if(key != 0) {
            int k;
            for(k = 0; k < 8; ++k) {
                if(safe[k] == 1) {
                    switch(k) {
                        case 0:
                            if(Check.scanCheckKingSaved(Kx + 1, Ky - 1, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                            break;
                        case 1:
                            if(Check.scanCheckKingSaved(Kx + 1, Ky, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                            break;
                        case 2:
                            if(Check.scanCheckKingSaved(Kx + 1, Ky + 1, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                            break;
                        case 3:
                            if(Check.scanCheckKingSaved(Kx, Ky - 1, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                            break;
                        case 4:
                            if(Check.scanCheckKingSaved(Kx, Ky + 1, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                            break;
                        case 5:
                            if(Check.scanCheckKingSaved(Kx - 1, Ky - 1, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                            break;
                        case 6:
                            if(Check.scanCheckKingSaved(Kx - 1, Ky, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                            break;
                        case 7:
                            if(Check.scanCheckKingSaved(Kx - 1, Ky + 1, p.getColor(), this.board)) {
                                safe[k] = 0;
                            }
                    }
                }
            }

            for(k = 0; k < 8; ++k) {
                if(safe[k] == 1) {
                    return 11;
                }
            }

            for(k = 0; k < 8; ++k) {
                if(danger[k] != null) {
                    danger[k] = Check.canBlock(danger[k].getPosition(), danger[k].getColor(), p.getPosition(), danger[k], k);
                }
            }

            for(k = 0; k < 8; ++k) {
                if(danger[k] != null) {
                    return 12;
                }
            }

            if(Knights.size() > 1) {
                return 12;
            } else if(Knights.size() != 0) {
                Piece var9 = (Piece)Knights.get(0);
                if(Check.scanCheck(var9.getPosition().getRank(), var9.getPosition().getFile(), var9.getColor(), var9.board)) {
                    return 11;
                } else {
                    return 12;
                }
            } else {
                return 11;
            }
        } else {
            return 0;
        }
    }

    public void resetEnPassent(int player) {
        Piece p;
        int i;
        if(player == 0) {
            for(i = 0; i < 8; ++i) {
                if((p = this.board.getSquare(5, i).getPiece()) != null && p instanceof Pawn) {
                    ((Pawn)p).remEnPassent();
                }
            }
        } else {
            for(i = 0; i < 8; ++i) {
                if((p = this.board.getSquare(4, i).getPiece()) != null && p instanceof Pawn) {
                    ((Pawn)p).remEnPassent();
                }
            }
        }
    }

    /**
     * Prints and states the commands of that can be used for voice
     */
    public void sayCommands(){
        System.out.print("Game Commands:\n\tSay Quit to save and exit game\n\tSay repeat to hear the last 5 moves\n\tSay draw if the game is a draw\n\tSay Resign to conceded and end the game ");
        tts.speak("Say Quit to save and exit game. Say repeat to hear the last 5 moves. Say draw if the game is a draw. Say Resign to conceded and end the game.", 2.0f, false, true);
    }

    /**
     * @post Loads moves from saved game into LoadedMoves attribute of chess class
     */
    public void loadMoves (){
        // The name of the file to open.


        String fileName = "DanChunn/src/production/DanChunn/util/Load.txt";

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(" ");
                int[] OneMove = new int[4];
                OneMove[0] = Integer.parseInt(splitLine[0].substring(0,1));
                OneMove[1] = Integer.parseInt(splitLine[0].substring(1,2));
                OneMove[2] = Integer.parseInt(splitLine[1].substring(0,1));
                OneMove[3] = Integer.parseInt(splitLine[1].substring(1,2));

                LoadedMoves.add(OneMove);

                System.out.println(line);
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
            tts.speak("Unable to open file '" +
                    fileName + "'", 2.0f, false, true);
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            tts.speak("Error reading file '" +
                    fileName + "'", 2.0f, false, true);
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }

    /*public static void main(String[] args) {
        Chess game = new Chess();
        game.start();
    }*/
}
