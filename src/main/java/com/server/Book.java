package com.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * Essa classe representa um livro. Contém informações como o nome e a lista
 * de pessoas na lista de reserva do livro.
 */
public class Book implements Serializable {
    private String name;
    private String owner;
    private Date returnDate;
    private Date reservationExpiryDate;
    private List<String> reservationList = new ArrayList<String>();

    public Book() {
    }

    public Book(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public List<String> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<String> reservationList) {
        this.reservationList = reservationList;
    }

    public Date getReservationExpiryDate() {
        return reservationExpiryDate;
    }

    public void setReservationExpiryDate(Date reservationExpiryDate) {
        this.reservationExpiryDate = reservationExpiryDate;
    }

    /***
     * Notifica o primeiro item da lista de clientes interessados e invoca o callback.
     */
    public void notifyReservee() {

    }


    @Override
    public String toString()
    {
        if(owner != null)
        {
            return name + " - " + returnDate.toString();
        }
        else if(! reservationList.isEmpty())
        {
            return name + " - Reserved to " + reservationList.get(0) + " until " + reservationExpiryDate.toString();
        }
        return name;
    }
}

