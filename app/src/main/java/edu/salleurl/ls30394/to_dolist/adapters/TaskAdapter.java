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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.salleurl.ls30394.to_dolist.MainActivity;
import edu.salleurl.ls30394.to_dolist.R;
import edu.salleurl.ls30394.to_dolist.model.Task;

/**
 * Task adapter for a recycler view List
 * Created by avoge on 07/03/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    /**
     * Desired timeout for a task's removal
     */
    private static final long PENDING_REMOVAL_TIMEOUT = 5 * 1000;

    /**
     * Activity in which the adapter is used
     */
    private Context context;

    /**
     * Set of tasks displayed on the main activity
     */
    private List<Task> taskList;

    /**
     * Set of tasks that are pending of removal
     */
    private List<Task> tasksPendingRemoval;

    /**
     * System handler for Runnables
     */
    private android.os.Handler handler;

    /**
     * Set of runnables associated to tasks pending removal
     */
    private HashMap<Task, Runnable> pendingRunnables;

    private boolean sortedByDate;

    private boolean sortedByPriority;

    /**
     * Builds a new TaskAdapter
     */
    public TaskAdapter(Context c){

        this.context = c;

        taskList = new ArrayList<>();
        tasksPendingRemoval = new ArrayList<>();

        handler = new android.os.Handler();
        pendingRunnables = new HashMap<>();

        sortedByDate = false;
        sortedByPriority = false;
    }


    /**
     * @return How many tasks are loaded in execution
     */
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new TaskViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        final Task taskItem = taskList.get(position);

        holder.taskDate.setText(taskItem.getDateOfCreation().toString());
        holder.taskdescription.setText(taskItem.getDescription());


        switch(taskItem.getPriority()){
            case Task.TASK_PRIORITY_ASAP:
                holder.taskIcon.setImageResource(R.drawable.ic_task_asap_priority);
                break;
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
            holder.itemView.setBackgroundColor(Color.LTGRAY);

            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingTaskRemoval = pendingRunnables.get(taskItem);
                    pendingRunnables.remove(taskItem);

                    if(pendingTaskRemoval != null){
                        handler.removeCallbacks(pendingTaskRemoval);
                    }
                    tasksPendingRemoval.remove(taskItem);
                    notifyChanges();
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Sorts the set of tasks by priority. First time used low priority tasks will show up first,
     * next time is used, high priority tasks will.
     */
    public void sortItemsByPriority(){

        if(!sortedByPriority){
            Collections.sort(taskList, Task.getPriorityComparator());
            sortedByPriority = true;
            sortedByDate = false;
        }else{
            Collections.reverse(taskList);
        }
        notifyChanges();

    }

    /**
     * Sorts the set of tasks by date. First time used, older tasks will show up first, next time is
     * used, newer tasks will.
     */
    public void sortItemsByDate(){

        if(!sortedByDate){
            Collections.sort(taskList, Task.getDateComparator());
            sortedByDate = true;
            sortedByPriority = false;
        }else{
            Collections.reverse(taskList);
        }

        notifyChanges();
    }

    /**
     * @param position Position of the desired task
     * @return The tasks in the specified position of the list
     */
    public Object getItem(int position) {
        return taskList.get(position);
    }

    /**
     * Adds a new task to the displayed set
     * @param task New task to be added
     */
    public void addTask(Task task){
        taskList.add(task);
        notifyItemInserted(taskList.size()-1);

        sortedByDate = false;
        sortedByPriority = false;

        notifyChanges();
    }

    /**
     * Sets a Task as  one pending removal
     * @param position The position of the task wanted to be removed
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

        notifyChanges();
    }

    /**
     * Removes a task from all the sets of tasks
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

        sortedByDate = false;
        sortedByPriority = false;
    }

    /**
     * @param position Position of the desired task to check
     * @return True if the task in the specified position is pending removal
     */
    public boolean isPendingRemoval(int position){
        return tasksPendingRemoval.contains(taskList.get(position));
    }

    /**
     * Auxiliar class that defines a TAsk ViewHolder for the RecyclerView
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

    private void notifyChanges(){

        ((MainActivity)context).setPendingTasks(taskList.size()-tasksPendingRemoval.size());

        super.notifyDataSetChanged();

    }
}
