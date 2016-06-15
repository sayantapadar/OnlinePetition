package apps.sayan.onlinepetition;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SAYAN on 08-06-2016.
 */
public class PetitionRecyclerAdapter extends RecyclerView.Adapter<PetitionRecyclerAdapter.MyViewHolder> {

    private Context context;

    public ArrayList<Information> data;

    private LayoutInflater inflater;

    SQLiteDatabase db;
    DBHelper helper;

    public PetitionRecyclerAdapter(Context context, ArrayList<Information> data, SQLiteDatabase db, DBHelper helper) {

        this.context = context;
        this.data = data;
        this.db=db;
        this.helper=helper;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public PetitionRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.petition_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PetitionRecyclerAdapter.MyViewHolder holder, int position) {
        holder.titleText.setText(data.get(position).title);
        holder.addressedText.setText(data.get(position).decision_maker);
        holder.makerText.setText(data.get(position).maker);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleText;
        TextView makerText;
        TextView addressedText;
        CardView container;
        public MyViewHolder(View itemView) {
            super(itemView);
            titleText= (TextView) itemView.findViewById(R.id.view_title);
            makerText= (TextView) itemView.findViewById(R.id.view_maker);
            addressedText= (TextView) itemView.findViewById(R.id.view_addressed);
            container=(CardView)itemView.findViewById(R.id.view_row_container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.view_row_container){
                context.startActivity(new Intent(context,SignActivity.class).putExtra("position",getAdapterPosition()));
            }
        }
    }
}
