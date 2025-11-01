package com.chess.chess_sever.service;

public class Request {
    public String from;
    public String to;
    public String promotionChoice;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPromotionChoice() {
        return promotionChoice;
    }

    public void setPromotionChoice(String promotionChoice) {
        this.promotionChoice = promotionChoice;
    }
}
