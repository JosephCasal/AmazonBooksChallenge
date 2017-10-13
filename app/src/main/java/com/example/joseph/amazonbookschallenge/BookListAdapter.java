package com.example.joseph.amazonbookschallenge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.joseph.amazonbookschallenge.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 10/13/17.
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder>{

    List<Book> bookList;
    Context context;

    public BookListAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Book book = bookList.get(position);

        holder.tvBookTitle.setText(book.getTitle());
        Glide.with(context).load(book.getImageURL()).into(holder.ivBookPic);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle;
        TextView tvBookAuthor;
        ImageView ivBookPic;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            ivBookPic = itemView.findViewById(R.id.ivBookPic);
        }
    }
}
