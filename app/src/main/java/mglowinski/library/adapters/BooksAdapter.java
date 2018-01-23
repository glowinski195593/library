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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mglowinski.library.R;
import mglowinski.library.fragments.PagerFragment;
import mglowinski.library.model.Book;
import mglowinski.library.model.Borrow;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

    private Map<Book, Integer> mapNumberOfBooks;
    private Boolean check = false;
    private Context context;
    private Comparator<Book> comparator;
    private String userId;
    private List<Book> listBooksRepeated;
    private List<Book> listBooksToSend;
    private List<Borrow> borrowListFromResponse;
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

        TextView title, author, numberOfBooks;
        ImageView imageBookAvailable;
        ImageView imageBookInaccessible;
        RelativeLayout expandableLayout;
        Button button;

        public MyViewHolder(View view) {
            super(view);
            prepareView(view);
        }

        public void prepareView(View view) {
            this.imageBookAvailable = view.findViewById(R.id.image_book_avaible);
            this.imageBookInaccessible = view.findViewById(R.id.image_book_not_avaible);
            this.expandableLayout = view.findViewById(R.id.expandableLayout);
            this.title = view.findViewById(R.id.titleId);
            this.author = view.findViewById(R.id.authorId);
            this.button = view.findViewById(R.id.buttonId);
            this.numberOfBooks = view.findViewById(R.id.numberOfBooksId);
        }
    }

    public BooksAdapter(Map<Book, Integer> mapNumberOfBooks, Context context, Comparator<Book> comparator, String userId) {
        this.mapNumberOfBooks = mapNumberOfBooks;
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
            public void onClick(View view) {
                showOrHideLayout(myViewHolder);
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        final Book book = sortedList.get(listPosition);
        final int count = mapNumberOfBooks.get(book);
        if (count != 0) {
            showAvailableIcon(holder);
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    listBooksToSend = getAllBooksWithTheSameTitle(book);
                    Bundle bundle = new Bundle();
                    bundle.putInt("CURRENT_POSITION", 0);
                    bundle.putSerializable("books", (Serializable) listBooksToSend);
                    bundle.putString("userId", userId);
                    showBookDetailsFragment(bundle);
                }
            });
        } else if (count == 0) {
            showInaccessibleIcon(holder);
            holder.button.setVisibility(View.GONE);
        }
        holder.title.setText(book.getBookTitle());
        holder.author.setText(book.getBookAuthor());
        holder.numberOfBooks.setText(mapNumberOfBooks.get(book).toString() + ". szt");
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void updateAnswers(List<Book> books, Map<Book, Integer> mapNumberOfBooks, List<Book> listBooksRepeated, List<Borrow> borrowListFromResponse) {
        sortedList.clear();
        sortedList.addAll(books);
        this.mapNumberOfBooks = mapNumberOfBooks;
        this.listBooksRepeated = listBooksRepeated;
        this.borrowListFromResponse = borrowListFromResponse;
        notifyDataSetChanged();
    }

    public void showOrHideLayout(MyViewHolder myViewHolder) {
        if (!check) {
            myViewHolder.expandableLayout.animate()
                    .alpha(0.0f)
                    .setDuration(1000);
            myViewHolder.expandableLayout.setVisibility(View.GONE);
            check = true;

        } else {
            myViewHolder.expandableLayout.setVisibility(View.VISIBLE);
            myViewHolder.expandableLayout.animate()
                    .alpha(1.0f)
                    .setDuration(1000);
            check = false;
        }
    }

    public void showAvailableIcon(MyViewHolder myViewHolder) {
        myViewHolder.imageBookAvailable.setVisibility(View.VISIBLE);
        myViewHolder.imageBookInaccessible.setVisibility(View.GONE);
    }

    public void showInaccessibleIcon(MyViewHolder myViewHolder) {
        myViewHolder.imageBookAvailable.setVisibility(View.GONE);
        myViewHolder.imageBookInaccessible.setVisibility(View.VISIBLE);
    }

    public List<Book> getAllBooksWithTheSameTitle(Book book) {
        List<Book> listBooksToSend = new ArrayList<>();
        boolean exist, exist2;
        exist = false;
        if (borrowListFromResponse.size() == 0) {
            listBooksToSend.add(book);
            for (int i = 0; i < listBooksRepeated.size(); i++) {
                if (book.getBookTitle().equals(listBooksRepeated.get(i).getBookTitle())) {
                    listBooksToSend.add(listBooksRepeated.get(i));
                }
            }
        } else {
            for (int i = 0; i < borrowListFromResponse.size(); i++) {
                if (book.getBookId().equals(borrowListFromResponse.get(i).getBook().getBookId())) {
                    exist = true;
                }
            }
            if (!exist) {
                listBooksToSend.add(book);
            }

            for (int i = 0; i < listBooksRepeated.size(); i++) {
                exist2 = false;
                if (book.getBookTitle().equals(listBooksRepeated.get(i).getBookTitle())) {
                    for (int j = 0; j < borrowListFromResponse.size(); j++) {
                        if (listBooksRepeated.get(i).getBookTitle().equals(borrowListFromResponse.get(j).getBook().getBookTitle())) {
                            exist2 = true;
                        }
                        if (!listBooksRepeated.get(i).getBookId().equals(borrowListFromResponse.get(j).getBook().getBookId())
                                && listBooksRepeated.get(i).getBookTitle().equals(borrowListFromResponse.get(j).getBook().getBookTitle())) {
                            listBooksToSend.add(listBooksRepeated.get(i));
                        }
                    }
                    if (!exist2) {
                        listBooksToSend.add(listBooksRepeated.get(i));
                    }
                }
            }
        }
        return listBooksToSend;
    }

    public void showBookDetailsFragment(Bundle bundle) {
        PagerFragment pagerFragment = new PagerFragment();
        pagerFragment.setArguments(bundle);
        FragmentActivity activity = (FragmentActivity) context;
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_fragment_container, pagerFragment, "swipe_view_fragment")
                .addToBackStack(null)
                .commit();
    }
}