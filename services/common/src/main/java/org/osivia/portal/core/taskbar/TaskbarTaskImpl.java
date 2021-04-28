package org.osivia.portal.core.taskbar;

import org.apache.commons.beanutils.BeanUtils;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarTask;

/**
 * Taskbar task implementation.
 *
 * @author Cédric Krommenhoek
 * @see TaskbarItemImpl
 * @see TaskbarTask
 */
public class TaskbarTaskImpl extends TaskbarItemImpl implements TaskbarTask {

    /** Title. */
    private String title;
    /** CMS path. */
    private String path;
    /** Disabled indicator. */
    private boolean disabled;


    /**
     * Constructor.
     *
     * @param item taskbar item
     * @throws ReflectiveOperationException
     */
    public TaskbarTaskImpl(TaskbarItem item) {
        super();
        try {
            BeanUtils.copyProperties(this, item);
        } catch (Exception e) {
            // Do nothing
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return this.title;
    }


    /**
     * {@inheritDoc}
     */
    public String getPath() {
        return this.path;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDisabled() {
        return this.disabled;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TaskbarTaskImpl [id=");
        builder.append(this.getId());
        builder.append("]");
        return builder.toString();
    }


    /**
     * Setter for title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setter for path.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Setter for disabled.
     *
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
