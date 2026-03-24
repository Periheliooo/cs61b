package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                if (Repository.isInitialized()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }
                Repository.setup();
                break;
            case "add":
                // TODO: handle the `add [filename]`
                // 目前只考虑加一个文件
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                Repository.checkInitialized();
                String filename = args[1];
                Repository.add(filename);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                Repository.checkInitialized();
                if (StagingArea.snapshot().isEmpty()) {
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                } else if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                } else if (args.length > 2) {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                } else {
                    Repository.makeCommit(args[1]);
                }
                break;
            case "log":
                Repository.checkInitialized();
                if (args.length > 1) {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                } else {
                    Repository.log();
                }
                break;
            case "rm":
                Repository.checkInitialized();
                if (args.length == 1 || args.length > 2) {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                } else {
                    Repository.rm(args[1]);
                    break;
                }
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
