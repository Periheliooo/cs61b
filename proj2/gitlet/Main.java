package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if (Repository.isInitialized()) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                }
                Repository.setup();
                break;
            case "add":
                // 目前只考虑加一个文件
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                Repository.checkInitialized();
                String filename = args[1];
                Repository.add(filename);
                break;
            case "commit":
                Repository.checkInitialized();
                if (StagingArea.added().isEmpty() && StagingArea.removed().isEmpty()) {
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                } else if (args.length == 1 || args[1].equals("")) {
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
                }
                break;
            case "global-log":
                Repository.checkInitialized();
                if (args.length > 1) {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                } else {
                    Repository.globalLog();
                }
                break;
            case "find":
                Repository.checkInitialized();
                if (args.length == 1 || args.length > 2) {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                } else {
                    Repository.find(args[1]);
                }
                break;
            case "status":
                Repository.checkInitialized();
                if (args.length > 1) {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                } else {
                    Repository.status();
                }
                break;
            case "checkout":
                Repository.checkInitialized();
                if (args.length == 3 && args[1].equals("--")) {
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    Repository.checkoutFile(args[1], args[3]);
                } else if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                break;
            case "branch":
                Repository.checkInitialized();
                if(args.length == 2) {
                    Repository.branch(args[1]);
                } else {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                break;
            case "rm-branch":
                Repository.checkInitialized();
                if(args.length == 2) {
                    Repository.rmBranch(args[1]);
                } else {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                break;
            case "reset":
                Repository.checkInitialized();
                if (args.length == 2) {
                    Repository.reset(args[1]);
                } else {
                    System.out.println("Incorrect operands");
                    System.exit(0);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
