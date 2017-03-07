package edu.salleurl.ls30394.to_dolist.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.salleurl.ls30394.to_dolist.R;
import edu.salleurl.ls30394.to_dolist.model.Task;

/**
 * Task adapter for a recycler view List
 * Created by avoge on 07/03/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private static final long PENDING_REMOVAL_TIMEOUT = 10 * 1000;

    private List<Task> taskList;

    private List<Task> tasksPendingRemoval;

    private android.os.Handler handler;

    private HashMap<Task, Runnable> pendingRunnables;

    private Context context;

    public TaskAdapter(Context context){
        this.context = context;

        taskList = new ArrayList<>();
        tasksPendingRemoval = new ArrayList<>();

        handler = new android.os.Handler();
        pendingRunnables = new HashMap<>();
    }


    /**
     *
     * @return
     */
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    /**
     *
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     */
    public void sortItemsByPriority(){}

    /**
     *
     */
    public void sortItemsByDate(){}


    @Override
    /**
     *
     */
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new TaskViewHolder(parent);
    }

    @Override
    /**
     *
     */
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        final Task taskItem = taskList.get(position);

        holder.taskDate.setText(taskItem.getDateOfCreation().toString());
        holder.taskdescription.setText(taskItem.getDescription());


        switch(taskItem.getPriority()){
            case Task.TASK_PRIORITY_HI:
                holder.taskIcon.setImageResource(R.drawable.ic_task_high_priority);
                break;
            case Task.TASK_PRIORITY_NORMAL:
                holder.taskIcon.setImageResource(R.drawable.ic_task_normal_priority);
                break;
            case Task.TASK_PRIORITY_LO:
                holder.taskIcon.setImageResource(R.drawable.ic_task_low_priority);
                break;
        }

        if(tasksPendingRemoval.contains(taskItem)){

            holder.textsWrapper.setVisibility(View.GONE);
            holder.taskIcon.setVisibility(View.GONE);
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.RED);

            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingTaskRemoval = pendingRunnables.get(taskItem);
                    pendingRunnables.remove(taskItem);

                    if(pendingTaskRemoval != null){
                        handler.removeCallbacks(pendingTaskRemoval);
                    }
                    tasksPendingRemoval.remove(taskItem);
                    notifyItemChanged(taskList.indexOf(taskItem));
                }
            });

        }else{
            holder.textsWrapper.setVisibility(View.VISIBLE);
            holder.taskIcon.setVisibility(View.VISIBLE);
            holder.undoButton.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }


    /**
     *
     * @param position
     * @return
     */
    public Object getItem(int position) {
        return taskList.get(position);
    }

    /**
     *
     * @param task
     */
    public void addTask(Task task){
        taskList.add(task);
        notifyItemInserted(taskList.size()-1);
    }

    /**
     *
     * @param position
     */
    public void setPendingRemoval(int position){
        final Task taskItem = taskList.get(position);

        if(!tasksPendingRemoval.contains(taskItem)){

            tasksPendingRemoval.add(taskItem);
            notifyItemChanged(position);

            Runnable pendingTaskRemoval = new Runnable() {
                @Override
                public void run() {
                    removeItem(taskList.indexOf(taskItem));
                }
            };
            handler.postDelayed(pendingTaskRemoval, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(taskItem, pendingTaskRemoval);
        }
    }

    /**
     *
     * @param position
     */
    public void removeItem(int position){
        Task taskItem = taskList.get(position);

        if(tasksPendingRemoval.contains(taskItem))
            tasksPendingRemoval.remove(taskItem);

        if(taskList.contains(taskItem)){
            taskList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     *
     * @param position
     * @return
     */
    public boolean isPendingRemoval(int position){
        return tasksPendingRemoval.contains(taskList.get(position));
    }

    /**
     *
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder{

        protected ImageView taskIcon;
        protected TextView taskdescription;
        protected TextView taskDate;

        protected Button undoButton;

        protected LinearLayout textsWrapper;

        public TaskViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false));

            taskIcon = (ImageView) itemView.findViewById(R.id.task_icon);
            taskdescription = (TextView)itemView.findViewById(R.id.task_description);
            taskDate = (TextView)itemView.findViewById(R.id.task_date);
            textsWrapper = (LinearLayout) itemView.findViewById(R.id.task_texts);
            undoButton = (Button)itemView.findViewById(R.id.task_undoDelete_btn);
        }
    }
}
