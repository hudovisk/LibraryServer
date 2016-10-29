package com.server;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
/**
 * Created by Hudo on 26/10/2016.
 */
@Path("/lib")
public class LibraryRestServer {

    //By default a new resource class instance is created for each request to that resource.
    //We need to mock our database then.
    private MockDatabase m_db;

    /**
     * Task usada para dar sequência na lista de reservas em caso de estouro do tempo de reserva.
     */
    private class BookTimerTask extends TimerTask {

        private Book mBook;

        public BookTimerTask(Book book) {
            super();
            mBook = book;
        }

        @Override
        public void run() {
            if(!mBook.getReservationList().isEmpty()) {
                mBook.getReservationList().remove(0);

                updateReservationList(mBook);
            }
        }
    };

    public LibraryRestServer() {
        m_db = MockDatabase.getInstance();
    }

    private Client getClient(String clientName)
    {
        for(Client c : m_db.clientList)
        {
            if(c.getName().equals(clientName))
            {
                return c;
            }
        }

        Client c = new Client(clientName);
        m_db.clientList.add(c);
        return c;
    }

    private Book getBook(String bookName)
    {
        for(Book bk : m_db.booksList)
        {
            if(bk.getName().equals(bookName))
            {
                return bk;
            }
        }
        return null;
    }

    /***
     * Adiciona um número de dias a data. Na implementação atual, está na verdade adicionando segundos.
     * @param date - data na qual o tempo será adicionado
     * @param days - número de dias a ser adicionado
     * @return resultado da adição
     */
    private Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//        cal.add(Calendar.DATE, days);
        cal.add(Calendar.SECOND, days);
        return cal.getTime();
    }

    private void updateReservationList(Book book) {
        if(!book.getReservationList().isEmpty()) {
            book.setReservationExpiryDate(addDaysToDate(new Date(), 5));
            book.notifyReservee();

            Timer timer = new Timer();
            timer.schedule(new BookTimerTask(book), book.getReservationExpiryDate());
            m_db.bookTimerMap.put(book.getName(), timer);
        }
    }

    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> getBooks(@QueryParam("name") String name) {
        System.out.println(m_db.clientList.size());
        if(name == null) return m_db.booksList;

        List<Book> resultList = new ArrayList<Book>();
        for(Book book : m_db.booksList) {
            if(book.getName().contains(name)) resultList.add(book);
        }

        return resultList;
    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Client> getClients(@QueryParam("name") String name) {
        if(name == null) return m_db.clientList;

        List<Client> resultList = new ArrayList<Client>();
        for(Client c : m_db.clientList) {
            if(c.getName().contains(name)) resultList.add(c);
        }

        return resultList;
    }

    @POST
    @Path("/books/lend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response lendBook(final ClientBookParams params) {
        System.out.println(params.client);
        System.out.println(params.bookName);

        String client = params.client;
        String bookName = params.bookName;

        Client c = getClient(client);
        //Check the pre-requisites for the client
        List<Book> clientBookList = c.getBookList();

        if(c.getPenaltyValidationDate() != null)
        {
            if(c.getPenaltyValidationDate().getTime() > new Date().getTime()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("User has penalties").build();
            }
        }

        if(clientBookList.size() >= 3)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("User has 3 or more books reserved").build();
        }
        for(Book b : clientBookList)
        {
            if(b.getReturnDate().getTime() < (new Date()).getTime())
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("User has books to return").build();
            }
        }

        Book book = getBook(bookName);
        if(book == null) return Response.status(Response.Status.BAD_REQUEST).entity("Book not found").build();

        //Check the pre-requisites for the book
        if(book.getOwner() != null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Book is already taken").build();
        }

        if(!book.getReservationList().isEmpty())
        {
            if(!book.getReservationList().get(0).equals(c.getName()))
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Book is reserved").build();
            }
            book.getReservationList().remove(0);
            m_db.bookTimerMap.get(book.getName()).cancel();
        }


        book.setOwner(client);

        book.setReturnDate((addDaysToDate(new Date(), 7)));

        c.addBook(book);

        System.out.println(m_db.clientList.size());

        return Response.ok().build();
    }

    @POST
    @Path("/books/return")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnBook(final BookNameParams params)
    {
        Book book = getBook(params.bookName);
        if(book.getOwner() == null) return Response.status(Response.Status.BAD_REQUEST).entity("Book has no owner").build();

        Client client = getClient(book.getOwner());

        if(book.getReturnDate().getTime() < new Date().getTime()) {
            client.setPenaltyValidationDate((addDaysToDate(new Date(), 7)));
        }

        book.setOwner(null);
        client.removeBook(book);

        updateReservationList(book);

        return Response.ok().build();
    }

    @POST
    @Path("/books/reserve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReservationBook(final ClientBookParams params) {
        String bookName = params.bookName;
        String client = params.client;

        Book book = getBook(bookName);
        if(book == null) return Response.status(Response.Status.BAD_REQUEST).entity("Book not found").build();

        List<String> reservationList = book.getReservationList();
        for(String clientName : reservationList)
        {
            if(client.equals(clientName))
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Client already in reservation list").build();
            }
        }
        reservationList.add(client);
        return Response.ok().build();
    }

    @POST
    @Path("/books/renovate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response renovateBook(final BookNameParams params)
    {
        String bookName = params.bookName;
        Book book = getBook(bookName);
        if(book == null) return Response.status(Response.Status.BAD_REQUEST).entity("Book not found").build();
        if(book.getOwner() == null) return Response.status(Response.Status.BAD_REQUEST).entity("Book has no owner").build();

        Client client = getClient(book.getOwner());
        if(client.getPenaltyValidationDate() != null
                && client.getPenaltyValidationDate().getTime() > new Date().getTime()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User has penalties").build();
        }

        for(Book b : getClient(book.getOwner()).getBookList()) {
            if(book.getReturnDate().getTime() < (new Date()).getTime()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("User has books to return").build();
            }
        }

        if(book.getReservationList().isEmpty())
        {
            book.setReturnDate(addDaysToDate(new Date(), 7));
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Book is already reserved").build();
        }
    }
}
