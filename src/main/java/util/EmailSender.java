package util;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Envío de correos con adjunto (ticket en .txt)
 */
public class EmailSender {

    public static void sendTicketByEmail(String toEmail, String filePath) {

        final String fromEmail = "tucorreo@gmail.com";   // correo del sistema
        final String password = "CONTRASEÑA_DE_APP";     // contraseña de aplicación

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject("🎟 Ticket de Parqueo");

            // Texto del correo
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(
                    "Hola,\n\n"
                    + "Adjunto encontrarás tu ticket de parqueo.\n\n"
                    + "Gracias por utilizar nuestro servicio."
            );

            // Adjunto (.txt)
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(filePath));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("📧 Correo enviado correctamente a " + toEmail);

        } catch (Exception e) {
            System.out.println("❌ Error enviando correo");
            e.printStackTrace();
        }
    }
}
