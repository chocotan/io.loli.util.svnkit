package io.loli.util.svnkit;

import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * SVNKitService类, 封装util
 * 
 * @author ye
 * 
 */
public class SVNKitService {
    private SVNURL url;
    private String localPath;
    private SVNKitUtil util;

    public SVNRepository getRepository() {
        return util.getRepository();
    }
    public long getLatestRevision(){
        long revision = 0;
        try {
            revision = this.getRepository().getLatestRevision();
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return revision;
    }

    public SVNURL getUrl() {
        return url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public SVNKitUtil getUtil() {
        return util;
    }

    public SVNKitService() {
        // 从配置文件中读取信息
        ResourceBundle rb = ResourceBundle.getBundle(
                "svn", Locale.getDefault());
        String localPath = rb.getString("svn.localpath");
        String svnUrl = rb.getString("svn.svnurl");
        String username = rb.getString("svn.username");
        String password = rb.getString("svn.password");
        this.localPath = localPath;
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
        SVNRepository repository = null;
        try {
            url = SVNURL.parseURIEncoded(svnUrl);
            repository = SVNRepositoryFactory.create(url, null);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        ISVNAuthenticationManager authManager = new BasicAuthenticationManager(
                username, password);
        repository.setAuthenticationManager(authManager);
        util = new SVNKitUtil(url, repository, localPath, username, password);
    }

    /**
     * 获取指定目录下的所有文件(夹)列表(无递归)
     * 
     * @param path
     * @return 文件列表
     */
    public Collection<SVNDirEntry> listEntries(String path) {
        Collection<SVNDirEntry> collection = null;
        try {
            collection = util.listEntries(path);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return collection;
    }

    /**
     * 判断一个文件是否是txt文本文件
     * 
     * @param path
     *            此文件的url
     * @return 是否是文本wenjian
     */
    public boolean isTxtFile(String path) {
        try {
            return util.isTxtFile(path);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取一个文件
     * 
     * @param filePath
     *            文件路径
     * @return 此文件的内容
     */
    public String readFile(String filePath) {
        String result = null;
        try {
            result = util.readFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 在远程仓库里直接创建文件夹
     * 
     * @param path
     *            需要创建的文件夹的路径
     * @return 此次提交的信息
     */
    public SVNCommitInfo mkDirInRepo(String path) {
        SVNCommitInfo sci = null;
        try {
            sci = util.makeDirectory(util.getUrl().appendPath(path, false),
                    "建立文件夹" + path);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return sci;
    }

    /**
     * 在本地新建文件夹并提交
     * 
     * @param path
     *            需要新建的文件夹的路径
     * @return 此次提交的信息
     */
    public SVNCommitInfo mkDirInLocalAndCommit(String path) {
        File file = new File(util.getLocalBaseUrl() + File.separator + path);
        util.createLocalDir(new File(util.getLocalBaseUrl() + File.separator
                + path));
        SVNCommitInfo sci = null;
        try {
            util.addEntry(file);
            sci = util.commit(file, false, "建立文件夹" + path);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return sci;
    }

    /**
     * 把某个目录下的所有修改都提交到服务器里
     * 
     * @param path
     *            需要提交的目录
     * @param commitMessage
     *            消息
     * @return 此次提交的信息
     */
    public SVNCommitInfo commit(String path, String commitMessage) {
        SVNCommitInfo sci = null;
        File file = new File(localPath + File.separator + path);
        try {
            sci = util.commit(file, false, commitMessage);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return sci;
    }

    /**
     * 把某个文件checkout到指定版本
     * 
     * @param revision
     *            版本
     * @param path
     *            指定的文件/文件夹
     * @return
     */
    public long checkout(SVNRevision revision, String path) {
        long result = 0L;
        File file = new File(util.getLocalBaseUrl() + File.separator + path);
        try {
            result = util.checkout(url, revision, file, true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 把指定文件更新到最新版本
     * 
     * @param path
     *            文件路径
     */
    public void updateToHEAD(String path) {
        File file = new File(util.getLocalBaseUrl() + File.separator + path);
        try {
            util.update(file, SVNRevision.HEAD, true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除某个文件并提交到服务器
     * 
     * @param path
     *            需要删除的文件路径
     */
    public SVNCommitInfo deleteFile(String path) {
        File file = new File(util.getLocalBaseUrl() + File.separator + path);
        SVNCommitInfo sci = null;
        try {
            util.delete(file, true);
            sci = this.commit(path, "删除" + path);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return sci;
    }

    /**
     * 把某个文件/文件夹添加到版本库
     * 
     * @param path
     */
    public void addEntry(String path) {
        File file = new File(util.getLocalBaseUrl() + File.separator + path);
        try {
            util.addEntry(file);
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把某个文件/文件夹添加到版本库并提交到服务器
     * 
     * @param path
     * @return 此次提交的信息
     */
    public SVNCommitInfo addAndCommit(String path) {
        this.addEntry(path);
        return this.commit(path, "添加" + path);
    }

    /**
     * 把某个文件/文件夹退回到指定版本并提交
     * 
     * @param path
     *            需要退回的文件路径
     * @param revision
     *            指定的版本呢
     * @return 此次提交的信息
     */
    public SVNCommitInfo revertToRevision(String path, long revision) {
        File file = new File(util.getLocalBaseUrl() + File.separator + path);
        SVNCommitInfo sci = null;
        try {
            util.revertToRevision(file, SVNRevision.create(revision));
            sci = this.commit(path, "恢复到" + revision + "版本");
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return sci;
    }

    /**
     * 获取指定文件/文件夹的版本历史
     * 
     * @param path
     * @return 历史集合
     */
    public Collection<SVNLogEntry> getRevisionHistory(String path) {
        return util.getRevisionHistory(path);
    }

    public SVNCommitInfo copyHEADTo(String addtionalURL) {
        SVNCommitInfo sci = null;
        try {
            sci = util
                    .copyHEADTo(util.getUrl().appendPath(addtionalURL, false));
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return sci;
    }
    
    public SVNCommitInfo importDirectory(File localPath,
            String commitMessage){
        SVNCommitInfo sci = null;
        try {
            sci = util.importDirectory(localPath, url, "first import", true);
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return sci;
    }
    
    public void clean(String path){
        File file = new File(util.getLocalBaseUrl() + File.separator + path);
        util.clean(file);
    }
}
