package com.my_jlox.lox;

class Token {
    // final means once assigned, cannot be changed
    final TokenType type;
    final String lexeme;
    // Object can hold any data type (Union type)
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}