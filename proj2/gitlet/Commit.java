package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  does at a high level.
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private static final long serialVersionUID = 1L;
    private String message;
    private String date;
    private String parent;
    private Map<String, String> snapshots = new TreeMap<>();

    public Commit(String message, String date, String parent, Map<String, String> snapshots) {
        this.message = message;
        this.date = date;
        this.parent = parent;
        this.snapshots = snapshots;
    }

    public static Commit fromFile(String filename) {
        File file = Utils.join(Repository.COMMITS_DIR, filename);
        Commit c = Utils.readObject(file, Commit.class);
        return c;
    }

    /**
     * 保存commit对象
     */
    public void saveCommit() {
        File commitFile = Utils.join(Repository.COMMITS_DIR, Utils.sha1(Utils.serialize(this)));
        Utils.writeObject(commitFile, this);
    }

    public Map<String, String> snapshots() {
        return this.snapshots;
    }

    public String date() {
        return date;
    }

    public String message() {
        return message;
    }

    public Commit getParent() {
        if (parent == null) {
            return null;
        }
        File parentFile = Utils.join(Repository.COMMITS_DIR, parent);
        return Utils.readObject(parentFile, Commit.class);
    }
}
