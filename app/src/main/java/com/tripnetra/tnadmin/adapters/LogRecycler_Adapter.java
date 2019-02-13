package com.tripnetra.tnadmin.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tnadmin.R;

import java.util.ArrayList;
import java.util.List;

public class LogRecycler_Adapter extends RecyclerView.Adapter<LogRecycler_Adapter.ViewHolder> implements Filterable {

    private List<DataAdapter> listadapter,arrayadap;

    public LogRecycler_Adapter(List<DataAdapter> listAdapter) {
        super();
        this.listadapter = listAdapter;
        this.arrayadap = listAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.log_view_recycler, parent, false));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView NameTv, EditDateTv, DatesTv, BefPriceTv, AftPriceTv, BefAvlTv, AftAvlTv,
                PnameTv,carNameTv, OcapTv,NcapTv,PLabTv,CLabTv;
        LinearLayout Caplayt;

        ViewHolder(View itemView) {
            super(itemView);

            NameTv = itemView.findViewById(R.id.nameTv);
            EditDateTv = itemView.findViewById(R.id.DateTimeTv);
            DatesTv = itemView.findViewById(R.id.BookDatetv);
            BefPriceTv = itemView.findViewById(R.id.befpricetv);
            AftPriceTv = itemView.findViewById(R.id.aftpricetv);
            BefAvlTv = itemView.findViewById(R.id.befavltv);
            AftAvlTv = itemView.findViewById(R.id.aftavltv);

            PnameTv = itemView.findViewById(R.id.packgnameTv);
            carNameTv = itemView.findViewById(R.id.carnameTv);
            PLabTv = itemView.findViewById(R.id.price_labtv);
            CLabTv = itemView.findViewById(R.id.avl_labtv);
            Caplayt = itemView.findViewById(R.id.caplayt);
            OcapTv = itemView.findViewById(R.id.bef_captv);
            NcapTv = itemView.findViewById(R.id.aft_captv);


        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {

        DataAdapter dataAdap = listadapter.get(pos);

         if(dataAdap.getNewType().equals("HOTEL")){
             vh.PnameTv.setVisibility(View.GONE);
             vh.carNameTv.setVisibility(View.GONE);
             vh.Caplayt.setVisibility(View.GONE);
         }else{
             vh.PnameTv.setText(dataAdap.getNewName());
             vh.carNameTv.setText(dataAdap.getCarName());
             vh.OcapTv.setText(dataAdap.getCap1());
             vh.NcapTv.setText(dataAdap.getCap2());
             vh.PLabTv.setText("Adult ₹ ");
             vh.CLabTv.setText("Child ₹ ");

         }

        vh.NameTv.setText(dataAdap.getGname());
        vh.EditDateTv.setText("Edited On " + dataAdap.getMobile());
        vh.DatesTv.setText(dataAdap.getInDate() + " " + dataAdap.getOutDate());
        vh.BefPriceTv.setText(dataAdap.getHname());
        vh.AftPriceTv.setText(dataAdap.getRName());
        vh.BefAvlTv.setText(dataAdap.getType());
        vh.AftAvlTv.setText(dataAdap.getStatus());

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
                        if (filterdata.getGname().toLowerCase().contains(charString) ) {
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