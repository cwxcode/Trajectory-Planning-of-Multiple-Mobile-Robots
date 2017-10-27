package cz.agents.alite.communication.acquaintance;

import java.io.Serializable;

/**
 *
 * Abstract class for default Task. 
 * Contains content Object which defines the taks activity.
 *
 * @author Jiri Vokrinek
 */
public class AbstractTask implements Task, Serializable {

	private static final long serialVersionUID = -8640881245155276281L;

	private static final String TASK_TYPE_NAME = "AbstractTask";
    
    private Object content;
    private String taskID;
    
    /**
     *  Default constructor
     *
     * @param taskID unique task identiffication
     * @param content content of the task
     */
    public AbstractTask(String taskID, Object content) {
        this.content = content;
        this.taskID = taskID;
    }

    public String getTaskType() {
        return TASK_TYPE_NAME;
    }

    /**
     * Gives the name of the tak type of this class.
     * @return name
     */
    public static String getTaskTypeName() {
        return TASK_TYPE_NAME;
    }

    public boolean compliesType(String typeName) {
        if (TASK_TYPE_NAME.equals(typeName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the content of this task
     *
     * @return task content
     */
    public Object getContent() {
        return content;
    }

    @Override
    public String toString() {
        return getTaskType() + "("+taskID+"): " + ((content!=null)?content.toString():"");
    }

    public String getTaskID() {
        return taskID;
    }
}
