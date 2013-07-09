package io.loli.docmanager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.loli.docmanager.tools.SVNKitUtil;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SVNKitToolTest {
    private SVNKitUtil repo;

    @Before
    public void setUp() {
        repo = new SVNKitUtil("svn://localhost/io.loli.docmanager",
                "choco", "choco");
    }

    @Test
    public void testReadFile() {
        try {
            assertTrue(repo.readFile("pom.xml") != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void testMkDir() throws SVNException {
        assertEquals(repo.getRepository().getLatestRevision()+1, repo.makeDirectory(repo.getUrl().appendPath("/newtest", false), "create test").getNewRevision());
    }
    
    
    public void testcreateDirAndFile(){
        File file = new File("/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir/testfile");
        String content="this is test1";
        repo.createLocalDir(new File("/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir"), new File[]{file}, new String[]{content});
        assertTrue(file.exists());
    }
    
    
    public void testCommit() throws SVNException{
        File file = new File("/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir");
    //    repo.addEntry(file);
        assertEquals(repo.getRepository().getLatestRevision()+1,repo.commit(file, false, "test commit").getNewRevision());
    }
    
//    @Test
//    public void testCheckOut() throws SVNException{
//        File file = new File("/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir/");
//        repo.checkout(repo.getUrl().appendPath("/testdir", false), SVNRevision.create(11), file, true);
//        repo.commit(file, false, "test");
//    }
    
    @Test
    public void testMerge() throws SVNException {
        repo.revertToRevision(new File("/home/choco/文档/代码/java/localhost/io.loli.docmanager/testdir/testfile"), SVNRevision.create(15));
    }
    
    
}
