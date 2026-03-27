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
    public static final File COMMITS_DIR = join(OBJECT_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECT_DIR, "blobs");

    /* TODO: fill in the rest of this class. */
    public static void setup() {

        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        REF_DIR.mkdir();
        HEADS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
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
            System.exit(0);
        }
    }

    /**
     * 创建一个commit对象并保存
     */
    public static void makeCommit(String m) {

        String message = m;
        Date epoch = new Date();
        String date = getDate(epoch);
        String parent = getParentCommitHash();
        Map<String, String> snapshots = getSnapshots();

        Commit c = new Commit(message, date, parent, snapshots);
        c.saveCommit();

        //TODO: 分支更改
        File curBranch = Utils.readObject(HEAD_FILE, File.class);
        // Utils.writeObject(HEAD_FILE, MASTER_FILE);    这边似乎不用修改，可能是branch操作修改
        Utils.writeObject(curBranch, sha1(Utils.serialize(c)));
        StagingArea.clear();

    }

    //初始commit
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
     */
    public static String getDate(Date date) {
        // EEE: 星期几缩写, MMM: 月份缩写, d: 日期, HH:mm:ss: 24小时制时间, yyyy: 年份, Z: 时区
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-0800"));   // 强制 UTC-8
        return sdf.format(date);
    }

    public static String getParentCommitHash() {
        File PATH = Utils.readObject(HEAD_FILE, File.class);
        return Utils.readObject(PATH, String.class);
    }

    /**
     * 将上一个commit和新修改的内容合并起来，变成一个新的snapshot
     */
    public static Map<String, String> getSnapshots() {
        Map<String, String> stagShots = StagingArea.added();
        // 父节点的commit
        Map<String, String> parentShots =getParentSnapshots();
        parentShots.putAll(stagShots);
        rmFile(parentShots);
        return parentShots;
    }

    public static void rmFile(Map<String, String> snapshots) {
        for (String i : StagingArea.removed()) {
            snapshots.remove(i);
        }
    }

    public static Map<String, String> getParentSnapshots() {
        String parentHash = getParentCommitHash();
        Commit parentCommit = Commit.fromFile(parentHash);
        return parentCommit.snapshots();
    }


    public static void add(String filename) {
        StagingArea.addFile(filename);
    }

    public static void log() {
        Commit c = getCurCommit();
        while (c != null) {
            printLog(c);
            c = c.getParent();
        }
    }

    public static void globalLog() {
        List<String> logList = plainFilenamesIn(COMMITS_DIR);
        for (String s : logList) {
            File path = join(COMMITS_DIR, s);
            Commit c = Utils.readObject(path, Commit.class);
            printLog(c);
        }
    }

    public static void printLog(Commit c) {
        System.out.println("===");
        System.out.println("commit " + sha1(serialize(c)));
        System.out.println("Date: " + c.date());
        System.out.println(c.message());
        System.out.println();
    }

    public static void find(String m) {
        List<String> logList = plainFilenamesIn(COMMITS_DIR);
        for (String s : logList) {
            File path = join(COMMITS_DIR, s);
            Commit c = Utils.readObject(path, Commit.class);
            if (c.message().equals(m)) {
                System.out.println(sha1(serialize(c)));
            }
        }
    }

    public static void status() {
        printBranches();
        printStagedFiles();
        printRemovedFiles();
        printModifications();
        printUntrackedFiles();
    }

    public static void printBranches() {
        //TODO： Branches
        System.out.println("=== Branches ===");
        File curBranch = readObject(HEAD_FILE, File.class);
        System.out.println();
    }

    public static void printStagedFiles() {
        System.out.println("=== Staged Files ===");
        Map<String, String> stagedFileList = StagingArea.added();
        for (String s : stagedFileList.keySet()) {
            System.out.println(s);
        }
        System.out.println();
    }

    public static void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        LinkedList<String> removedFileList = StagingArea.removed();
        for (String s : removedFileList) {
            System.out.println(s);
        }
        System.out.println();
    }

    public static void printModifications() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> allFiles = plainFilenamesIn(CWD);
        Map<String, String> snapshots = getSnapshots();
        Set<String> snapFiles = snapshots.keySet();
        for (String s : snapFiles) {
            if (!allFiles.contains(s)){
                System.out.println(s + " (deleted)");
            } else if (!snapshots.get(s).equals(sha1(readContents(join(CWD, s))))) {
                System.out.println(s + " (modified)");
            }
        }
        System.out.println();
    }

    public static void printUntrackedFiles() {
        System.out.println("=== Untracked Files ===");
        List<String> allFiles = plainFilenamesIn(CWD);
        Set<String> snapFiles = getSnapshots().keySet();
        for (String s : allFiles) {
            if (!snapFiles.contains(s)){
                System.out.println(s);
            }
        }
        System.out.println();
    }

    public static Commit getCurCommit() {
        File presentBranch = Utils.readObject(HEAD_FILE, File.class);
        String fileName = Utils.readObject(presentBranch, String.class);
        File filePath = Utils.join(OBJECT_DIR, fileName);
        return Utils.readObject(filePath, Commit.class);
    }

    public static void rm(String filename) {
        boolean flag = true;
        Commit curCommit = getCurCommit();
        if (curCommit.snapshots().containsKey(filename)) {
            File targetFile = join(CWD, filename);
            StagingArea.rmFromCommit(filename);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            flag = false;
        }
        if (StagingArea.added().containsKey(filename)) {
            StagingArea.rmFromStagingArea(filename);
            flag = false;
        }
        if (flag) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
}
