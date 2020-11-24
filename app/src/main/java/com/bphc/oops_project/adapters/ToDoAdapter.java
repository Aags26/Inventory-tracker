package com.bphc.oops_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.helper.OnItemClickListener;
import com.bphc.oops_project.models.ToDo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    private ArrayList<ToDo> toDos;
    private OnItemClickListener listener;

    public ToDoAdapter(ArrayList<ToDo> toDos) {
        this.toDos = toDos;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, description, date, time;
        ImageView deleteImage, tickImage;
        OnItemClickListener listener;

        public ToDoViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            this.listener = listener;

            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            date = itemView.findViewById(R.id.task_date);
            time = itemView.findViewById(R.id.task_time);
            deleteImage = itemView.findViewById(R.id.delete_task_image);
            tickImage = itemView.findViewById(R.id.image_task_done);

            deleteImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, v.getId());
                }
            }

        }
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_todo, parent, false);
        return new ToDoAdapter.ToDoViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        ToDo toDo = toDos.get(position);
        holder.title.setText(toDo.getTitle());
        holder.description.setText(toDo.getDescription());
        holder.date.setText(toDo.getTasktime().split(" ")[0]);
        holder.time.setText(toDo.getTasktime().split(" ")[1]);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(toDo.getTasktime());
            long millis = date.getTime();
            if (Calendar.getInstance().getTimeInMillis() > millis)
                holder.tickImage.setVisibility(View.VISIBLE);
            else
                holder.tickImage.setVisibility(View.GONE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return toDos.size();
    }

}
