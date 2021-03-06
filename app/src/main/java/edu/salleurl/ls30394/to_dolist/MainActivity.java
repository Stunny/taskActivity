package edu.salleurl.ls30394.to_dolist;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.sql.Date;
import java.util.Calendar;

import edu.salleurl.ls30394.to_dolist.adapters.TaskAdapter;
import edu.salleurl.ls30394.to_dolist.model.Task;

import static edu.salleurl.ls30394.to_dolist.R.attr.layoutManager;

public class MainActivity extends AppCompatActivity {

    /**
     * How many tasks are not yet accomplished, or, in other words, deleted.
     */
    private int pendingTasks;

    /**
     * Where all tasks are displayed as a list
     */
    private RecyclerView recyclerView;

    /**
     * Custom Recycler View adapter implemented for the display of tasks
     */
    private TaskAdapter taskAdapter;


    private Spinner addTaskPrioritySpn;

    private TextInputEditText addTaskHintInput;

    private LinearLayout addTaskView;

    private FloatingActionButton fab;

    //--------------------------------------------------------------------------------------------//
    //---------------------------------OVERRIDE METHODS-------------------------------------------//
    @Override
    /**
     * Main functionality for the Main activity. All procedures that must be done when the activity
     * is created are implemented here
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

        pendingTasks = taskAdapter.getItemCount();
        Snackbar.make(findViewById(R.id.activity_main), R.string.swipe, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    /**
     * Function that initializes the action bar menu when the activity is created
     */
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    /**
     * Controls which of the action bar menu options has been selected, and executes the
     * corresponding functionality
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.main_menu_sortPriority:
                OnSortTasksByPriority();
                return true;

            case R.id.main_menu_sortDate:
                OnSortTasksByDate();
                return true;
        }

        return true;
    }

    //--------------------------------------------------------------------------------------------//
    //-----------------------METODOS DE INIICIALIZACION GRAFICA Y LOGICA--------------------------//

    /**
     * Assigns every layout widget to its Activity attribute and then configures it.
     */
    private void initWidgets() {
        initRecycler();
        initActionBar();
        initAddTaskPanel();

        fab = (FloatingActionButton)findViewById(R.id.fab);
    }

    /**
     * Initializes and configures the activity's action bar
     */
    private void initActionBar() {

        Toolbar actionBar;

        if(getSupportActionBar() == null) {
            actionBar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(actionBar);
        }
        getSupportActionBar().setTitle(String.format(getString(R.string.pending_tasks), pendingTasks));
    }

    /**
     * initializes and configures the "Add new task" drawer
     */
    private void initAddTaskPanel(){
        addTaskView = (LinearLayout)findViewById(R.id.addTaskPanel);
        addTaskView.setVisibility(View.GONE);
        addTaskPrioritySpn = (Spinner)findViewById(R.id.prioritySpinner);
        addTaskHintInput = (TextInputEditText)findViewById(R.id.taskHintInput);
    }

    /**
     * Initializes and configures the recycler view that displays the Task List
     */
    private void initRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        taskAdapter = new TaskAdapter(this, recyclerView);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        hardCodeExamples();

