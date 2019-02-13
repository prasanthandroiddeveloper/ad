package com.tripnetra.tnadmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.payments.PaymentVoucherActivity;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepRecycleAdapter extends RecyclerView.Adapter<PaymentRepRecycleAdapter.ViewHolder> implements Filterable {

    private List<DataAdapter> listadapter, arrayadap;

    public PaymentRepRecycleAdapter(List<DataAdapter> listAdapter) {
        super();
        this.listadapter = listAdapter;
        this.arrayadap = listAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pay_rep, parent, false));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView HNameTv, PnrNoTv,BookStatusTV, RoomTypeTV,CinTV, PayStatTv,PriceTv;
        private Context context;
        ViewHolder(View itemView) {

            super(itemView);

            HNameTv = itemView.findViewById(R.id.HNameTV);
            PnrNoTv = itemView.findViewById(R.id.PnrTv);
            BookStatusTV = itemView.findViewById(R.id.PnrStatTv);
            RoomTypeTV = itemView.findViewById(R.id.RoomTypeTv);
            CinTV = itemView.findViewById(R.id.CInTv);
            PayStatTv = itemView.findViewById(R.id.PaidTv);
            PriceTv = itemView.findViewById(R.id.PriceTv);
            context = itemView.getContext();
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent =  new Intent(context, PaymentVoucherActivity.class);
            intent.putExtra("pnrnumber", listadapter.get(getAdapterPosition()).getHid());
            context.startActivity(intent);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {

        DataAdapter dataAdap = listadapter.get(pos);

        vh.HNameTv.setText(dataAdap.getHname());
        vh.PnrNoTv.setText(dataAdap.getHid());
        vh.BookStatusTV.setText(dataAdap.getStatus());
        vh.RoomTypeTV.setText(dataAdap.getType());
       vh.CinTV.setText(dataAdap.getInDate());

        vh.PriceTv.setText(dataAdap.getGname());
        String ss = dataAdap.getMobile();
        if(ss.equals("")||ss.equals("null")){
            vh.PayStatTv.setText("-");
        }else {
            vh.PayStatTv.setText(ss);
        }
    }

    @Override
    public int getItemCount() {
        return listadapter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSeq) {
                String charString = charSeq.toString();
                if (charString.isEmpty()) {
                    listadapter = arrayadap;
                } else {
                    ArrayList<DataAdapter> filteredList = new ArrayList<>();
                    for (DataAdapter filterdata : arrayadap) {
                        if (filterdata.getHname().toLowerCase().contains(charString) || filterdata.getHid().toLowerCase().contains(charString) ||
                                filterdata.getStatus().toLowerCase().contains(charString)) {
                            filteredList.add(filterdata);
                        }
                    }
                    listadapter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listadapter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listadapter = (ArrayList<DataAdapter>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
