package io.loli.util.svnkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SVNKitServiceTest {
    private SVNKitService ss;

    @Before
    public void setUp() throws Exception {
        ss = new SVNKitService();
    }

    @Test
    public void testAddFile() {
        File file = new File(ss.getLocalPath() + File.separator + "trunk"
                + File.separator + "test" + File.separator
                + new java.util.Date().getTime() + File.separator + "test.txt");
        file.getParentFile().mkdirs();
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write("rontech");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        assertEquals(ss.getLatestRevision() + 1, ss.addAndCommit("trunk")
                .getNewRevision());
    }

    @Test
    public void testEditFile() throws IOException {
        File file = new File(ss.getLocalPath() + File.separator + "trunk"
                + File.separator + "test" + File.separator
                + new java.util.Date().getTime() + File.separator + "test.txt");
        file.getParentFile().mkdirs();
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write("rontech");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assertEquals(ss.getLatestRevision() + 1, ss.addAndCommit("trunk")
                .getNewRevision());
        try {
            fw = new FileWriter(file);
            fw.write("rontech\r\nthe second line");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        assertEquals(
                ss.getLatestRevision() + 1,
                ss.commit(
                        "trunk" + File.separator + "test" + File.separator
                                + file.getParentFile().getName(),
                        "edit " + file.getAbsolutePath()).getNewRevision());
    }

    @Test
    public void testListEntries() {
        assertTrue(ss.listEntries("trunk").size() > 0);
    }

    @Test
    public void testIsTxtFile() {
        File file = new File(ss.getLocalPath() + "trunk" + File.separator
                + "test" + File.separator + "image.jpg");
        int width = 100;
        int height = 100;
        String s = "你好";
        Font font = new Font("Serif", Font.BOLD, 10);
        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, width, height);
        g2.setPaint(Color.RED);
        FontRenderContext context = g2.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(s, context);
        double x = (width - bounds.getWidth()) / 2;
        double y = (height - bounds.getHeight()) / 2;
        double ascent = -bounds.getY();
        double baseY = y + ascent;
        g2.drawString(s, (int) x, (int) baseY);
        try {
            ImageIO.write(bi, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(!ss.isTxtFile("trunk" + File.separator + "test"
                + File.separator + file.getName()));
        file.delete();
    }

    @Test
    public void testReadFile() {
        File file = new File(ss.getLocalPath() + File.separator + "trunk"
                + File.separator + "test" + File.separator + "test.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write("rontech");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ss.addAndCommit("trunk");
        assertEquals("rontech", ss.readFile("trunk/test/test.txt"));
    }

    @Test
    // testUpdateToHEAD
    public void testMkDirInRepo() {
        String path = "trunk/test/testbaka";
        ss.mkDirInRepo(path);
        ss.updateToHEAD("");
        File file = new File(ss.getLocalPath() + File.separator + path);
        assertTrue(file.exists() && file.isDirectory());
        file.delete();
    }

    @Test
    public void testMkDirInLocalAndCommit() {
        String path = "trunk" + File.separator + "testye";
        ss.mkDirInLocalAndCommit(path);
        assertEquals(0, ss.listEntries("trunk/testye").size());
        File file = new File(path);
        file.delete();
        ss.addAndCommit("trunk");
        assertTrue(!file.exists());
    }

    @Test
    public void testCommit() {
        this.testAddFile();
    }

    @Ignore
    @Test
    public void testCheckout() {
    //TODO testCheckout
    }

    @Test
    public void testUpdateToHEAD() {
        this.testMkDirInRepo();
    }

    @Test
    public void testDeleteFile() {
        assertEquals(ss.getLatestRevision() + 1,
                ss.deleteFile("trunk" + File.separator + "test")
                        .getNewRevision());
    }

    @Test
    public void testAddEntry() {
        this.testAddFile();
    }

    @Test
    public void testAddAndCommit() {
        this.testAddFile();
    }

    @Test
    public void testRevertToRevision() {
        ss.updateToHEAD("");
        assertEquals(ss.getLatestRevision() + 1, ss
                .revertToRevision("trunk", 1).getNewRevision());
    }

    @Test
    public void testGetRevisionHistory() {
        assertTrue(ss.getRevisionHistory("trunk").size() > 0);
    }

    @Test
    public void testCopyHEADTo() {
        ss.copyHEADTo("tag/" + "test");
        assertTrue(ss.listEntries("tag/test").size() > 0);
    }
}
