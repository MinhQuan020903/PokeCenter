package com.example.pokecenter.admin.AdminTab.Utils;

import android.util.Log;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailUtils {
    public static void sendResponseEmailToCustomer
            (
             String receiverEmail,
             String currentEmail,
             String currentPassword,
             Boolean isApproved
    ) {
        try {
            Properties properties = new Properties();

            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.starttls.enable", true);
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", 587);

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(currentEmail, currentPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            mimeMessage.setSubject("PokéCenter's Response for Customer Product Request");

            String content = "";
            if (isApproved) {
                content = "Dear " + receiverEmail.replace(",", ".") +  ", \n"
                        + "We are pleased to inform you that your product request has been approved!"
                        + " We appreciate your interest in our store and your choice of products."
                        + " Our team has carefully reviewed your request, and we are happy to fulfill it.\n"
                        + "Thank you for your patronage.\n"
                        +"PokéCenter.";
            } else {
                content = "Dear " + receiverEmail.replace(",", ".") + ",\n"
                        + "We appreciate your interest in our store and your choice of products. "
                        + "But unfortunately, we cannot approve your request.\n"
                        + "If you have any further inquiries or need assistance, please feel free to contact us.\n"
                        + "Thank you for your understanding.\n"
                        + "PokéCenter.";
            }
            mimeMessage.setText(content);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
