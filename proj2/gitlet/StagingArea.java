package gitlet;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class StagingArea {
    private static Map<String, String> added = new TreeMap<>();

    public static void addFile(String fileName) {
        File file = Utils.join(Repository.CWD, fileName);
        byte[] contents = Utils.readContents(file);
        String newFileName = Utils.sha1(contents);
        File newPath = Utils.join(Repository.OBJECT_DIR, newFileName);
        Utils.writeContents(newPath, contents);
        added.put(fileName, newFileName);    // 现在的名字 -> 版本的名字
    }

    /**
     * commit之后应该清楚缓存区
     */
    public static void clear() {
        added = new TreeMap<>();
    }

    /**
     * 返回add的文件的对应关系
     * @return
     */
    public static Map<String, String> snapshot() {
        return added;
    }

}
