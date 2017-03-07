package edu.salleurl.ls30394.to_dolist.model;

import java.sql.Date;

/**
 * Created by avoge on 07/03/2017.
 */

public class Task {

    public static final int TASK_PRIORITY_LO = 0;

    public static final int TASK_PRIORITY_NORMAL = 1;

    public static final int TASK_PRIORITY_HI = 2;

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
}
