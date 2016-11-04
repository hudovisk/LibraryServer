package com.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * Classe cliente, contendo informações como o nome, se tem alguma penalidade e a lista de livros que possui no momento.
 */
public class Client implements Serializable{
    private String name;
    private List<Book> mNotificationBooks = new ArrayList<Book>();
    private List<Book> mBookList = new ArrayList<Book>();
    private Date penaltyValidationDate;

    public Client(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBookList() {
        return mBookList;
    }

    public void setBookList(List<Book> bookList) {
        mBookList = bookList;
    }

    public void addBook(Book newBook)
    {
        mBookList.add(newBook);
    }

    public void removeBook(Book oldBook)
    {
        mBookList.remove(oldBook);
    }

    public Date getPenaltyValidationDate() {
        return penaltyValidationDate;
    }

    public void setPenaltyValidationDate(Date penaltyValidationDate) {
        this.penaltyValidationDate = penaltyValidationDate;
    }

    public List<Book> getNotificationBooks() {
        return mNotificationBooks;
    }

    public void setNotificationBooks(List<Book> mNotificationBooks) {
        this.mNotificationBooks = mNotificationBooks;
    }
}