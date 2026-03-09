package com.my_jlox.lox;

import java.util.*;
import static com.my_jlox.lox.TokenType.*; 

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    private boolean isEnd() {
        return current >= source.length();
    }

    List<Token> scanTokens() {
        while (!isEnd()) {
            start = current;
            scanToken();
        }
        // Add EOF token at the end.
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        // Add break for each case otherwise it falls through.
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break; 
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

            case '/': 
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isEnd()) advance();
                } 
                else if (match('*')) {
                    while (!isEnd() && !(match('*') && match('/'))) {
                        if (peek() == '\n') line++;
                        advance();
                    }
                }
                else {
                    addToken(SLASH);
                }
                break;
            // Fall through and ignore whitespace.
            case ' ':
            case '\r':
            case '\t': break;
            case '\n': line++; break;
            case '"': string(); break;
            case 'o':
                if (match('r')) {
                    addToken(OR);
                }
                break;
            default:
                if (isDigit(c)) {
                    number();
                } 
                else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected) {
        if (isEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        if (isEnd()) return '\0';
        return source.charAt(current);
    }

    private char peek2() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void string() {
        while (peek() != '"' && !isEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }


    private void number() {
        while (isDigit(peek())) advance();
        // Decimal
        if (peek() == '.' && isDigit(peek2())) {
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        // Check for match in keywords map
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }
}