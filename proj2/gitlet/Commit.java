package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.EmptyStackException;
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String date;   // TODO: 时间
    private String parent;   // TODO:
    private Map<String, String> snapshots = new TreeMap<>();

    public Commit(String message, String date, String parent, Map<String, String> snapshots) {
        this.message = message;
        this.date = date;
        this.parent = parent;
        this.snapshots = snapshots;
    }

    //TODO
    public static Commit fromFile(String filename) {
        File file = Utils.join(Repository.OBJECT_DIR, filename);
        Commit c = Utils.readObject(file, Commit.class);
        return c;
    }

    /**
     * 保存commit对象
     */
    public void saveCommit() {
        File commitFile = Utils.join(Repository.OBJECT_DIR, Utils.sha1(this));
        Utils.writeObject(commitFile, this);
    }
    /* TODO: fill in the rest of this class. */

    public Map<String, String> getSnapshots() {
        return this.snapshots;
    }
}
