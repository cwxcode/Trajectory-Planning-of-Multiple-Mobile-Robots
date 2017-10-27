package cz.agents.alite.communication.content;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Externalizable content wrapper for the messaging.
 *
 * @author Michal Stolba
 */
public class ExternalizableContent extends Content implements Externalizable {

    private Object data;

    public ExternalizableContent() {
        super(null);
    }

    /**
     *
     * @param data the content data
     */
    public ExternalizableContent(Object data) {
        super(null);
        this.data = data;
    }

    /**
     * Returns the content data.
     *
     * @return the content data
     */
    public Object getData() {
        return data;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(data);
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        data = in.readObject();
    }

    public String toString() {
        if (data == null) {
            return "";
        } else {
            return data.toString();
        }
    }

}
