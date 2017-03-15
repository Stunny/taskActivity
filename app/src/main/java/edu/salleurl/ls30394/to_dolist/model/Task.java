package edu.salleurl.ls30394.to_dolist.model;

import android.annotation.TargetApi;
import android.os.Build;

import java.sql.Date;
import java.util.Comparator;

/**
 * Created by avoge on 07/03/2017.
 */

public class Task {

    public static final int TASK_PRIORITY_LO = 0;

    public static final int TASK_PRIORITY_NORMAL = 1;

    public static final int TASK_PRIORITY_HI = 2;

    public static final int TASK_PRIORITY_ASAP = 3;

    private int priority;

    private String description;

    private Date dateOfCreation;

    public Task(){}

    /**
     *
     * @param priority
     * @param description
     * @param dateOfCreation
     */
    public Task(int priority, String description, Date dateOfCreation) {
        this.priority = priority;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
    }

    public int getPriority() {
        return priority;
    }

    /**
     *
     * @param priority
     */
    public void setPriority(int priority) {

        if(priority <  0|| priority > 2){
            return;
        }

        this.priority = priority;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     *
     * @param dateOfCreation
     */
    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }


    /**
     * @return * @return A new comparator that sorts a list of Tasks by their date of creation
     */
    public static Comparator<Task> getDateComparator(){

        return new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                long date1 = o1.getDateOfCreation().getTime();
                long date2 = o2.getDateOfCreation().getTime();

                return (int) ((int) date2 - date1);

            }
        };

    }

    /**
     * @return A new comparator that sorts a list of Tasks by their priority
     */
    public static Comparator<Task> getPriorityComparator(){
        return new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                int priority1 = o1.getPriority();
                int priority2 = o2.getPriority();

                return priority2-priority1;
            }
        };
    }
}
