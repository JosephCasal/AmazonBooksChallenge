package com.example.joseph.amazonbookschallenge.remote;

import com.example.joseph.amazonbookschallenge.model.Book;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by joseph on 10/13/17.
 */

public interface RemoteService {

    @GET("books.json")
    Observable<List<Book>> getBooks();

}
