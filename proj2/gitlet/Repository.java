package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 */
public class Repository {
    /**
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
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * 创建一个commit对象并保存
     */
    public static void makeCommit(String m) {
        Date date = new Date();
        String parent = getParentCommitHash();
        Map<String, String> snapshots = getSnapshots();

        Commit c = new Commit(m, date, parent, snapshots);
        c.saveCommit();

        File curBranch = Utils.readObject(HEAD_FILE, File.class);
        Utils.writeObject(curBranch, sha1(Utils.serialize(c)));
        StagingArea.clear();
    }

    //初始commit
    public static void makeInitialCommit() {
        String message = "initial commit";
        Date date = new Date(0);
        Map<String, String> snapshots = new TreeMap<>();

        Commit c = new Commit(message, date, null, snapshots);
        c.saveCommit();

        Utils.writeObject(HEAD_FILE, MASTER_FILE);
        Utils.writeObject(MASTER_FILE, sha1(Utils.serialize(c)));
    }


    /**
     * 创建commit对象时获取参数
     */
    public static String visualizeDate(Date date) {
        // EEE: 星期几缩写, MMM: 月份缩写, d: 日期, HH:mm:ss: 24小时制时间, yyyy: 年份, Z: 时区
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-0800"));   // 强制 UTC-8
        return sdf.format(date);
    }

    public static String getParentCommitHash() {
        File path = Utils.readObject(HEAD_FILE, File.class);
        return Utils.readObject(path, String.class);
    }

    /**
     * 将上一个commit和新修改的内容合并起来，变成一个新的snapshot
     */
    public static Map<String, String> getSnapshots() {
        Map<String, String> stagShots = StagingArea.added();
        // 父节点的commit
        Map<String, String> parentShots = new TreeMap<>(getParentSnapshots());
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
        System.out.println("Date: " + visualizeDate(c.date()));
        System.out.println(c.message());
        System.out.println();
    }

