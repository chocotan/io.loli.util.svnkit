package io.loli.docmanager.svnkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.loli.docmanager.svnkit.SVNKitUtil;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SVNKitUtilTest {
    private SVNKitUtil svnutil;

    @Before
    public void setUp() {
        svnutil = new SVNKitUtil("https://JIE-Z-PC:8443/svn/test",
                "D:\\\\Users\\ye\\Desktop\\work\\test", "wl-ye", "wl-ye");
    }

    @Test
    public void testcreateDirAndFile() {
        String dir = svnutil.getLocalBaseUrl() + "\\trunk\\"
                + new java.util.Date().getTime() + "\\";
        File file1 = new File(dir + "\\file1.txt");
        File file2 = new File(dir + "\\file2.txt");
        svnutil.createLocalDir(file1.getParentFile(),
                new File[] { file1, file2 }, new String[] { "file1", "file2" });
        assertTrue(file1.exists() && file2.exists());
    }

    @Test
    public void testAddAndCommit() throws SVNException {
        String dirstr = svnutil.getLocalBaseUrl() + "\\trunk\\";
        File dir = new File(dirstr);
        svnutil.addEntry(dir);
        assertEquals(svnutil.getRepository().getLatestRevision() + 1, svnutil
                .commit(dir, false, "test create dir").getNewRevision());
    }

    public void testMerge() throws SVNException {
        File dir = new File(svnutil.getLocalBaseUrl() + "\\trunk\\");
        svnutil.update(dir, SVNRevision.HEAD, true);
        svnutil.revertToRevision(dir, SVNRevision.create(2));
        assertEquals(svnutil.getRepository().getLatestRevision() + 1, svnutil
                .commit(dir, false, "back to first").getNewRevision());
    }

    @Test
    public void testHistory() {
        assertTrue(svnutil.getRevisionHistory("/trunk/1373419338189")
                .iterator().next().getRevision() > 0);
    }

    @Test
    public void testCopyHeadTo() throws SVNException {
        svnutil.copyHEADTo(svnutil.getUrl().appendPath("/branches/mill", false));
    }
    // @Test
    // public void testReadFile() {
    // try {
    // assertTrue(svnutil.readFile("pom.xml") != null);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // public void testMkDir() throws SVNException {
    // assertEquals(repo.getRepository().getLatestRevision()+1,
    // repo.makeDirectory(repo.getUrl().appendPath("/newtest", false),
    // "create test").getNewRevision());
    // }

    // public void testCommit() throws SVNException {
    // File file = new File(
    // "/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir");
    // // repo.addEntry(file);
    // assertEquals(svnutil.getRepository().getLatestRevision() + 1, svnutil
    // .commit(file, false, "test commit").getNewRevision());
    // }

    // @Test
    // public void testCheckOut() throws SVNException{
    // File file = new
    // File("/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir/");
    // repo.checkout(repo.getUrl().appendPath("/testdir", false),
    // SVNRevision.create(11), file, true);
    // repo.commit(file, false, "test");
    // }

    // public void testMerge() throws SVNException {
    // svnutil.revertToRevision(
    // new File(
    // "/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir/testfile"),
    // SVNRevision.create(15));
    // }

}
