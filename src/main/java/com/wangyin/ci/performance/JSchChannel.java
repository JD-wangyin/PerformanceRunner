package com.wangyin.ci.performance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

/**
 * 
 * 
 * @author wyhubingyin
 * @date 2014年7月25日
 */
public class JSchChannel {
    private final Logger LOG = Logger.getLogger(JSchChannel.class);
    private String host;
    private String user;
    private String password;
    private int port;
    private int maxWaitTime;
    private String keyfile;
    private String passphrase;
    private boolean sshKey;
    private ChannelSftp sftp;
    private Session session;

    public JSchChannel(String host, String user, String password, int port, int maxWaitTime) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.maxWaitTime = maxWaitTime;
        this.keyfile = null;
        this.passphrase = null;
        this.sshKey = false;
    }

    public JSchChannel(String host, String user, int port, int maxWaitTime, String keyfile, String passphrase) {
        this.host = host;
        this.user = user;
        this.password = null;
        this.port = port;
        this.maxWaitTime = maxWaitTime;
        this.keyfile = keyfile;
        this.passphrase = passphrase;
        this.sshKey = true;
    }

    public void open() throws JSchException {
        JSch client = new JSch();
        if (sshKey && keyfile != null && keyfile.length() > 0) {
            client.addIdentity(this.keyfile, this.passphrase);
        }
        session = client.getSession(this.user, this.host, this.port);
        session.setUserInfo(new UserInfo() {

            public String getPassphrase() {
                return null;
            }

            public String getPassword() {
                return password;
            }

            public boolean promptPassphrase(String arg0) {
                return true;
            }

            public boolean promptPassword(String arg0) {
                return true;
            }

            public boolean promptYesNo(String arg0) {
                return true;
            }

            public void showMessage(String arg0) {
            }
        });
        session.setTimeout(maxWaitTime);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;
    }

    public String exec(String command) throws JSchException {
        JSch client = new JSch();
        if (sshKey && keyfile != null && keyfile.length() > 0) {
            client.addIdentity(this.keyfile, this.passphrase);
        }
        session = client.getSession(this.user, this.host, this.port);
        Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setUserInfo(new UserInfo() {

            public String getPassphrase() {
                return null;
            }

            public String getPassword() {
                return password;
            }

            public boolean promptPassphrase(String arg0) {
                return true;
            }

            public boolean promptPassword(String arg0) {
                return true;
            }

            public boolean promptYesNo(String arg0) {
                return true;
            }

            public void showMessage(String arg0) {
            }
        });
        session.setTimeout(maxWaitTime);
        session.connect();

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        channel.setInputStream(null);

        ((ChannelExec) channel).setErrStream(System.err);

        InputStream in = null;
        try {
            in = channel.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        channel.connect();
        StringBuilder sBuilder=new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            try {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    sBuilder.append(new String(tmp, 0, i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (channel.isClosed()) {
                try {
                    if (in.available() > 0)
                        continue;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        channel.disconnect();
        session.disconnect();
        return sBuilder.toString();
    }

    public void close() {
        if (sftp != null) {
            sftp.disconnect();
            sftp = null;
        }
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    public void cd(String path) throws SftpException {
        sftp.cd(path);
    }

    public void pwd() throws SftpException {
        LOG.info(sftp.pwd());
    }

    public void ls() throws SftpException {
        Vector<?> vector = sftp.ls(".");
        for (Object object : vector) {
            if (object instanceof LsEntry) {
                LsEntry entry = LsEntry.class.cast(object);
                LOG.info(entry.getFilename());
            }
        }
    }

    public void ls(String path) throws SftpException {
        Vector<?> vector = sftp.ls(path);
        for (Object object : vector) {
            if (object instanceof LsEntry) {
                LsEntry entry = LsEntry.class.cast(object);
                LOG.info(entry.getFilename());
            }
        }
    }

    public void rename(String oldPath, String newPath) throws SftpException {
        sftp.rename(oldPath, newPath);
    }

    public void cp(String src, String dest) throws SftpException, JSchException, IOException {
    	 JSch client = new JSch();
         if (sshKey && keyfile != null && keyfile.length() > 0) {
             client.addIdentity(this.keyfile, this.passphrase);
         }
         session = client.getSession(this.user, this.host, this.port);
         Properties config = new java.util.Properties();
         config.put("StrictHostKeyChecking", "no");
         session.setConfig(config);
         session.setUserInfo(new UserInfo() {

             public String getPassphrase() {
                 return null;
             }

             public String getPassword() {
                 return password;
             }

             public boolean promptPassphrase(String arg0) {
                 return true;
             }

             public boolean promptPassword(String arg0) {
                 return true;
             }

             public boolean promptYesNo(String arg0) {
                 return true;
             }

             public void showMessage(String arg0) {
             }
         });
         session.setTimeout(maxWaitTime);
         session.connect();

          sftp= (ChannelSftp) session.openChannel("sftp");
          sftp.connect();
         InputStream instream= sftp.get(src);
         OutputStream outstream = new FileOutputStream(new File(dest));


         byte b[] = new byte[1024]; 
         int n; 


          while ((n = instream.read(b)) != -1) { 


              outstream.write(b, 0, n); 


          } 
          outstream.flush();
          outstream.close();
          instream.close();
    }
    
    
    public InputStream get(String src) throws SftpException, JSchException, IOException {
   	 JSch client = new JSch();
        if (sshKey && keyfile != null && keyfile.length() > 0) {
            client.addIdentity(this.keyfile, this.passphrase);
        }
        session = client.getSession(this.user, this.host, this.port);
        Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setUserInfo(new UserInfo() {

            public String getPassphrase() {
                return null;
            }

            public String getPassword() {
                return password;
            }

            public boolean promptPassphrase(String arg0) {
                return true;
            }

            public boolean promptPassword(String arg0) {
                return true;
            }

            public boolean promptYesNo(String arg0) {
                return true;
            }

            public void showMessage(String arg0) {
            }
        });
        session.setTimeout(maxWaitTime);
        session.connect();

         sftp= (ChannelSftp) session.openChannel("sftp");
         sftp.connect();
        InputStream instream= sftp.get(src);
         return instream;
   }
    
    public void upload (String dst,InputStream src) throws SftpException, JSchException, IOException {
      	 JSch client = new JSch();
           if (sshKey && keyfile != null && keyfile.length() > 0) {
               client.addIdentity(this.keyfile, this.passphrase);
           }
           session = client.getSession(this.user, this.host, this.port);
           Properties config = new java.util.Properties();
           config.put("StrictHostKeyChecking", "no");
           session.setConfig(config);
           session.setUserInfo(new UserInfo() {

               public String getPassphrase() {
                   return null;
               }

               public String getPassword() {
                   return password;
               }

               public boolean promptPassphrase(String arg0) {
                   return true;
               }

               public boolean promptPassword(String arg0) {
                   return true;
               }

               public boolean promptYesNo(String arg0) {
                   return true;
               }

               public void showMessage(String arg0) {
               }
           });
           session.setTimeout(maxWaitTime);
           session.connect();

            sftp= (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
           sftp.put(src, dst);
      }

    public void rm(String file) throws SftpException {
        sftp.rm(file);
    }

    public void mv(String src, String dest) throws SftpException {
        rename(src, dest);
    }

    public void rmdir(String path) throws SftpException {
        sftp.rmdir(path);
    }

    public void chmod(int permissions, String path) throws SftpException {
        sftp.chmod(permissions, path);
    }
    

   
}
