package com.example.joseph.amazonbookschallenge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.joseph.amazonbookschallenge.model.Book;
import com.example.joseph.amazonbookschallenge.remote.RemoteDataSource;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView rvBookList;

    List<Book> bookList = new ArrayList<>();
    private BookListAdapter bookListAdapter;
    private RecyclerView.ItemAnimator itemAnimator;
    private RecyclerView.LayoutManager layoutManager;
    private TextView tvStatus;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvBookList = findViewById(R.id.rvBookList);
        tvStatus = findViewById(R.id.tvStatus);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemAnimator = new DefaultItemAnimator();


        db = new DatabaseHelper(this);



        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String data = sharedPreferences.getString("data", "defaultValue");
//        tvStatus.setText(data);

        if(data.equals("defaultValue")){
            Log.d(TAG, "onCreate: theres is no previous data");
            tvStatus.setText("there is no previous data");
            getData();
            Date now = new Date();
            editor.putString("data", DateFormat.getDateTimeInstance().format(now));
            editor.commit();
            Log.d(TAG, "onCreate: saved new data1");
        }else{
            Date lastTimeUpdated = new Date(data);
            Log.d(TAG, "onCreate: lastTimeUpdated: " + lastTimeUpdated);
            Date now = new Date();
//            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            double difference = ((now.getTime() - lastTimeUpdated.getTime())/1000)/60;
            Log.d(TAG, "onCreate: difference: " + difference);
            if(difference >= 30){
                tvStatus.setText("it has been 30 minutes or more, updated data");
                Log.d(TAG, "onCreate: updating data");
                getData();
                editor.putString("data", DateFormat.getDateTimeInstance().format(now));
                boolean isSaved = editor.commit();
                if(isSaved){
                    Log.d(TAG, "onCreate: saved new data2");
                }else{
                    Log.d(TAG, "onCreate: did not save data");
                }
            }else{
                tvStatus.setText("it has been " + difference + " minutes since last update");
                Log.d(TAG, "onCreate: " + "has not been 30 minutes yet");
                getDatabseBooks();
            }
        }






    }




    public void getDatabseBooks(){

        bookList = db.getBookList();
        Log.d(TAG, "getDatabseBooks: " + bookList.size());
        for (int i = 0; i < bookList.size(); i++) {
            Log.d(TAG, "usingSQLite: " + bookList.get(i).toString());
        }

        bookListAdapter = new BookListAdapter(bookList);
        rvBookList.setLayoutManager(layoutManager);
        rvBookList.setItemAnimator(itemAnimator);
        rvBookList.setAdapter(bookListAdapter);

    }




    public void getData(){

        RemoteDataSource.getBooks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<Book>, ObservableSource<Book>>() {
                    @Override
                    public ObservableSource<Book> apply(@NonNull final List<Book> books) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Book>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<Book> e) throws Exception {

                                for (Book book: books){
                                    e.onNext(book);
                                }
                                e.onComplete();
                            }
                        });
                    }
                })
//                .map(new Function<Book, Book>() {
//                    @Override
//                    public Book apply(@NonNull Book book) throws Exception {
//                        String repoName = book.getTitle();
//                        book.setTitle("My" + repoName);
//                        return book;
//                    }
//                })
                .subscribe(new Observer<Book>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(@NonNull Book book) {
                        Log.d(TAG, "onNext: " + book.getTitle());
                        bookList.add(book);
                        long isSaved = db.saveBook(book);
                        Log.d(TAG, "onNext: isSaved: " + isSaved);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                        bookListAdapter = new BookListAdapter(bookList);
                        rvBookList.setLayoutManager(layoutManager);
                        rvBookList.setItemAnimator(itemAnimator);
                        rvBookList.setAdapter(bookListAdapter);
                    }
                });
    }


}
