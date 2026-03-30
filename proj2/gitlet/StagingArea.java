package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static gitlet.Utils.*;

public class StagingArea {
    private static StagingData d = readData();
    private static Map<String, String> added = d.saveAdded;
    private static LinkedList<String> removed = d.saveRemoved;

    public static void addFile(String fileName) {

        File file = Utils.join(Repository.CWD, fileName);

        // 文件不存在情况
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        byte[] contents = Utils.readContents(file);
        String newFileName = Utils.sha1(contents);
        String oldFileName = getOldFileName(fileName);

        // 改回原样时从暂存区删去
        if (newFileName.equals(oldFileName)) {
            added.remove(fileName);
            writeData();

        } else {
            File newPath = Utils.join(Repository.BLOBS_DIR, newFileName);
            Utils.writeContents(newPath, contents);
            added.put(fileName, newFileName);    // 现在的名字 -> 版本的名字
            writeData();

        }
    }

    // commit之后应该清楚缓存区
    public static void clear() {
        added = new TreeMap<>();
        removed = new LinkedList<>();
        writeData();
    }

    public static void rmFromStagingArea(String filename) {
        added.remove(filename);
        writeData();
    }

    public static void rmFromCommit(String filename) {
        removed.add(filename);
        writeData();
    }

    // 返回add的文件的对应关系
    public static Map<String, String> added() {
        return added;
    }

    public static LinkedList<String> removed() {
        return removed;
    }

    public static String getOldFileName(String fileName) {
        Map<String, String> parentShots = Repository.getParentSnapshots();
        return parentShots.getOrDefault(fileName, null);
    }

    public static void writeData() {
        StagingData d = new StagingData((TreeMap<String, String>) added, removed);
        Utils.writeObject(Repository.INDEX_FILE, d);
    }

    //从index中读取Map
    private static StagingData readData() {
        // 检查文件长度。如果为 0，说明还没存过东西
        if (!Repository.INDEX_FILE.exists() || Repository.INDEX_FILE.length() == 0) {
            return new StagingData(); // 返回空 Map 供后续逻辑使用
        }
        return Utils.readObject(Repository.INDEX_FILE, StagingData.class);
    }

    private static class StagingData implements Serializable {
        TreeMap<String, String> saveAdded;
        LinkedList<String> saveRemoved;

        public StagingData(TreeMap<String, String> a, LinkedList<String> r) {
            this.saveAdded = a;
            this.saveRemoved = r;
        }

        public StagingData() {
            this.saveAdded = new TreeMap<>();
            this.saveRemoved = new LinkedList<>();
        }
    }

    public static void checkoutBranch(Commit c) {
        Map<String, String> snapshots = c.snapshots();
        for (String s : added.keySet()) {
            if (!snapshots.containsKey(s)) {
                added.remove(s);
            }
        }
    }

    public static boolean checkAllTracked(String commitId) {
        boolean flag = true;
        Map<String, String> snapshots = Repository.getSnapshots();
        Commit c = readObject(join(Repository.COMMITS_DIR, commitId), Commit.class);
        for (String s : Objects.requireNonNull(plainFilenamesIn(Repository.CWD))) {
            if (!snapshots.containsKey(s) && c.snapshots().containsKey(s)) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
