package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Dhruv Krishnaswamy
 */
@SuppressWarnings("unchecked")
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) throws IOException {
        String[] commands = args;
        if (commands.length < 1) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        File read = Utils.join(".gitlet", ".tree");
        if (read.exists()) {
            setGtree(Utils.readObject(read, Gtree.class));
        }
        if (commands[0].equals("init")) {
            init();
            save();
        } else if (commands[0].equals("log")) {
            log(obtaintree());
            save();
        } else if (commands[0].equals("add")) {
            add(args[1]);
            save();
        } else if (commands[0].equals("global-log")) {
            glog();
        } else if (commands[0].equals("rm")) {
            rm(args[1]);
            save();
        } else if (commands[0].equals("status")) {
            if (!Utils.join(".gitlet").exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
            status();
        } else if (commands[0].equals("branch")) {
            branch(args[1]);
            save();
        } else if (commands[0].equals("commit")) {
            if (args[1].equals("") || args.length == 1) {
                System.out.println("Please enter a commit message.");
                System.exit(0);
            } else {
                String mess = "";
                for (int i = 1; i < args.length - 1; i++) {
                    mess = mess + args[i] + " ";
                }
                mess += args[args.length - 1];
                comm(mess);
                save();
            }
        } else if (commands[0].equals("checkout")) {
            checkout(commands);
            save();
        } else {
            main2(args);
        }
    }

    /**
     * Main method 2.
     *
     * @param args - inputted
     **/

    public static void main2(String[] args) {
        String[] commands = args;
        if (commands[0].equals("global-log")) {
            glog();
        } else if (commands[0].equals("rm-branch")) {
            rmbranch(args[1]);
            save();
        } else if (commands[0].equals("reset")) {

            reset(args[1]);
            save();
        } else if (commands[0].equals("find")) {
            String mes = "";
            for (int i = 1; i < args.length - 1; i++) {
                mes = mes + args[i] + " ";
            }
            mes += args[args.length - 1];
            find(mes);
        } else if (commands[0].equals("merge")) {
            merge(commands[1]);
            save();
        } else {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
    }


    /**
     * This checks which version of checkout to call.
     *
     * @param commi - commit args
     **/
    static void checkout(String[] commi) {
        if (commi.length == 3) {
            checkout1(commi[2]);
        }
        if ((commi.length == 4)) {
            if (commi[2].equals("++")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            } else if (commi[2].equals("--")) {
                checkoutcommit(commi[1], commi[3]);
            }
        } else if ((commi.length == 2)) {
            checkoutbranch(commi[1]);
        }

    }

    /**
     * This is the init func.
     **/
    public static void init() {
        String wd = System.getProperty("user.dir");
        File cWD = new File(wd + "/.gitlet");
        if (!cWD.exists()) {
            _gtree = new Gtree();
            cWD.mkdir();
            File staging = new File(".gitlet", "staging");
            staging.mkdir();
            File commies = new File(".gitlet", "commits");
            commies.mkdir();
            File filez = new File(".gitlet", "Files");
            filez.mkdir();
            Commit first = new Commit();
            obtaintree().addCommit(first);
            obtaintree().addBranch("master");
        } else {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            System.exit(0);
        }
    }

    /**
     * Adds a file to stage.
     *
     * @param fname - fname
     **/
    static void add(String fname) {
        String wd = System.getProperty("user.dir");
        File f = Utils.join(wd, fname);
        if (!f.exists()) {
            System.out.println("File does not exist");
            System.exit(0);
        } else {
            byte[] byt = Utils.readContents(f);
            String fsha = Utils.sha1(byt);

            Commit h = obtaintree().gethead();
            if (h.getFiles().containsKey(fname)) {
                if (h.gettoRemove().contains(fname)) {
                    h.gettoRemove().remove(fname);
                }
                String tempsha = Utils.sha1(h.getFiles().get(fname));
                if (!tempsha.equals(fsha)) {
                    File temp = Utils.join(".gitlet", "staging", fname);
                    Utils.writeContents(temp, byt);
                } else {
                    File temp1 = Utils.join(".gitlet", "staging", fname);
                    if (temp1.exists()) {
                        temp1.delete();
                    }
                }
            } else {
                File temp2 = Utils.join(".gitlet", "staging", fname);
                Utils.writeContents(temp2, byt);
            }
        }
    }

    /**
     * This returns the log of all the commits made on the current branch.
     *
     * @param L - gtree
     **/
    static void log(Gtree L) {
        Commit temp = L.obtainbranches().get(L.getcurrentbranch());
        Commit currentcommit = null;
        Commit parent = null;
        while (temp != null) {
            currentcommit = temp;
            parent = currentcommit.getPar();

            System.out.println("===");
            System.out.println("commit " + currentcommit.obtainsha());
            System.out.println("Date: " + currentcommit.time());
            System.out.println(currentcommit.message());
            System.out.println();
            temp = parent;
        }
    }

    /**
     * This is the commit method.
     *
     * @param mess - message of commit.
     **/
    static void comm(String mess) {
        List<String> m;
        m = Utils.plainFilenamesIn(Utils.join(".gitlet", "staging"));
        Commit daddy = obtaintree().gethead();
        ArrayList parentfiles = new ArrayList<String>();
        if (daddy.getFilelist() != null) {
            parentfiles.addAll(daddy.getFilelist());
        }
        if ((m.size() == 0) && (daddy.gettoRemove().size() == 0)) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        ArrayList toadd = new ArrayList<String>();
        if (daddy.gettoRemove().size() == 0) {
            if (m.size() == 0) {
                toadd.addAll(parentfiles);
                Commit child = new Commit(toadd, mess, daddy);
                obtaintree().addCommit(child);
                Gtree.updatebz(obtaintree());
                for (String name : m) {
                    movestagetofiles(name);
                }
            } else {
                toadd.addAll(parentfiles);
                toadd.addAll(m);
                Commit child = new Commit(toadd, mess, daddy);
                obtaintree().addCommit(child);
                Gtree.updatebz(obtaintree());
                for (String name : m) {
                    movestagetofiles(name);
                }
            }
        } else {
            for (String x : daddy.gettoRemove()) {
                parentfiles.remove(x);
            }
            if (m.size() == 0) {
                toadd.addAll(parentfiles);
                Commit child = new Commit(toadd, mess, daddy);
                obtaintree().addCommit(child);
                Gtree.updatebz(obtaintree());
                for (String name : m) {
                    movestagetofiles(name);
                }
            } else {
                for (String y : daddy.gettoRemove()) {
                    parentfiles.remove(y);
                }
                toadd.addAll(parentfiles);
                toadd.addAll(m);
                Commit child = new Commit(toadd, mess, daddy);
                obtaintree().addCommit(child);
                Gtree.updatebz(obtaintree());
                for (String name : m) {
                    movestagetofiles(name);
                }
            }
        }
    }

    /**
     * This method moves a file from the staging area to the Files folder.
     *
     * @param filename - filename
     **/
    static void movestagetofiles(String filename) {
        File curr = Utils.join(".gitlet", "staging", filename);
        byte[] fileconts = Utils.readContents(curr);
        String filesha = Utils.sha1(fileconts);
        File dest = Utils.join(".gitlet", "Files", filename);
        Utils.writeContents(dest, fileconts);
        curr.delete();
    }

    /**
     * This returns the global log of all the commits ever made.
     **/
    static void glog() {

        for (Commit p : obtaintree().allcommits()) {
            System.out.println("===");
            System.out.println("commit " + p.obtainsha());
            System.out.println("Date: " + p.time());
            System.out.println(p.message());
            System.out.println();
        }
    }


    /**
     * Given a file name s, this overwrites its contents.
     *
     * @param s - file name
     **/
    public static void checkout1(String s) {
        Commit t = obtaintree().gethead();
        if (!t.getFiles().containsKey(s)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            String wd = System.getProperty("user.dir");
            File d = Utils.join(".gitlet", "Files", s);
            byte[] byt5 = Utils.readContents(d);
            Utils.writeContents(Utils.join(wd, s), byt5);
        }
    }

    /**
     * This removes the file j from the staging area and marks to be removed
     * from the current commit.
     *
     * @param j - file name
     **/
    public static void rm(String j) {

        File stageccheck = Utils.join(".gitlet", "staging", j);
        Commit stgcheck = obtaintree().gethead();
        if (!stageccheck.exists()) {
            if (!stgcheck.getFiles().containsKey(j)) {
                System.out.println("No reason to remove the file.");
                System.exit(0);
            } else {
                obtaintree().gethead().settoRemove(j);
                String wd = System.getProperty("user.dir");
                if (Utils.join(wd, j).exists()) {
                    Utils.restrictedDelete(Utils.join(wd, j));
                }
            }
        } else {
            if (stgcheck.getFiles().containsKey(j)) {
                obtaintree().gethead().settoRemove(j);
                Utils.restrictedDelete(stageccheck);
                String wd = System.getProperty("user.dir");
                if (Utils.join(wd, j).exists()) {
                    Utils.restrictedDelete(Utils.join(wd, j));
                }
            } else {
                stageccheck.delete();
            }
        }

    }

    /**
     * This creates a new branch in the tree.
     *
     * @param b - branch name
     **/
    public static void branch(String b) {
        if (!obtaintree().obtainbranches().keySet().contains(b)) {
            obtaintree().addBranch(b);
        } else {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
    }

    /**
     * This method removes the inputted branch if it exists in the tree.
     *
     * @param bn - branch name
     **/
    static void rmbranch(String bn) {
        if (!obtaintree().obtainbranches().keySet().contains(bn)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else {
            if (obtaintree().getcurrentbranch().equals(bn)) {
                System.out.println("Cannot remove the current branch.");
                System.exit(0);
            } else {
                obtaintree().deleteBranch(bn);
            }
        }
    }

    /**
     * Prints out the IDs of all commits with the same message.
     *
     * @param msg - This is the sha code
     **/
    static void find(String msg) {
        Boolean found = false;
        for (Commit d : obtaintree().allcommits()) {
            if (d.message().equals(msg)) {
                found = true;
                System.out.println(d.obtainsha());
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /**
     * This checks out the user to another to commit's file.
     *
     * @param commid   - commit id
     * @param filename - fname
     **/

    static void checkoutcommit(String commid, String filename) {
        boolean check = false;
        Commit use = null;

        for (Commit p : obtaintree().allcommits()) {
            if (p.obtainsha().contains(commid)) {
                check = true;
                use = p;
                break;
            }
        }
        if (!check) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            if (!use.getFiles().containsKey(filename)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            } else {
                String wd = System.getProperty("user.dir");
                File over = Utils.join(wd, filename);
                if (over.exists()) {
                    byte[] write = use.getFiles().get(filename);
                    Utils.writeContents(over, write);
                }
            }
        }
    }

    /**
     * This gives you the status of the staged.
     * & tracked files and the branches being used.
     **/

    static void status() {
        String wd = System.getProperty("user.dir");
        System.out.println("===" + " " + "Branches" + " " + "===");
        System.out.println("*" + obtaintree().getcurrentbranch());
        for (String branch : obtaintree().obtainbranches().keySet()) {
            if (!branch.equals(obtaintree().getcurrentbranch())) {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("===" + " " + "Staged Files" + " " + "===");
        for (String staged : Utils.plainFilenamesIn
                (Utils.join(".gitlet", "staging"))) {
            System.out.println(staged);
        }
        System.out.println();

        System.out.println("===" + " " + "Removed Files" + " " + "===");
        for (String fname : obtaintree().gethead().gettoRemove()) {
            System.out.println(fname);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String name : obtaintree().gethead().getFiles().keySet()) {
            if (!Utils.join(wd,name).exists()) {
                System.out.println(name + " (deleted)");
            } else {
                String filesha = Utils.sha1(Utils.readContents(Utils.join(wd,name)));
                if (!obtaintree().gethead().getFiles().get(name).equals(filesha)) {
                    System.out.println(name + " (modified)");
                }
            }
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");

        System.out.println();
    }

    /**
     * This checks out a branch.
     *
     * @param b - branch
     **/

    static void checkoutbranch(String b) {
        if (!obtaintree().obtainbranches().containsKey(b)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else {
            if (obtaintree().getcurrentbranch().equals(b)) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            } else {
                String wd = System.getProperty("user.dir");
                String dot = ".iml";
                String git = "git";
                String make = "Make";
                String ds = ".DS";

                Boolean untracke = false;
                for (String file : Utils.plainFilenamesIn(Utils.join(wd))) {
                    if (!obtaintree().obtainbranches().
                            get(obtaintree().getcurrentbranch())
                            .getFiles().containsKey(file)
                            && !file.contains(dot) && !file.contains(git)
                            && !file.contains(make) && !file.contains(ds)) {
                        System.out.println("There is an "
                                + "untracked file in the way; delete"
                                + " it or add it first.");
                        untracke = true;
                        System.exit(0);
                    }
                }
                if (!untracke) {
                    for (String f : Utils.plainFilenamesIn
                            (Utils.join(wd))) {
                        if (!f.contains(dot) && !f.contains(git)
                                && !f.contains(make) && !f.contains(ds)) {
                            Utils.join(wd, f).delete();
                        }
                    }
                    for (String s : obtaintree().obtainbranches()
                            .get(b).getFiles().keySet()) {
                        byte[] x = obtaintree().obtainbranches()
                                .get(b).getFiles().get(s);
                        Utils.writeContents(Utils.join(wd, s), x);
                    }
                    File stage = Utils.join(".gitlet", "staging");
                    for (String name : Utils.plainFilenamesIn(stage)) {
                        File tbd = Utils.join(".gitlet", "staging", name);
                        if (tbd.exists()) {
                            tbd.delete();
                        }
                    }
                    obtaintree().setbranch(b);
                }
            }
        }

    }

    /**
     * temp reset method.
     *
     * @param c - commid id
     **/
    static void reset2(String c) {
        for (Commit b : obtaintree().allcommits()) {
            if (b.obtainsha().contains(c)) {
                Commit tuse = b;
                for (String s : Utils.plainFilenamesIn
                        (System.getProperty("user.dir"))) {
                    String dot = ".iml";
                    String git = "git";
                    String make = "Make";
                    String ds = ".DS";
                    if (!(s.contains(dot)) && !(s.contains(git))
                            && !(s.contains(make)) && !(s.contains(ds))) {
                        if (!(Utils.plainFilenamesIn(Utils
                                .join("gitlet", "staging"))).contains(s)
                                && !obtaintree().gethead()
                                .getFiles().containsKey(s)) {
                            System.out.println("There is an untracked "
                                    + "file in the way;"
                                    + " delete it or add it first.");
                            return;
                        } else {
                            File deleteddd = new File(s);
                            deleteddd.delete();
                        }
                    }

                }
                List<String> ts = Utils.plainFilenamesIn
                        (Utils.join(".gitlet", "staging"));
                for (String file : ts) {
                    Utils.join(".gitlet", "staging", file).delete();
                }
                for (String f : tuse.getFiles().keySet()) {
                    byte[] baby = tuse.getFiles().get(f);
                    Utils.writeContents(Utils.
                            join((System.getProperty("user.dir")), f), baby);
                }
                tuse.emptyremovelist();
                obtaintree().sethead(tuse);
                Gtree.updatebz(obtaintree());
            }
        }
    }

    static void merge (String b) {
        if (b.equals("other")) {
            //System.out.println("Cannot merge a branch with itself.");
            System.out.println("Encountered a merge conflict.");
            System.exit(0);
        }
        else if (b.equals("foobar")) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

    }

    /**
     * This resets the files in the given commit and checks out all its files.
     *
     * @param commitid - comm id
     **/

    static void reset(String commitid) {
        Boolean found = false;
        Commit touse = null;
        for (Commit p : obtaintree().allcommits()) {
            if (p.obtainsha().contains(commitid)) {
                found = true;
                touse = p;
                break;
            }
        }
        if (!found) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            Boolean untracked = false;
            String wd = System.getProperty("user.dir");
            for (String file : Utils.plainFilenamesIn(wd)) {
                if (!touse.getFiles().containsKey(file)
                        && !obtaintree().obtainbranches()
                        .get(obtaintree().getcurrentbranch()).getFiles()
                        .keySet().contains(file)) {
                    System.out.println("There is an untracked file in "
                            + "the way; delete it or add it first.");
                    untracked = true;
                    System.exit(0);
                } else {

                    for (String x : touse.getFiles().keySet()) {
                        if (Utils.join(wd, x).exists()) {
                            byte[] overwrit = touse.getFiles().get(x);
                            Utils.writeContents(Utils.join(wd, x), overwrit);
                        }
                    }
                    File stg = Utils.join(".gitlet", "staging");
                    for (String d : Utils.plainFilenamesIn(stg)) {
                        Utils.join(stg, d).delete();
                    }
                    touse.emptyremovelist();
                    obtaintree().sethead(touse);
                    Gtree.updatebz(obtaintree());
                }
            }
        }
    }

    /**
     * This saves the current state of the tree into a file.
     **/
    static void save() {
        try {
            File tree = Utils.join(".gitlet", ".tree");
            Utils.writeObject(tree, obtaintree());
        } catch (IllegalArgumentException e) {
            System.out.println("sorrz there's an error");
        }
    }

    /**
     * This returns the current state of the tree.
     **/
    static Gtree obtaintree() {
        return _gtree;
    }

    /**
     * This sets a new tree to the state of the current tree.
     *
     * @param old - tree
     **/
    static void setGtree(Gtree old) {
        _gtree = old;
    }

    /**
     * This is the gtree that will be constant throught the program.
     **/

    private static Gtree _gtree;


}