        initRecyclerTouchHelper();
        initRecyclerAnimationDecoratorHelper();
    }

    //-------------------------INICIALIZACION GRAFICA DE LA RECYCLER VIEW-------------------------//
    /**
     * Initializes Recycler view animations
     */
    private void initRecyclerAnimationDecoratorHelper() {
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.LTGRAY);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                if (parent.getItemAnimator().isRunning()) {

                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    int left = 0;
                    int right = parent.getWidth();

                    int top = 0;
                    int bottom = 0;

                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {

                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {

                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {

                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();

                    } else if (lastViewComingDown != null) {

                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();

                    } else if (firstViewComingUp != null) {

                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();

                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }
        });
    }

    /**
     * Initializes the Recycler view touch helper
     */
    private void initRecyclerTouchHelper() {

        ItemTouchHelper.SimpleCallback itemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ) {

                    Drawable background;
                    Drawable deleteMark;
                    int deleteMarkMargin;
                    boolean initiated;

                    private void init() {
                        background = new ColorDrawable(Color.LTGRAY);
                        deleteMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_deleting);
                        deleteMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                        deleteMarkMargin = (int) MainActivity.this.getResources()
                                .getDimension(R.dimen.activity_horizontal_margin);
                        initiated = true;
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int swipedPosition = viewHolder.getAdapterPosition();
                        TaskAdapter adapter = (TaskAdapter) recyclerView.getAdapter();

                        adapter.setPendingRemoval(swipedPosition);
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;

                        // not sure why, but this method get's called for viewholder that are already swiped away
                        if (viewHolder.getAdapterPosition() == -1) {
                            // not interested in those
                            return;
                        }

                        if (!initiated) {
                            init();
                        }

                        // draw red background
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(c);

                        // draw x mark
                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = deleteMark.getIntrinsicWidth();
                        int intrinsicHeight = deleteMark.getIntrinsicWidth();

                        int xMarkLeft = itemView.getRight() - deleteMarkMargin - intrinsicWidth;
                        int xMarkRight = itemView.getRight() - deleteMarkMargin;
                        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        int xMarkBottom = xMarkTop + intrinsicHeight;
                        deleteMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                        deleteMark.draw(c);

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }


                };

        ItemTouchHelper ith = new ItemTouchHelper(itemTouchCallback);
        ith.attachToRecyclerView(recyclerView);
    }
    //--------------------------------------------------------------------------------------------//

    /**
     * Initializes some hardcoded task examples to show the current functioning of the activity
     */
    private void hardCodeExamples() {


        taskAdapter.addTask(
                new Task(Task.TASK_PRIORITY_HI, "Hoigh 1", new Date(System.currentTimeMillis())));

        taskAdapter.addTask(
                new Task(Task.TASK_PRIORITY_NORMAL, "Normal 1", new Date(System.currentTimeMillis()))
        );

        taskAdapter.addTask(
                new Task(Task.TASK_PRIORITY_LO, "Low1", new Date(System.currentTimeMillis()))
        );

        taskAdapter.addTask(
                new Task(Task.TASK_PRIORITY_HI, "High2", new Date(System.currentTimeMillis())));

        taskAdapter.addTask(
                new Task(Task.TASK_PRIORITY_NORMAL, "Normal 2", new Date(System.currentTimeMillis()-1000))
        );

        taskAdapter.addTask(
                new Task(Task.TASK_PRIORITY_LO, "Low2", new Date(System.currentTimeMillis()-5000))
        );

        taskAdapter.addTask(
                new Task(Task.TASK_PRIORITY_HI, "High3", new Date(System.currentTimeMillis()-10000)));

    }

    //--------------------------------------------------------------------------------------------//
    //------------------------------LOGICA PRINCIPAL DE LA ACTIVIDAD------------------------------//

    /**
     * Tells the task adapter to sort the task list by their addition date
     */
    private void OnSortTasksByDate() {
        taskAdapter.sortItemsByDate();
    }

    /**
     * Tells the task adapter to sort the task list by their priority
     */
    private void OnSortTasksByPriority() {
        taskAdapter.sortItemsByPriority();
    }

    /**
     * Sets the number of pending pending tasks are saved.
     * @param pendingTasks Number of pending tasks to display
     */
    public void setPendingTasks(int pendingTasks){
        this.pendingTasks = pendingTasks;

        initActionBar();
    }

    /**
     * Añade la nueva tarea al adaptador y esconde el panel, mostrando el FAB
     * @param view Boton de añadir tarea a la lista
     */
    public void onAddTask(View view) {
        Task t = new Task();

        switch((String)addTaskPrioritySpn.getSelectedItem()){
            case "ASAP":
                t.setPriority(Task.TASK_PRIORITY_ASAP);
                break;
            case "HIGH":
                t.setPriority(Task.TASK_PRIORITY_HI);
                break;
            case "NORMAL":
                t.setPriority(Task.TASK_PRIORITY_NORMAL);
                break;
            case "LOW":
                t.setPriority(Task.TASK_PRIORITY_LO);
        }

        t.setDescription(addTaskHintInput.getText().toString());
        addTaskHintInput.setText("");
        t.setDateOfCreation(new Date(System.currentTimeMillis()));

        taskAdapter.addTask(t);
        taskAdapter.notifyChanges();
        recyclerView.requestLayout();

        addTaskView.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
    }

    /**
     * Abre el panel de añadir tareas a la lista y esconde el fab
     * @param view FAB
     */
    public void onFABclick(View view) {

        addTaskView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

    }
}

//------------------------------------------------------------------------------------------------//
//------------------------------------------------------------------------------------------------//