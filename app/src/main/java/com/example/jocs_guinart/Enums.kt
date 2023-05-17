package com.example.jocs_guinart

enum class DIR
{
    NW, N, NE,
    W,      E,
    SW, S, SE
}

enum class GAMES
{
    TRES_EN_RATLLA,
    OTHELLO,
    PENJAT,
    DAMES
}

enum class TBL_POS // Posicions d'un taulell. Othello, dames
{
    EMPTY,
    AVAILABLE,
    WHITE,
    BLACK
};