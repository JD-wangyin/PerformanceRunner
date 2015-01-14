package com.wangyin.ci.performance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP 操作帮助类， 代码来自互联网
 * 
 * @author wyliangxiaowu
 * @date 2014年7月24日
 */
public class FtpUtil {
	// 创建ftp客户端
    private FTPClient ftp;

    /**
     * connectServer 主要进行初始化配置连接FTP服务器。
     * 
     * @param host
     *            FTP服务器的IP地址
     * @param port
     *            FTP服务器的端口
     * @param userName
     *            FTP服务器的用户名
     * @param passWord
     *            FTP服务器的密码
     */
    public void connectServer(String host, int port, String userName, String passWord) {
        ftp = new FTPClient();
        try {
            ftp.connect(host, port);// 连接FTP服务器
            ftp.login(userName, passWord);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                System.out.println("未连接到FTP，用户名或密码错误。");
                ftp.disconnect();
            } else {
                System.out.println("FTP连接成功。");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FTP的端口错误,请正确配置。");
        }
    }

    /**
     * 上传文件到FTP服务器
     * 
     * @param local
     *            本地文件名称，绝对路径
     * @param remote
     *            远程文件路径,支持多级目录嵌套，支持递归创建不存在的目录结构
     * @throws IOException
     */
    public void upload(String local, String remote) throws IOException {
        // 设置PassiveMode传输
        ftp.enterLocalPassiveMode();
        // 设置以二进制流的方式传输
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        // 对远程目录的处理
        String remoteFileName = remote;
        if (remote.contains("/")) {
            remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
            // 创建服务器远程目录结构，创建失败直接返回
            if (!createDirecroty(remote)) {
                return;
            }
        }
       // FTPFile[] files = ftp.listFiles(new String(remoteFileName));
        File f = new File(local);
        uploadFile(remoteFileName, f);
    }
    
    /**
     * 上传单个文件到FTP
     * 
     * @param remoteFile
     * @param localFile
     * @throws IOException
     */
    public void uploadFile(String remoteFile, File localFile) throws IOException {
        InputStream in = new FileInputStream(localFile);
        ftp.storeFile(remoteFile, in);
        in.close();
    }

    /**
     * 递归创建远程服务器目录
     * 
     * @param remote
     *            远程服务器文件绝对路径
     * 
     * @return 目录创建是否成功
     * @throws IOException
     */
    public boolean createDirecroty(String remote) throws IOException {
        boolean success = true;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !ftp.changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(remote.substring(start, end));
                if (!ftp.changeWorkingDirectory(subDirectory)) {
                    if (ftp.makeDirectory(subDirectory)) {
                        ftp.changeWorkingDirectory(subDirectory);
                    } else {
                        System.out.println("创建目录失败");
                        success = false;
                        return success;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }
    
    /**
     * 上传整个文件夹下的所有文件到FTP
     * 
     * @param filename
     * @param uploadpath
     * @return
     * @throws Exception
     */
    public boolean uploadAll(String filename, String uploadpath) throws Exception {
        boolean success = false;
        File file = new File(filename);
        // 要上传的是否存在
        if (!file.exists()) {
            return success;
        }
        // 要上传的是否是文件夹
        if (!file.isDirectory()) {
            return success;
        }
        File[] flles = file.listFiles();
        for (File files : flles) {
            if (files.exists()) {
                if (files.isDirectory()) {
                    this.uploadAll(files.getAbsoluteFile().toString(), uploadpath);
                } else {
                    String local = files.getCanonicalPath().replaceAll("\\\\", "/");
                    String remote = uploadpath + local.substring(local.indexOf("/") + 1);
                    upload(local, remote);
                    ftp.changeWorkingDirectory("/");
                }
            }
        }
        return true;
    }

    /**
     * disconnectServer 断开与ftp服务器的链接
     * 
     * @throws java.io.IOException
     */
    public void disconnectServer() throws IOException {
        try {
            if (ftp != null) {
                ftp.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 测试
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FtpUtil ftp = new FtpUtil();
        ftp.connectServer("172.24.2.21", 21, "ftp", "Wangyin@2014");
       // ftp.upload("D:/test.txt", "/bb/aa/");
        ftp.uploadAll("D:/test/", "/tmp/");
        //ftp.uploadFile("/ptp", "test.txt", new FileInputStream(new File("D:/test/2req.log")));
        ftp.disconnectServer();
    }

}