    public static void find(String m) {
        List<String> logList = plainFilenamesIn(COMMITS_DIR);
        boolean flag = false;
        for (String s : logList) {
            File path = join(COMMITS_DIR, s);
            Commit c = Utils.readObject(path, Commit.class);
            if (c.message().equals(m)) {
                System.out.println(sha1(serialize(c)));
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
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
        System.out.println("=== Branches ===");
        File curBranch = readObject(HEAD_FILE, File.class);
        List<String> allFile = plainFilenamesIn(HEADS_DIR);
        Collections.sort(allFile);
        for (String s : allFile) {
            if (s.equals(curBranch.getName())) {
                System.out.println("*" + s);
            } else {
                System.out.println(s);
            }
        }
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
        Collections.sort(removedFileList);
        for (String s : removedFileList) {
            System.out.println(s);
        }
        System.out.println();
    }

    public static void printModifications() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> curFiles = plainFilenamesIn(CWD);
        Map<String, String> snapshots = getSnapshots();
        Set<String> snapFiles = snapshots.keySet();
        for (String s : snapFiles) {
            if (!curFiles.contains(s)){
                System.out.println(s + " (deleted)");
            } else if (!snapshots.get(s).equals(sha1(readContents(join(CWD, s))))) {
                System.out.println(s + " (modified)");
            }
        }
        System.out.println();
    }

    public static void printUntrackedFiles() {
        System.out.println("=== Untracked Files ===");
        List<String> curFiles = plainFilenamesIn(CWD);
        Set<String> snapFiles = getSnapshots().keySet();
        for (String s : curFiles) {
            if (!snapFiles.contains(s)){
                System.out.println(s);
            }
        }
        System.out.println();
    }

    public static Commit getCurCommit() {
        File presentBranch = Utils.readObject(HEAD_FILE, File.class);
        String fileName = Utils.readObject(presentBranch, String.class);
        File filePath = Utils.join(COMMITS_DIR, fileName);
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

    public static void checkoutFile(String fileName){
        File f = readObject(HEAD_FILE, File.class);
        String commitId = readObject(f, String.class);
        checkoutFile(commitId, fileName);
    }

    public static void checkoutFile(String commitId, String fileName) {
        commitId = expandShortId(commitId);
        File f = join(COMMITS_DIR, commitId);
        if (!f.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            Commit c = readObject(f, Commit.class);
            Map<String, String> snapshots = c.snapshots();
            if (snapshots.containsKey(fileName)) {
                String fileId = snapshots.get(fileName);
                byte[] content = readContents(join(BLOBS_DIR, fileId));
                writeContents(join(CWD, fileName), content);
            } else {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
        }
    }

    public static void checkoutBranch(String branchName) {
        File f = join(HEADS_DIR, branchName);
        if (!f.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (f.equals(readObject(HEAD_FILE, File.class))) {
            System.out.println("No need to checkout the current branch.");
        } else if (!StagingArea.checkAllTracked(readObject(f, String.class))) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        } else {
            String commitId = readObject(f, String.class);
            Commit c = readObject(join(COMMITS_DIR, commitId), Commit.class);
            checkoutCommit(c);
            writeObject(HEAD_FILE, join(HEADS_DIR, branchName));
        }
    }

    public static void checkoutCommit(Commit c) {
        Map<String, String> snapshots = c.snapshots();
        for (String s : snapshots.keySet()) {
            byte[] content = readContents(join(BLOBS_DIR, snapshots.get(s)));
            writeContents(join(CWD, s), content);
        }
        for (String s : getSnapshots().keySet()) {
            if (!snapshots.keySet().contains(s)) {
                join(CWD, s).delete();
            }
        }
        StagingArea.clear();
    }

    public static String expandShortId(String commitId) {
        if (commitId.length() < 40) {
            List<String> allCommits = plainFilenamesIn(COMMITS_DIR);
            for (String id : allCommits) {
                if (id.startsWith(commitId)) {
                    commitId = id;
                    break;
                }
            }
        }
        return commitId;
    }

    public static void branch(String branchName) {
        File newBranch = join(HEADS_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File curBranch = readObject(HEAD_FILE, File.class);
        String headHash = readObject(curBranch, String.class);
        writeObject(newBranch, headHash);
    }

    public static void rmBranch(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (branch.exists()) {
            if (readObject(HEAD_FILE, File.class).getName().equals(branchName)) {
                System.out.println("Cannot remove the current branch.");
                System.exit(0);
            } else {
                join(HEADS_DIR, branchName).delete();
            }
        } else {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    public static void reset(String commitId) {
        commitId = expandShortId(commitId);
        File f = join(COMMITS_DIR, commitId);
        if (!f.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else if (!StagingArea.checkAllTracked(commitId)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        } else {
            Commit c = readObject(f, Commit.class);
            checkoutCommit(c);
            File curBranch = readObject(HEAD_FILE, File.class);
            writeObject(curBranch, commitId);
        }
    }

    public static void merge(String branchName) {
        if (!StagingArea.added().isEmpty() || !StagingArea.removed().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        boolean flag = true;    // 无冲突为true
        File f = join(HEADS_DIR, branchName);
        if (!f.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        File curBranchFile = readObject(HEAD_FILE, File.class);
        if (branchName.equals(curBranchFile.getName())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        String commitId = readObject(f, String.class);
        Commit targetCommit = readObject(join(COMMITS_DIR, commitId), Commit.class);
        Commit curCommit = getCurCommit();
        Commit splitCommit = getSplitCommit(targetCommit, curCommit);

        if (!StagingArea.checkAllTracked(commitId)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        String splitCommitHash = sha1(serialize(splitCommit));
        String targetCommitHash = sha1(serialize(targetCommit));
        String curCommitHash = sha1(serialize(curCommit));

        if (splitCommitHash.equals(targetCommitHash)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        if (splitCommitHash.equals(curCommitHash)) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        Map<String, String> curSnapshots = curCommit.snapshots();
        Map<String, String> targetSnapshots = targetCommit.snapshots();
        Map<String, String> splitSnapshots = splitCommit.snapshots();
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitSnapshots.keySet());
        allFiles.addAll(curSnapshots.keySet());
        allFiles.addAll(targetSnapshots.keySet());

        for (String s : allFiles) {
            String splitHash = splitSnapshots.get(s);
            String curHash = curSnapshots.get(s);
            String givenHash = targetSnapshots.get(s);

            // 第一层大分叉：Split 里有没有？
            if (splitHash == null) {
                // Split 无
                if (curHash == null && givenHash != null) {
                    checkoutFile(commitId, s);
                    add(s);
                } else if (curHash != null && givenHash == null) {
                    // 无
                } else if (curHash != null && givenHash != null) {
                    if (curHash.equals(givenHash)) {
                        // 无
                    } else {
                        dealConflict(s, targetSnapshots, curSnapshots);
                        flag = false;
                    }
                }
            } else {
                // Split 有
                if (curHash == null && givenHash != null) {
                    if (givenHash.equals(splitHash)) {
                        // 无
                    } else {
                        dealConflict(s, targetSnapshots, curSnapshots);
                        flag = false;
                    }
                } else if (curHash != null && givenHash == null) {
                    if (curHash.equals(splitHash)) {
                        rm(s);
                    } else {
                        dealConflict(s, targetSnapshots, curSnapshots);
                        flag = false;
                    }
                } else if (curHash == null && givenHash == null) {
                    // 无
                } else {
                    // 有有：根据改/未改进行判断
                    boolean curModified = !curHash.equals(splitHash);
                    boolean givenModified = !givenHash.equals(splitHash);

                    if (!curModified && !givenModified) {
                        // 无
                    } else if (!curModified && givenModified) {
                        checkoutFile(commitId, s);
                        add(s);
                    } else if (curModified && !givenModified) {
                        // 无
                    } else { // 有改, 有改
                        if (curHash.equals(givenHash)) {
                            // 无
                        } else {
                            dealConflict(s, targetSnapshots, curSnapshots);
                            flag = false;
                        }
                    }
                }
            }
        }

        String curBranchName = Utils.readObject(HEAD_FILE, File.class).getName();
        String msg = "Merged " + branchName + " into " + curBranchName + ".";
        makeCommit(msg);

        if (!flag) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static Commit getSplitCommit(Commit targetCommit, Commit curCommit) {
        Commit a = targetCommit;
        Commit b = curCommit;
        HashSet<String> curAncestors = new HashSet<>();
        while (a != null) {
            curAncestors.add(sha1(serialize(a)));
            a = a.getParent();
        }
        while (b != null) {
            if (curAncestors.contains(sha1(serialize(b)))) {
                break;
            }
            b = b.getParent();
        }
        return b;
    }

    public static void dealConflict(String fileName, Map<String, String> targetSnapshots, Map<String, String> curSnapshots) {
        File file = join(CWD, fileName);
        String curHash = curSnapshots.get(fileName);
        String givenHash = targetSnapshots.get(fileName);

        String curContent = "";
        if (curHash != null) {
            curContent = readContentsAsString(join(BLOBS_DIR, curHash));
        }

        String givenContent = "";
        if (givenHash != null) {
            givenContent = readContentsAsString(join(BLOBS_DIR, givenHash));
        }

        String conflictContent = "<<<<<<< HEAD\n" +
                curContent +
                "=======\n" +
                givenContent +
                ">>>>>>>\n";

        writeContents(file, conflictContent);
        add(fileName);
    }
}
