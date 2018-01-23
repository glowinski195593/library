package mglowinski.library.adapters;

/**
 * Created by macglo on 25.09.17.
 */

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import mglowinski.library.R;
import mglowinski.library.model.Borrow;

public class BorrowBooksAdapter extends RecyclerView.Adapter<BorrowBooksAdapter.UserViewHolder> {

    private List<Borrow> borrowList;
    private String dateReturn;
    private int maxDaysToPickup = 7;
    private int actualDaysToPickup;
    private long diffDays;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public BorrowBooksAdapter(List<Borrow> borrowList) {
        this.borrowList = borrowList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_borrow_book_item, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.textViewTitle.setText(borrowList.get(position).getBook().getBookTitle());
        holder.textViewAuthor.setText(borrowList.get(position).getBook().getBookAuthor());
        holder.textViewDateBorrow.setText(borrowList.get(position).getDateBorrow());
        holder.textViewDateReturn.setText(calculateDateOfReturnBook());
        holder.textViewIsbn.setText(borrowList.get(position).getBook().getBookIsbn());
        holder.textViewPublicationDate.setText(borrowList.get(position).getBook().getBookPublicationYear());
        try {
            actualDaysToPickup = calculateDaysToPickup(borrowList.get(position).getDateBorrow());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (actualDaysToPickup == 1) {
            holder.textViewPickupTime.setText(actualDaysToPickup + ". dnia! Inaczej anulujemy rezerwacje. ");
        } else {
            holder.textViewPickupTime.setText(actualDaysToPickup + ". dni");
        }
    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewTitle;
        AppCompatTextView textViewAuthor;
        AppCompatTextView textViewDateBorrow;
        AppCompatTextView textViewDateReturn;
        AppCompatTextView textViewPickupTime;
        AppCompatTextView textViewIsbn;
        AppCompatTextView textViewPublicationDate;

        public UserViewHolder(View view) {
            super(view);
            prepareView(view);
        }

        public void prepareView(View view) {
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewAuthor = view.findViewById(R.id.textViewAuthor);
            textViewDateBorrow = view.findViewById(R.id.textViewDateBorrow);
            textViewDateReturn = view.findViewById(R.id.textViewDateReturn);
            textViewIsbn = view.findViewById(R.id.textViewIsbn);
            textViewPublicationDate = view.findViewById(R.id.textViewPublicationDate);
            textViewPickupTime = view.findViewById(R.id.textViewPickupTime);
        }
    }

    public String calculateDateOfReturnBook() {
        Calendar cal = Calendar.getInstance();
        Date dateActual = cal.getTime();
        cal.setTime(dateActual);
        cal.add(Calendar.MONTH, 3);
        dateActual = cal.getTime();
        dateReturn = simpleDateFormat.format(dateActual);
        return dateReturn;
    }

    public int calculateDaysToPickup(String date) throws ParseException {
        Date dateActual;
        Date dateReservation = simpleDateFormat.parse(date);
        int actualDaysToPickup;
        Calendar cal2 = Calendar.getInstance();
        dateActual = cal2.getTime();
        long d1 = dateReservation.getTime();
        long d2 = dateActual.getTime();
        //in milliseconds
        long diff = d2 - d1;
        diffDays = diff / (24 * 60 * 60 * 1000);
        actualDaysToPickup = maxDaysToPickup - Integer.parseInt(Long.toString(diffDays));
        return actualDaysToPickup;
    }
}