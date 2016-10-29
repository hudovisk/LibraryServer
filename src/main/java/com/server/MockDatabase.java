package com.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * Created by Hudo on 29/10/2016.
 */
public class MockDatabase {

    private static MockDatabase instance;

    public HashMap<String, Timer> bookTimerMap = new HashMap<String, Timer>();
    public List<Book> booksList = new ArrayList<Book>();
    public List<Client> clientList = new ArrayList<Client>();

    public static MockDatabase getInstance() {
        if(instance == null) {
            instance = new MockDatabase();
        }

        return instance;
    }

    private MockDatabase() {
        booksList.add(new Book("Teste1"));
        booksList.add(new Book("Teste2"));
        booksList.add(new Book("Teste3"));
        booksList.add(new Book("Teste4"));
    }
}
