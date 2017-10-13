package com.example.joseph.amazonbookschallenge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.joseph.amazonbookschallenge.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 10/13/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    //when you change the schema, change the version number

    private static final String DATABASE_NAME = "Books.db";

    private static final String TABLE_NAME = "Books";
    private static final String COLUMN_TITLE = "Title";
    private static final String COLUMN_IMAGE = "ImageURL";
    private static final String COLUMN_AUTHOR = "Author";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_TITLE + " TEXT PRIMARY KEY, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT" +
                ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE ID EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long saveBook(Book book){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //key column name, value
        contentValues.put(COLUMN_TITLE, book.getTitle());
        contentValues.put(COLUMN_IMAGE, book.getImageURL());
        contentValues.put(COLUMN_AUTHOR, book.getAuthor());

        //database.insert returns row value where this data was saved
        long isSaved = database.insert(TABLE_NAME, null, contentValues);

        return isSaved;

    }

    public List<Book> getBookList(){

        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase databse = this.getWritableDatabase();

        String QUERY = "SELECT * from " + TABLE_NAME;
        Cursor cursor = databse.rawQuery(QUERY, null);
//        String QUERY = "SELECT * from " + TABLE_NAME + " where name = ?";
//        Cursor cursor = databse.rawQuery(QUERY, new String[]{"34"});

        //returns boolean true if there is a record
        if(cursor.moveToFirst()){
            do {
                Book book = new Book(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2));
                bookList.add(book);
            } while(cursor.moveToNext());
        }

        return bookList;

    }

}
