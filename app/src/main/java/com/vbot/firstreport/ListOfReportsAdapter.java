package com.vbot.firstreport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vbot.firstreport.ReportForms.DailyDeclarationSheet;
import com.vbot.firstreport.ReportForms.ThirdPartyInjuryForm;
import com.vbot.firstreport.common.ItemClickListener;

import java.util.List;

public class ListOfReportsAdapter extends RecyclerView.Adapter<ListOfReportsAdapter.MyViewHolder> {
    private Context context;
    private List<String> listOfReports;
    private ListOfReportsAdapter.ListOfReportsAdapterListener listener;
    private String report, letter;

    public ListOfReportsAdapter(Context context, List<String> listOfReports) {
        this.context = context;
        this.listOfReports = listOfReports;
        try {
            listener = (ListOfReportsAdapter.ListOfReportsAdapterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvReport;
        public ImageView item_letter;

        ItemClickListener itemClickListener;
        public MyViewHolder(View view) {
            super(view);
            tvReport = view.findViewById(R.id.tvReport);
            item_letter = view.findViewById(R.id.item_letter);

            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener=itemClickListener;
        }
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, this.getLayoutPosition());
        }
    }

    @Override
    public ListOfReportsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fetch_choice, parent, false);

        return new ListOfReportsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListOfReportsAdapter.MyViewHolder holder, final int position) {
        report = listOfReports.get(position);
        letter = String.valueOf(listOfReports.get(position).charAt(0));
        TextDrawable drawableText = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.defaultFromStyle(Typeface.BOLD)).fontSize(40)
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRound(letter, Color.red(200));
        holder.tvReport.setText(String.valueOf(report));
        holder.item_letter.setImageDrawable(drawableText);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Context context = v.getContext();
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent = new Intent(context, ThirdPartyInjuryForm.class);
                        break;
                    case 1:
                        intent = new Intent(context, DailyDeclarationSheet.class);
                        break;
                }
                    context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfReports.size();
    }

    public interface ListOfReportsAdapterListener {
        void applyTextsListOfReports(String reports);
    }

}