package com.example.pokecenter.admin.Quan.AdminTab.Utils;

import com.example.pokecenter.admin.Quan.AdminTab.Model.MessageSender.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DateUtils {
    public static Message getLatestMessage(ArrayList<Message> messageList) {
        if (messageList.isEmpty()) {
            // Return null if the messageList is empty
            return null;
        }

        // Start with the first message as the latest
        Message latestMessage = messageList.get(0);

        // Create a DateTimeFormatter for date and time comparison
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss");

        // Iterate over the remaining messages
        for (int i = 1; i < messageList.size(); i++) {
            Message currentMessage = messageList.get(i);

            // Parse the dates of the current message and the latest message
            LocalDateTime currentDateTime = LocalDateTime.parse(currentMessage.getSentTime(), formatter);
            LocalDateTime latestDateTime = LocalDateTime.parse(latestMessage.getSentTime(), formatter);

            // Compare the dates of the current message and the latest message
            if (currentDateTime.isAfter(latestDateTime)) {
                // If the current message's date is later, update the latestMessage
                latestMessage = currentMessage;
            }
        }
        // Return the latest message
        return latestMessage;
    }

    public static String formatMessageDateTime(String dateTimeString) {
        // Create a DateTimeFormatter for the given date and time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm:ss");

        // Parse the given date and time string
        LocalDateTime messageDateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Get the date parts of the message and current date
        LocalDate messageDate = messageDateTime.toLocalDate();
        LocalDate currentDate = currentDateTime.toLocalDate();

        // Calculate the difference in days
        long daysDiff = currentDate.toEpochDay() - messageDate.toEpochDay();

        if (daysDiff == 0) {
            // Same day, display only the time
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return messageDateTime.format(timeFormatter);
        } else if (daysDiff == 1) {
            // Yesterday, display "yesterday," + time
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("'yesterday,' HH:mm:ss");
            return messageDateTime.format(timeFormatter);
        } else if (daysDiff >= 2 && daysDiff <= 6) {
            // 2 to 6 days ago, display "X days ago"
            return daysDiff + " days ago";
        } else if (daysDiff >= 7 && daysDiff <= 30) {
            // 1 week to 1 month ago, display "X weeks ago"
            long weeksDiff = daysDiff / 7;
            return weeksDiff + " weeks ago";
        } else if (daysDiff > 30) {
            // More than 1 month ago, display "X months ago"
            long monthsDiff = daysDiff / 30;
            return monthsDiff + " months ago";
        } else {
            // Future date (shouldn't happen, but handle it just in case)
            return "Invalid date";
        }
    }
}
