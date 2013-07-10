package io.loli.docmanager.svnkit;

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
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

public class SVNKitServiceTest {
    private SVNKitService ss;

    @Before
    public void setUp() {
        ss = new SVNKitService();
    }

    @Test
    public void testAddFile() throws IOException, SVNException {
        File file = new File(ss.getLocalPath() + File.separator + "trunk"
                + File.separator + "test" + File.separator
                + new java.util.Date().getTime() + File.separator + "test.txt");
        file.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(file);
        fw.write("rontech");
        fw.close();
        assertEquals(ss.getRepository().getLatestRevision() + 1, ss
                .addAndCommit("trunk").getNewRevision());
    }

    @Test
    public void testEditFile() throws SVNException, IOException {
        File file = new File(ss.getLocalPath() + File.separator + "trunk"
                + File.separator + "test" + File.separator
                + new java.util.Date().getTime() + File.separator + "test.txt");
        file.getParentFile().mkdirs();
        FileWriter fw = new FileWriter(file);
        fw.write("rontech");
        fw.close();
        assertEquals(ss.getRepository().getLatestRevision() + 1, ss
                .addAndCommit("trunk").getNewRevision());
        fw = new FileWriter(file);
        fw.write("rontech\r\nthe second line");
        fw.close();
        assertEquals(
                ss.getRepository().getLatestRevision() + 1,
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
    public void testIsTxtFile() throws IOException {
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
        ImageIO.write(bi, "jpg", file);
        assertTrue(!ss.isTxtFile("trunk" + File.separator + "test"
                + File.separator + file.getName()));
        file.delete();
    }

    @Test
    public void testReadFile() throws IOException {
        File file = new File(ss.getLocalPath() + File.separator + "trunk"
                + File.separator + "test" + File.separator + "test.txt");
        FileWriter fw = new FileWriter(file);
        fw.write("rontech");
        fw.close();
        ss.addAndCommit("trunk");
        assertEquals(
                "rontech",
                ss.readFile("trunk/test/test.txt"));
    }

    @Test
    public void testRevert() throws SVNException {
        ss.updateToHEAD("");
        assertEquals(ss.getRepository().getLatestRevision() + 1, ss
                .revertToRevision("trunk", 1).getNewRevision());
    }

    @Test
    public void deleteFile() throws SVNException {
        assertEquals(ss.getRepository().getLatestRevision() + 1, ss
                .deleteFileAndCommit("trunk" + File.separator + "test")
                .getNewRevision());
    }
}