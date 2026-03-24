package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File REF_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REF_DIR, "heads");
    public static final File INDEX_FILE = join(GITLET_DIR, "index");
    public static final File HEAD_FILE = join(GITLET_DIR, "head");
    public static final File MASTER_FILE = join(HEADS_DIR, "master");

    /* TODO: fill in the rest of this class. */
    public static void setup() {

        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REF_DIR.mkdir();
        HEADS_DIR.mkdir();
        try {
            INDEX_FILE.createNewFile();
            HEAD_FILE.createNewFile();
            MASTER_FILE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        makeInitialCommit();
    }

    /**
     * 判断是否init
     * @return true
     */
    public static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

    public static void checkInitialized() {
        if (! isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * 创建一个commit对象并保存
     * @param m
     */
    public static void makeCommit(String m) {

        String message = m;
        Date epoch = new Date();
        String date = getDate(epoch);
        String parent = getParentCommitHash();
        Map<String, String> snapshots = getSnapshots();

        Commit c = new Commit(message, date, parent, snapshots);
        c.saveCommit();


        Utils.writeObject(HEAD_FILE, MASTER_FILE);
        Utils.writeObject(MASTER_FILE, sha1(Utils.serialize(c)));
        StagingArea.clear();

    }

    //TODO: 初始commit
    public static void makeInitialCommit() {
        String message = "initial commit";
        String date = getDate(new Date(0));
        Map<String, String> snapshots = new TreeMap<>();

        Commit c = new Commit(message, date, null, snapshots);
        c.saveCommit();

        Utils.writeObject(HEAD_FILE, MASTER_FILE);
        Utils.writeObject(MASTER_FILE, sha1(Utils.serialize(c)));
    }


    /**
     * 创建commit对象时获取参数
     * @param date
     * @return
     */
    public static String getDate(Date date) {
        // EEE: 星期几缩写, MMM: 月份缩写, d: 日期, HH:mm:ss: 24小时制时间, yyyy: 年份, Z: 时区
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-0800"));   // 强制 UTC-8
        return sdf.format(date);
    }

    public static String getParentCommitHash() {
        File PATH = Utils.readObject(HEAD_FILE, File.class);
        String parentCommitHash = Utils.readObject(PATH, String.class);
        return parentCommitHash;
    }

    /**
     * 将上一个commit和新修改的内容合并起来，变成一个新的snapshot
     */
    public static Map<String, String> getSnapshots() {
        Map<String, String> stagShots = StagingArea.snapshot();
        // 父节点的commit
        Map<String, String> parentShots =getParentSnapshots();
        parentShots.putAll(stagShots);
        return parentShots;
    }

    public static Map<String, String> getParentSnapshots() {
        String parentHash = getParentCommitHash();
        Commit parentCommit = Commit.fromFile(parentHash);
        return parentCommit.getSnapshots();
    }


    public static void add(String filename) {
        StagingArea.addFile(filename);
    }
}
