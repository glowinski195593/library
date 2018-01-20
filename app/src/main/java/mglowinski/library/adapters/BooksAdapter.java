package mglowinski.library.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mglowinski.library.R;
import mglowinski.library.fragments.RentBookFragment;
import mglowinski.library.model.Book;
import mglowinski.library.model.User;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

    private Map<String, Integer> map;
    private Boolean check = false;
    private Context context;
    private Comparator<Book> comparator;
    private String userId;

    private final SortedList<Book> sortedList = new SortedList<>(Book.class, new SortedList.Callback<Book>() {
        @Override
        public int compare(Book a, Book b) {
            return comparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Book oldItem, Book newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Book item1, Book item2) {
            return item1 == item2;
        }
    });

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, author, publicationYear;
        ImageView imageBookAvaible;
        ImageView imageBookNotAvaible;
        RelativeLayout expandableLayout;
        Button button;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageBookAvaible = itemView.findViewById(R.id.image_book_avaible);
            this.imageBookNotAvaible = itemView.findViewById(R.id.image_book_not_avaible);
            this.expandableLayout = itemView.findViewById(R.id.expandableLayout);
            this.title = itemView.findViewById(R.id.titleId);
            this.author = itemView.findViewById(R.id.authorId);
            this.button = itemView.findViewById(R.id.buttonId);
            this.publicationYear = itemView.findViewById(R.id.publicationYear);
        }
    }

    public BooksAdapter(Map<String, Integer> map, Context context, Comparator<Book> comparator, String userId) {
        this.map = map;
        this.context = context;
        this.comparator = comparator;
        this.userId = userId;
    }

    public void replaceAll(List<Book> models) {
        sortedList.beginBatchedUpdates();
        for (int i = sortedList.size() - 1; i >= 0; i--) {
            final Book model = sortedList.get(i);
            if (!models.contains(model)) {
                sortedList.remove(model);
            }
        }
        sortedList.addAll(models);
        sortedList.endBatchedUpdates();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_book_item, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check) {
                    myViewHolder.expandableLayout.animate()
                            .alpha(0.0f)
                            .setDuration(1000);
                    myViewHolder.expandableLayout.setVisibility(View.GONE);
                    check = true;
                    //  myViewHolder.schedule.setVisibility(View.VISIBLE);

                } else {
                    myViewHolder.expandableLayout.setVisibility(View.VISIBLE);
                    myViewHolder.expandableLayout.animate()
                            .alpha(1.0f)
                            .setDuration(1000);
                    check = false;
                }
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final Book book = sortedList.get(listPosition);
        String i = book.getBookId();
        final int availability = map.get(i);
        if (availability == 0) {
            holder.imageBookAvaible.setVisibility(View.VISIBLE);
            holder.imageBookNotAvaible.setVisibility(View.GONE);
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("book", book);
                    bundle.putSerializable("userId", userId);
                    RentBookFragment rentBookFragment = new RentBookFragment();
                    rentBookFragment.setArguments(bundle);
                    FragmentActivity activity = (FragmentActivity) context;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_main_fragment_container, rentBookFragment, RentBookFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else if (availability == 1) {
            holder.imageBookAvaible.setVisibility(View.GONE);
            holder.imageBookNotAvaible.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
        }
        holder.title.setText(book.getBookTitle());
        holder.author.setText(book.getBookAuthor());
        holder.publicationYear.setText(book.getBookPublicationYear());
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void updateAnswers(List<Book> books, Map<String, Integer> map) {
        sortedList.clear();
        sortedList.addAll(books);
        this.map = map;
        notifyDataSetChanged();
    }
}