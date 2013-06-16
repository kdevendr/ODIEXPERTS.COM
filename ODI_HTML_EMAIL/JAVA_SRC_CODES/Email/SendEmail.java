package Email;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

// Referenced classes of package Email:
//            HTMLstyle

public class SendEmail
{

    private static HtmlEmail email[];
    private static StringBuilder strBuild[];
    private static InternetAddress[] list ;

    public static void SendHTMLEmail(String HostName, int SMTPPort, Boolean ssl, Boolean tls, String UserName, String Password, String From, String TO, 
            String CC, String Subject, String Message, String CharacterSet, String Fileattachment1, String Fileattachment2, String Fileattachment3, 
            String Fileattachment4, String Fileattachment5, String Fileattachment6, String Fileattachment7, String Fileattachment8, String Fileattachment9, String Fileattachment10, 
            int sessNo)
        throws EmailException, AddressException
    {
        email = new HtmlEmail[sessNo + 1];
        email[sessNo] = new HtmlEmail();
        email[sessNo].setHostName(HostName);
        email[sessNo].setSmtpPort(SMTPPort);
        if(UserName.length() > 0 && Password.length() > 0)
        {
            email[sessNo].setAuthenticator(new DefaultAuthenticator(UserName, Password));
        }
        email[sessNo].setSSL(ssl.booleanValue());
        email[sessNo].setTLS(tls.booleanValue());
        email[sessNo].setFrom(From);
        email[sessNo].setSubject(Subject);
        email[sessNo].setHtmlMsg(Message);
        list = InternetAddress.parse(TO.toString());	
        email[sessNo].setTo(Arrays.asList(list));
        email[sessNo].setCharset(CharacterSet);
        if(CC.length() > 0)
        {
        	list = InternetAddress.parse(CC.toString());	
            email[sessNo].setCc(Arrays.asList(list));
            
        }
        if(Fileattachment1.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment1);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment2.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment2);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment3.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment3);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment4.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment4);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment5.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment5);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment6.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment6);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment7.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment7);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment8.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment8);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment9.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment9);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment10.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment10);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        email[sessNo].send();
    }

    public static void SendMultiPartHTMLEmailInitialize(String HostName, int SMTPPort, Boolean ssl, Boolean tls, String UserName, String Password, String From, String TO, 
            String CC, String Subject, String Message, String characterset, int sessNo)
        throws EmailException, AddressException
    {
        email = new HtmlEmail[sessNo + 1];
        email[sessNo] = new HtmlEmail();
        email[sessNo].setHostName(HostName);
        email[sessNo].setSmtpPort(SMTPPort);
        if(UserName.length() > 0 && Password.length() > 0)
        {
            email[sessNo].setAuthenticator(new DefaultAuthenticator(UserName, Password));
        }
        email[sessNo].setSSL(ssl.booleanValue());
        email[sessNo].setTLS(tls.booleanValue());
        email[sessNo].setFrom(From);
        email[sessNo].setSubject(Subject);
        strBuild = new StringBuilder[sessNo + 1];
        strBuild[sessNo] = new StringBuilder();
        strBuild[sessNo].append(Message).append("<br>");
        list = InternetAddress.parse(TO.toString());	
        email[sessNo].setTo(Arrays.asList(list));
        if(CC.length() > 0)
        {
        	list = InternetAddress.parse(CC.toString());	
            email[sessNo].setCc(Arrays.asList(list));

        }
    }

    public static void SendMultiPartHTMLEmailAddContent(String Message, int sessNo)
        throws EmailException
    {
        strBuild[sessNo].append(Message).append("<br>");
    }

    public static void SendMultiPartHTMLEmail(String Fileattachment1, String Fileattachment2, String Fileattachment3, String Fileattachment4, String Fileattachment5, String Fileattachment6, String Fileattachment7, String Fileattachment8, 
            String Fileattachment9, String Fileattachment10, int sessNo)
        throws EmailException
    {
        if(Fileattachment1.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment1);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment2.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment2);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment3.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment3);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment4.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment4);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment5.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment5);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment6.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment6);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment7.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment7);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment8.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment8);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment9.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment9);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment10.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment10);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        email[sessNo].setHtmlMsg(strBuild[sessNo].toString());
        email[sessNo].send();
    }

    public static void SendHTMLEmailSqlQuery(String HostName, int SMTPPort, Boolean ssl, Boolean tls, String UserName, String Password, String From, String TO, 
            String CC, String Subject, String HdrMessage, String Footer, String Characterset, String style, String Fileattachment1, 
            String Fileattachment2, String Fileattachment3, String Fileattachment4, String Fileattachment5, Connection conn, String Sql, int sessNo)
        throws EmailException, SQLException, AddressException
    {
        email = new HtmlEmail[sessNo + 1];
        email[sessNo] = new HtmlEmail();
        email[sessNo].setHostName(HostName);
        email[sessNo].setSmtpPort(SMTPPort);
        if(UserName.length() > 0 && Password.length() > 0)
        {
            email[sessNo].setAuthenticator(new DefaultAuthenticator(UserName, Password));
        }
        email[sessNo].setSSL(ssl.booleanValue());
        email[sessNo].setTLS(tls.booleanValue());
        email[sessNo].setFrom(From);
        email[sessNo].setSubject(Subject);
        list = InternetAddress.parse(TO.toString());	
        email[sessNo].setTo(Arrays.asList(list));
        email[sessNo].setCharset(Characterset);
        StringBuilder build = new StringBuilder();
        build.append(HdrMessage);
        if(style.equals("STYLE1"))
        {
            build.append(HTMLstyle.style1(conn, Sql));
        } else
        if(style.equals("STYLE2"))
        {
            build.append(HTMLstyle.style2(conn, Sql));
        } else
        if(style.equals("STYLE3"))
        {
            build.append(HTMLstyle.style3(conn, Sql));
        } else
        if(style.equals("STYLE4"))
        {
            build.append(HTMLstyle.style4(conn, Sql));
        } else
        if(style.equals("STYLE5"))
        {
            build.append(HTMLstyle.style5(conn, Sql));
        } else
        {
            build.append(HTMLstyle.Defaultstyle(conn, Sql));
        }
        build.append(Footer);
        email[sessNo].setHtmlMsg(build.toString());
        if(CC.length() > 0)
        {
        	list = InternetAddress.parse(CC.toString());	
            email[sessNo].setCc(Arrays.asList(list));
        }
        if(Fileattachment1.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment1);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment2.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment2);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment3.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment3);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment4.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment4);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        if(Fileattachment5.length() > 0)
        {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(Fileattachment5);
            attachment.setDisposition("attachment");
            email[sessNo].attach(attachment);
        }
        email[sessNo].send();
    }
}
