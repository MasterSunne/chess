package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoard {

        // Board dimensions.
        private static final int BOARD_SIZE_IN_SQUARES = 8;
        private static final int SQUARE_SIZE_IN_PADDED_CHESS_CHARS = 1;
        private static final int BORDER_SIZE_IN_PADDED_NORMAL_CHARS = 3;


        public static void main(String[] args) {
            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            out.print(ERASE_SCREEN);

            drawWhiteView(out);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
        }

    private static void drawWhiteView(PrintStream out) {
        drawLetterForward(out);
        drawChessBoard(out);
        drawLetterForward(out);
    }

    private static void drawLetterForward(PrintStream out) {
        setBlack(out);

        String[] headers = {"   ", " a ", " b ", " c ", " d ", " e ", " f ", " g " };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
    }

    private static void drawChessBoard(PrintStream out) {
    }


    private static void drawHeaders(PrintStream out) {


        }

        private static void drawHeader(PrintStream out, String headerText) {
            int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
            int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

            out.print(EMPTY.repeat(prefixLength));
            printHeaderText(out, headerText);
            out.print(EMPTY.repeat(suffixLength));
        }

        private static void printHeaderText(PrintStream out, String player) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);

            out.print(player);

            setBlack(out);
        }

        private static void drawTicTacToeBoard(PrintStream out) {

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

                drawRowOfSquares(out);

                if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                    // Draw horizontal row separator.
                    drawHorizontalLine(out);
                    setBlack(out);
                }
            }
        }

        private static void drawRowOfSquares(PrintStream out) {

            for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                    setWhite(out);

                    if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                        out.print(EMPTY.repeat(prefixLength));
                        printPlayer(out, rand.nextBoolean() ? X : O);
                        out.print(EMPTY.repeat(suffixLength));
                    }
                    else {
                        out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                    }

                    if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                        // Draw vertical column separator.
                        setRed(out);
                        out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                    }

                    setBlack(out);
                }

                out.println();
            }
        }

        private static void drawHorizontalLine(PrintStream out) {

            int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                    (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

            for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
                setRed(out);
                out.print(EMPTY.repeat(boardSizeInSpaces));

                setBlack(out);
                out.println();
            }
        }

        private static void setWhite(PrintStream out) {
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_COLOR_WHITE);
        }

        private static void setRed(PrintStream out) {
            out.print(SET_BG_COLOR_RED);
            out.print(SET_TEXT_COLOR_RED);
        }

        private static void setBlack(PrintStream out) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_BLACK);
        }

        private static void printPlayer(PrintStream out, String player) {
            out.print(SET_BG_COLOR_WHITE);
            out.print(SET_TEXT_COLOR_BLACK);

            out.print(player);

            setWhite(out);
        }
    }

