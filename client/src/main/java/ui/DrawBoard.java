package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static ui.EscapeSequences.*;

public class DrawBoard {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHESS_CHARS = 1;
    private static final int BORDER_SIZE_IN_PADDED_NORMAL_CHARS = 3;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        drawWhiteView(out,board);
        out.println();
        drawBlackView(out,board);

        // Reset terminal to default colors
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
    }

    private static void drawWhiteView(PrintStream out, ChessBoard board) {
        drawLetterForward(out);
        drawChessBoardWhite(out,board);
        drawLetterForward(out);
    }

    private static void drawBlackView(PrintStream out, ChessBoard board) {
        drawLetterBackward(out);
        drawChessBoardBlack(out,board);
        drawLetterBackward(out);
    }

    private static void drawLetterForward(PrintStream out) {
        String[] headers = {"   ", "\u2005 a\u2003", "\u2004\u2005b", SPACER+"c", SPACER+"d", SPACER+"e", SPACER+"f", SPACER+"g", SPACER+"h", "\u2003\u2006\u2006  " };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            printHeaderText(out, headers[boardCol]);
        }
        out.println();
    }

    private static void drawLetterBackward(PrintStream out) {
        String[] headers = {"   ", "\u2005 h\u2003", "\u2004\u2005g", SPACER+"f", SPACER+"e", SPACER+"d", SPACER+"c", SPACER+"b", SPACER+"a", "\u2003\u2006\u2006  " };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            printHeaderText(out, headers[boardCol]);
        }
        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        setBorder(out);
        out.print(player);
        setBlack(out);
    }

    private static void drawChessBoardWhite(PrintStream out, ChessBoard board) {
        for (int row = 8; row >= 1; row--) {
            //print the side number first
            setBorder(out);
            out.print(" " + row + " ");

            for (int column = 1; column <= 8; column++) {
                printBoard(out, board, row, column);
            }
            //print the side number again last
            setBorder(out);
            out.print(" " + row + " ");
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.println();

        }
    }

    private static void printBoard(PrintStream out, ChessBoard board, int row, int column) {
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = board.getPiece(position);

        if (isEven(row)) {
            if(isOdd(column)){
                out.print(SET_BG_COLOR_CHESS_CREAM);
            } else {
                out.print(SET_BG_COLOR_CHESS_GREEN);
            }
        } else{
            if(isOdd(column)){
                out.print(SET_BG_COLOR_CHESS_GREEN);
            } else {
                out.print(SET_BG_COLOR_CHESS_CREAM);
            }
        }

        String pieceChar = EMPTY;
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                setWhitePiece(out);
                pieceChar = TYPE_TO_CHAR_MAP_WHITE.get(piece.getPieceType());
                out.print(pieceChar);
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                setBlackPiece(out);
                pieceChar = TYPE_TO_CHAR_MAP_BLACK.get(piece.getPieceType());
                out.print(pieceChar);
            }
        } else if (isOdd(column)) {
            setWhiteSquare(out);
            out.print(pieceChar);
        } else if (isEven(column)){
            setBlackSquare(out);
            out.print(pieceChar);
        }
    }

    private static void drawChessBoardBlack(PrintStream out, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            //print the side number first
            setBorder(out);
            out.print(" " + row + " ");

            for (int column = 8; column >= 1; column--) {
                printBoard(out, board, row, column);
            }
            //print the side number again last
            setBorder(out);
            out.print(" " + row + " ");
            out.print(RESET_BG_COLOR);
            out.print(RESET_TEXT_COLOR);
            out.println();

        }
    }

    private static void setWhiteSquare(PrintStream out) {
        out.print(SET_TEXT_COLOR_CHESS_CREAM);
    }

    private static void setWhitePiece(PrintStream out) {
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlackSquare(PrintStream out) {
        out.print(SET_TEXT_COLOR_CHESS_GREEN);
    }

    private static void setBlackPiece(PrintStream out) {
    out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);  // Use full reset instead of setting to black
    }

    private static void setBorder(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_CHESS_CREAM);
    }

    private static final Map<ChessPiece.PieceType, String> TYPE_TO_CHAR_MAP_WHITE = Map.of(
            ChessPiece.PieceType.PAWN, WHITE_PAWN,
            ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT,
            ChessPiece.PieceType.ROOK, WHITE_ROOK,
            ChessPiece.PieceType.QUEEN, WHITE_QUEEN,
            ChessPiece.PieceType.KING, WHITE_KING,
            ChessPiece.PieceType.BISHOP, WHITE_BISHOP);

    private static final Map<ChessPiece.PieceType, String> TYPE_TO_CHAR_MAP_BLACK = Map.of(
            ChessPiece.PieceType.PAWN, BLACK_PAWN,
            ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT,
            ChessPiece.PieceType.ROOK, BLACK_ROOK,
            ChessPiece.PieceType.QUEEN, BLACK_QUEEN,
            ChessPiece.PieceType.KING, BLACK_KING,
            ChessPiece.PieceType.BISHOP, BLACK_BISHOP);

    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static boolean isOdd(int number) {
        return number % 2 != 0;
    }
}

